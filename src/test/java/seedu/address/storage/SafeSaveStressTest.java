package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.util.JsonUtil;
import seedu.address.logic.LogicManager;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.employee.Employee;
import seedu.address.testutil.EmployeeBuilder;

/** Regression and stress tests for the complete command-to-file save boundary. */
public class SafeSaveStressTest {
    private static final List<String> COMMANDS = List.of(
            "add n/Charlie p/81234567 e/charlie@example.com a/Main Street le/14",
            "edit 1 n/Changed p/82345678", "salary 1 s/5000", "delete 1", "clear",
            "task add 1 d/New task", "task delete 1 i/1", "task mark 1 i/1", "task unmark 1 i/2",
            "leave add 1 from/12-10-2026 to/13-10-2026", "leave delete 1", "leave list",
            "find Bob", "filter t/engineering", "sort n/des", "list", "task list 1",
            "summary", "/summary scope/all", "help", "exit");
    private static final List<String> VIEWS = List.of("list", "sort n/des", "find Alice");
    private static final List<String> FAILURES = List.of("partial", "write", "denied");

    @TempDir
    public Path folder;

    @Test
    public void execute_failureMatrix_restoresStateAndAllowsRetry() throws Exception {
        for (String view : VIEWS) {
            for (String command : COMMANDS) {
                for (String failure : FAILURES) {
                    Fixture fixture = new Fixture();
                    fixture.logic.execute(view);
                    AddressBook before = new AddressBook(fixture.model.getAddressBook());
                    var observable = fixture.model.getFilteredEmployeeList();
                    List<Employee> displayed = List.copyOf(observable);
                    byte[] bytes = Files.readAllBytes(fixture.path);
                    fixture.storage.failure = failure;
                    String context = view + "; " + command + "; " + failure;

                    CommandException error = assertThrows(CommandException.class, () ->
                            fixture.logic.execute(command), context);

                    assertSame(fixture.storage.lastFailure, error.getCause(), context);
                    assertEquals(before, fixture.model.getAddressBook(), context);
                    assertSame(observable, fixture.model.getFilteredEmployeeList(), context);
                    assertEquals(displayed, observable, context);
                    if (!failure.equals("partial")) {
                        assertArrayEquals(bytes, Files.readAllBytes(fixture.path), context);
                    }

                    // Retrying must behave exactly like executing the command once on the original state.
                    ModelManager expected = new ModelManager(before, new UserPrefs());
                    AddressBookParser parser = new AddressBookParser();
                    parser.parseCommand(view).execute(expected);
                    CommandResult expectedResult = parser.parseCommand(command).execute(expected);
                    fixture.storage.failure = "";
                    assertEquals(expectedResult, fixture.logic.execute(command), context);
                    assertEquals(expected.getAddressBook(), fixture.model.getAddressBook(), context);
                    assertEquals(expected.getFilteredEmployeeList(), fixture.model.getFilteredEmployeeList(), context);
                    assertEquals(expected.getAddressBook(), fixture.storage.readAddressBook().orElseThrow(), context);
                }
            }
        }
    }

    @Test
    public void execute_successMatrix_matchesOriginalCommandAndJsonBehavior() throws Exception {
        AddressBookParser parser = new AddressBookParser();
        for (String view : VIEWS) {
            for (String command : COMMANDS) {
                Fixture fixture = new Fixture();
                ModelManager original = new ModelManager(fixture.model.getAddressBook(), new UserPrefs());
                parser.parseCommand(view).execute(original);
                fixture.logic.execute(view);

                // The original logic parsed and executed the command, then serialized the model directly.
                CommandResult expected = parser.parseCommand(command).execute(original);
                Path originalFile = folder.resolve("original.json");
                JsonUtil.saveJsonFile(new JsonSerializableAddressBook(original.getAddressBook()), originalFile);
                CommandResult actual = fixture.logic.execute(command);

                assertEquals(expected, actual, view + "; " + command);
                assertEquals(original.getAddressBook(), fixture.model.getAddressBook(), command);
                assertEquals(original.getFilteredEmployeeList(), fixture.model.getFilteredEmployeeList(), command);
                assertArrayEquals(Files.readAllBytes(originalFile), Files.readAllBytes(fixture.path), command);
                assertEquals(original.getAddressBook(), fixture.storage.readAddressBook().orElseThrow(), command);
            }
        }
    }

    @Test
    public void execute_repeatedMixedFailuresAndSaves_matchesAcceptedCommandsOnly() throws Exception {
        Fixture fixture = new Fixture();
        ModelManager accepted = new ModelManager(fixture.model.getAddressBook(), new UserPrefs());
        AddressBookParser parser = new AddressBookParser();
        Random random = new Random(2103);
        for (int i = 0; i < 300; i++) {
            String command = switch (i % 5) {
                case 0 -> "salary 1 s/" + random.nextInt(50001);
                case 1 -> "task add 1 d/Task " + i;
                case 2 -> "sort n/" + (random.nextBoolean() ? "asc" : "des");
                case 3 -> "edit 1 a/Address " + i;
                default -> "summary scope/all";
            };
            byte[] before = Files.readAllBytes(fixture.path);
            if (random.nextBoolean()) {
                fixture.storage.failure = FAILURES.get(random.nextInt(FAILURES.size()));
                assertThrows(CommandException.class, () -> fixture.logic.execute(command));
                if (!fixture.storage.failure.equals("partial")) {
                    assertArrayEquals(before, Files.readAllBytes(fixture.path));
                }
            } else {
                fixture.storage.failure = "";
                assertEquals(parser.parseCommand(command).execute(accepted), fixture.logic.execute(command));
            }
            assertEquals(accepted.getAddressBook(), fixture.model.getAddressBook(), "Iteration " + i);
            assertEquals(accepted.getFilteredEmployeeList(), fixture.model.getFilteredEmployeeList());
            // Only a successful save repairs damage from an earlier partial write.
            if (fixture.storage.failure.isEmpty()) {
                assertEquals(accepted.getAddressBook(), fixture.storage.readAddressBook().orElseThrow());
            }
        }
    }

    @Test
    public void execute_largeBookRepeatedRollbackAndSave_preservesAllRecords() throws Exception {
        Fixture fixture = new Fixture();
        AddressBook large = new AddressBook();
        for (int i = 0; i < 1000; i++) {
            large.addEmployee(new EmployeeBuilder().withName("Employee " + i)
                    .withPhone(Integer.toString(80000000 + i)).withEmail("employee" + i + "@example.com").build());
        }
        fixture.model.setAddressBook(large);
        fixture.logic.execute("sort n/des");
        for (int i = 0; i < 10; i++) {
            AddressBook before = new AddressBook(fixture.model.getAddressBook());
            fixture.storage.failure = "partial";
            assertThrows(CommandException.class, () -> fixture.logic.execute("clear"));
            assertEquals(before, fixture.model.getAddressBook());
            fixture.storage.failure = "";
            fixture.logic.execute("salary 1 s/" + i);
            assertEquals(fixture.model.getAddressBook(), fixture.storage.readAddressBook().orElseThrow());
            assertEquals(1000, fixture.model.getAddressBook().getEmployeeList().size());
        }
    }

    @Test
    public void execute_invalidCommands_doesNotAttemptSaving() throws Exception {
        Fixture fixture = new Fixture();
        AddressBook before = new AddressBook(fixture.model.getAddressBook());
        byte[] bytes = Files.readAllBytes(fixture.path);
        int writes = fixture.storage.writes;
        fixture.storage.failure = "partial";
        for (String command : List.of("unknown", "salary 1 s/abc", "add")) {
            assertThrows(ParseException.class, () -> fixture.logic.execute(command));
        }
        for (String command : List.of("delete 999", "task delete 1 i/99", "leave delete 999",
                "leave add 1 from/05-10-2026 to/07-10-2026")) {
            assertThrows(CommandException.class, () -> fixture.logic.execute(command));
        }
        assertEquals(writes, fixture.storage.writes);
        assertEquals(before, fixture.model.getAddressBook());
        assertArrayEquals(bytes, Files.readAllBytes(fixture.path));
    }

    @Test
    public void execute_emptyFilteredView_failureRestoresPredicateForFutureChanges() throws Exception {
        Fixture fixture = new Fixture();
        fixture.logic.execute("find Nobody");
        fixture.storage.failure = "denied";

        assertThrows(CommandException.class, () -> fixture.logic.execute("list"));

        assertTrue(fixture.model.getFilteredEmployeeList().isEmpty());
        Employee alice = fixture.model.getAddressBook().getEmployeeList().get(0);
        fixture.model.setEmployee(alice, new EmployeeBuilder(alice).withName("Nobody").build());
        assertEquals(1, fixture.model.getFilteredEmployeeList().size());
        assertEquals("Nobody", fixture.model.getFilteredEmployeeList().get(0).getName().fullName);
    }

    @Test
    public void execute_relativeUnicodePath_preservesJsonAndCreatesParents() throws Exception {
        // Keep both paths on the checkout drive; CI's default temp folder may use another drive.
        Path buildDirectory = Path.of("build").toAbsolutePath();
        Files.createDirectories(buildDirectory);
        Path testDirectory = Files.createTempDirectory(buildDirectory, "unicode-path-");
        Path absolute = testDirectory.resolve("nested folder/人事 book.json");
        try {
            Path relative = Path.of("").toAbsolutePath().relativize(absolute);
            JsonAddressBookStorage storage = new JsonAddressBookStorage(relative);
            AddressBook book = new AddressBook();
            book.addEmployee(new EmployeeBuilder().withName("Alice").withAddress("新加坡 Café Street").build());

            storage.saveAddressBook(book);
            assertEquals(book, storage.readAddressBook().orElseThrow());
            book.addEmployee(new EmployeeBuilder().withName("Bob").build());
            storage.saveAddressBook(book);
            assertEquals(book, storage.readAddressBook().orElseThrow());
            try (var files = Files.list(absolute.getParent())) {
                assertEquals(List.of(absolute), files.toList());
            }
        } finally {
            Files.deleteIfExists(absolute);
            Files.deleteIfExists(absolute.getParent());
            Files.deleteIfExists(testDirectory);
        }
    }

    @Test
    public void execute_parentIsFile_rollsBackModel() throws Exception {
        Fixture fixture = new Fixture();
        Path parent = folder.resolve("blocked");
        Files.writeString(parent, "Keep this file");
        LogicManager logic = new LogicManager(fixture.model, new StorageManager(
                new JsonAddressBookStorage(parent.resolve("addressbook.json")),
                new JsonUserPrefsStorage(folder.resolve("prefs.json"))));
        AddressBook before = new AddressBook(fixture.model.getAddressBook());

        assertThrows(CommandException.class, () -> logic.execute("clear"));

        assertEquals(before, fixture.model.getAddressBook());
        assertEquals("Keep this file", Files.readString(parent));
    }

    @Test
    public void execute_destinationIsNonemptyDirectory_rollsBackAndPreservesDirectory() throws Exception {
        Fixture fixture = new Fixture();
        Path directory = Files.createDirectory(folder.resolve("directory.json"));
        Path child = directory.resolve("keep.txt");
        Files.writeString(child, "Keep this file");
        LogicManager logic = new LogicManager(fixture.model, new StorageManager(
                new JsonAddressBookStorage(directory), new JsonUserPrefsStorage(folder.resolve("prefs.json"))));
        AddressBook before = new AddressBook(fixture.model.getAddressBook());

        assertThrows(CommandException.class, () -> logic.execute("clear"));

        assertEquals(before, fixture.model.getAddressBook());
        assertEquals("Keep this file", Files.readString(child));
        try (var files = Files.list(folder)) {
            assertTrue(files.noneMatch(path -> path.toString().endsWith(".tmp")));
        }
    }

    /** A fresh book with both task states and a leave record for each displayed employee. */
    private class Fixture {
        private final Path path = folder.resolve("addressbook.json");
        private final ModelManager model = new ModelManager();
        private final FaultStorage storage = new FaultStorage(path);
        private final LogicManager logic = new LogicManager(model,
                new StorageManager(storage, new JsonUserPrefsStorage(folder.resolve("prefs.json"))));

        Fixture() throws Exception {
            logic.execute("add n/Alice p/91234567 e/alice@example.com a/Main Street le/14 t/engineering");
            logic.execute("add n/Bob p/91234568 e/bob@example.com a/Other Street le/14 t/sales");
            for (int i = 1; i <= 2; i++) {
                logic.execute("task add " + i + " d/Report");
                logic.execute("task add " + i + " d/Review");
                logic.execute("task mark " + i + " i/2");
                logic.execute("leave add " + i + " from/05-10-2026 to/07-10-2026");
            }
        }
    }

    /** Injects save failures while retaining real direct writes for successful saves. */
    private static class FaultStorage extends JsonAddressBookStorage {
        private String failure = "";
        private IOException lastFailure;
        private int writes;

        FaultStorage(Path path) {
            super(path);
        }

        @Override
        public void saveAddressBook(ReadOnlyAddressBook book, Path path) throws IOException {
            writes++;
            if (failure.equals("partial")) {
                Files.writeString(path, "{incomplete");
            }
            lastFailure = switch (failure) {
                case "partial" -> new IOException("Partial write");
                case "write" -> new IOException("Write failed");
                case "denied" -> new AccessDeniedException(path.toString());
                default -> null;
            };
            if (lastFailure != null) {
                throw lastFailure;
            }
            super.saveAddressBook(book, path);
        }
    }
}
