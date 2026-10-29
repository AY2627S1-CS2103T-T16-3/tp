package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalEmployees.ALICE;
import static seedu.address.testutil.TypicalEmployees.HOON;
import static seedu.address.testutil.TypicalEmployees.IDA;
import static seedu.address.testutil.TypicalEmployees.getTypicalAddressBook;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.logic.LogicManager;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.employee.Employee;
import seedu.address.model.employee.EmployeeId;
import seedu.address.model.employee.Salary;
import seedu.address.model.employee.Task;
import seedu.address.model.leave.Leave;
import seedu.address.model.leave.LeaveId;
import seedu.address.model.leave.LeavePeriod;
import seedu.address.testutil.EmployeeBuilder;

public class JsonAddressBookStorageTest {
    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonAddressBookStorageTest");

    @TempDir
    public Path testFolder;

    @Test
    public void execute_failedSaveThenSuccessfulCommand_doesNotPersistRejectedChange() throws Exception {
        Path destination = testFolder.resolve("addressbook.json");
        FailingStorage storage = new FailingStorage(destination);
        ModelManager model = new ModelManager();
        LogicManager logic = new LogicManager(model, new StorageManager(storage,
                new JsonUserPrefsStorage(testFolder.resolve("prefs.json"))));
        logic.execute("add n/Alice p/91234567 e/alice@example.com a/Main Street le/14");
        AddressBook before = new AddressBook(model.getAddressBook());
        byte[] saved = Files.readAllBytes(destination);
        storage.failure = "write";

        assertThrows(CommandException.class, () -> logic.execute("salary 1 s/5000"));

        assertEquals(before, model.getAddressBook());
        assertArrayEquals(saved, Files.readAllBytes(destination));
        storage.failure = "";
        logic.execute("task add 1 d/Report");
        ReadOnlyAddressBook reloaded = storage.readAddressBook().orElseThrow();
        assertEquals(model.getAddressBook(), new AddressBook(reloaded));
        assertEquals(before.getEmployeeList().get(0).getSalary(), reloaded.getEmployeeList().get(0).getSalary());
        assertEquals(1, reloaded.getEmployeeList().get(0).getTasks().size());
    }

    @Test
    public void execute_partialWriteThenSuccessfulSaveAndRestart_preservesAcceptedChangesOnly() throws Exception {
        Path destination = testFolder.resolve("addressbook.json");
        Path prefsPath = testFolder.resolve("prefs.json");
        FailingStorage storage = new FailingStorage(destination);
        ModelManager model = new ModelManager();
        LogicManager logic = new LogicManager(model, new StorageManager(storage,
                new JsonUserPrefsStorage(prefsPath)));
        logic.execute("add n/Alice p/91234567 e/alice@example.com a/Main Street le/14");
        logic.execute("salary 1 s/1000");
        AddressBook before = new AddressBook(model.getAddressBook());
        storage.failure = "partial";

        assertThrows(CommandException.class, () -> logic.execute("salary 1 s/5000"));
        assertEquals(before, model.getAddressBook());
        assertThrows(DataLoadingException.class, () -> storage.readAddressBook());

        // A later successful command saves the restored model before restarting.
        storage.failure = "";
        logic.execute("task add 1 d/Report");
        AddressBook expected = new AddressBook(before);
        Employee originalEmployee = before.getEmployeeList().get(0);
        expected.setEmployee(originalEmployee, originalEmployee.addTask(new Task("Report")));

        // Simulate restart using only disk data, with fresh storage, model, and logic instances.
        JsonAddressBookStorage restartedStorage = new JsonAddressBookStorage(destination);
        ModelManager restartedModel = new ModelManager(restartedStorage.readAddressBook().orElseThrow(),
                new UserPrefs());
        LogicManager restartedLogic = new LogicManager(restartedModel, new StorageManager(restartedStorage,
                new JsonUserPrefsStorage(prefsPath)));
        assertEquals(expected, restartedModel.getAddressBook());

        restartedLogic.execute("list");

        ReadOnlyAddressBook reloaded = new JsonAddressBookStorage(destination).readAddressBook().orElseThrow();
        assertEquals(expected, new AddressBook(reloaded));
        assertEquals(expected, restartedModel.getAddressBook());
        assertEquals(1, reloaded.getEmployeeList().size());
        assertEquals(new Salary("1000"), reloaded.getEmployeeList().get(0).getSalary());
        assertEquals(java.util.List.of(new Task("Report")), reloaded.getEmployeeList().get(0).getTasks());
    }

    /** Simulates failures before writing or after damaging the destination file. */
    private static class FailingStorage extends JsonAddressBookStorage {
        private String failure = "";

        FailingStorage(Path destination) {
            super(destination);
        }

        @Override
        public void saveAddressBook(ReadOnlyAddressBook addressBook, Path destination) throws IOException {
            if (failure.equals("partial")) {
                Files.writeString(destination, "{incomplete");
                throw new IOException("Partial write");
            }
            if (failure.equals("write")) {
                throw new IOException("Write failed");
            }
            super.saveAddressBook(addressBook, destination);
        }
    }

    @Test
    public void readAddressBook_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> readAddressBook(null));
    }

    private java.util.Optional<ReadOnlyAddressBook> readAddressBook(String filePath) throws Exception {
        return new JsonAddressBookStorage(Paths.get(filePath)).readAddressBook(addToTestDataPathIfNotNull(filePath));
    }

    private Path addToTestDataPathIfNotNull(String prefsFileInTestDataFolder) {
        return prefsFileInTestDataFolder != null
                ? TEST_DATA_FOLDER.resolve(prefsFileInTestDataFolder)
                : null;
    }

    @Test
    public void read_missingFile_emptyResult() throws Exception {
        assertFalse(readAddressBook("NonExistentFile.json").isPresent());
    }

    @Test
    public void read_notJsonFormat_exceptionThrown() {
        assertThrows(DataLoadingException.class, () -> readAddressBook("notJsonFormatAddressBook.json"));
    }

    @Test
    public void readAddressBook_invalidEmployeeAddressBook_throwDataLoadingException() {
        assertThrows(DataLoadingException.class, () -> readAddressBook("invalidEmployeeAddressBook.json"));
    }

    @Test
    public void readAddressBook_invalidAndValidEmployeeAddressBook_throwDataLoadingException() {
        assertThrows(DataLoadingException.class, () -> readAddressBook("invalidAndValidEmployeeAddressBook.json"));
    }

    @Test
    public void readAndSaveAddressBook_allInOrder_success() throws Exception {
        Path filePath = testFolder.resolve("TempAddressBook.json");
        AddressBook original = getTypicalAddressBook();
        Leave leave = new Leave(new LeaveId(1), new EmployeeId(1), new LeavePeriod(
                LocalDate.of(2026, 10, 5), LocalDate.of(2026, 10, 7)));
        original.setLeaves(java.util.List.of(leave));
        JsonAddressBookStorage jsonAddressBookStorage = new JsonAddressBookStorage(filePath);

        // Save in new file and read back
        jsonAddressBookStorage.saveAddressBook(original, filePath);
        ReadOnlyAddressBook readBack = jsonAddressBookStorage.readAddressBook(filePath).get();
        assertEquals(original, new AddressBook(readBack));

        // Modify data, overwrite existing file, and read back
        original.addEmployee(HOON);
        original.removeEmployee(ALICE);
        jsonAddressBookStorage.saveAddressBook(original, filePath);
        readBack = jsonAddressBookStorage.readAddressBook(filePath).get();
        assertEquals(original, new AddressBook(readBack));

        // Save and read without specifying file path
        original.addEmployee(IDA);
        jsonAddressBookStorage.saveAddressBook(original); // file path not specified
        readBack = jsonAddressBookStorage.readAddressBook().get(); // file path not specified
        assertEquals(original, new AddressBook(readBack));

    }

    @Test
    public void saveAddressBook_nullAddressBook_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveAddressBook(null, "SomeFile.json"));
    }

    /**
     * Saves {@code addressBook} at the specified {@code filePath}.
     */
    private void saveAddressBook(ReadOnlyAddressBook addressBook, String filePath) {
        try {
            new JsonAddressBookStorage(Paths.get(filePath))
                    .saveAddressBook(addressBook, addToTestDataPathIfNotNull(filePath));
        } catch (IOException ioe) {
            throw new AssertionError("There should not be an error writing to the file.", ioe);
        }
    }

    @Test
    public void saveAddressBook_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveAddressBook(new AddressBook(), null));
    }
    @Test
    public void readAndSaveAddressBook_nonZeroSalary_preservesNormalizedSalary() throws Exception {
        Path filePath = testFolder.resolve("SalaryAddressBook.json");
        JsonAddressBookStorage storage = new JsonAddressBookStorage(filePath);
        AddressBook original = new AddressBook();
        Employee employee = new EmployeeBuilder().withSalary("00005000").build();
        original.addEmployee(employee);

        storage.saveAddressBook(original);
        ReadOnlyAddressBook readBack = storage.readAddressBook().get();

        assertEquals(original, new AddressBook(readBack));
        assertEquals(new Salary("5000"), readBack.getEmployeeList().get(0).getSalary());
        assertEquals("5000", readBack.getEmployeeList().get(0).getSalary().salary);
    }
}
