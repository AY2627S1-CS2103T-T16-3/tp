package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_INVALID_EMPLOYEE_DISPLAYED_INDEX;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.logic.commands.CommandTestUtil.ADDRESS_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.LEAVE_ENTITLEMENT_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.SummaryTestUtil.assertSummaryValue;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.SalaryCommand;
import seedu.address.logic.commands.SummaryCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.employee.Employee;
import seedu.address.model.employee.Task;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.EmployeeBuilder;

public class LogicManagerTest {
    private static final IOException DUMMY_IO_EXCEPTION = new IOException("dummy IO exception");
    private static final IOException DUMMY_AD_EXCEPTION = new AccessDeniedException("dummy access denied exception");

    @TempDir
    public Path temporaryFolder;

    private Model model = new ModelManager();
    private Logic logic;

    @BeforeEach
    public void setUp() {
        JsonAddressBookStorage addressBookStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage);
        logic = new LogicManager(model, storage);
    }

    @Test
    public void execute_invalidCommandFormat_throwsParseException() {
        String invalidCommand = "uicfhmowqewca";
        assertParseException(invalidCommand, MESSAGE_UNKNOWN_COMMAND);
    }

    @Test
    public void execute_commandExecutionError_throwsCommandException() {
        String deleteCommand = "delete 9";
        assertCommandException(deleteCommand, MESSAGE_INVALID_EMPLOYEE_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validCommand_success() throws Exception {
        String listCommand = ListCommand.COMMAND_WORD;
        assertCommandSuccess(listCommand, ListCommand.MESSAGE_SUCCESS, model);
    }

    @Test
    public void execute_addLeave_recordsAndPersistsLeave() throws Exception {
        logic.execute("add n/Alice p/91234567 e/alice@example.com a/123 Main Street le/14");

        logic.execute("leave add 1 from/05-10-2026 to/07-10-2026");

        assertEquals(1, model.getAddressBook().getLeaveList().size());
        JsonAddressBookStorage storage = new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        ReadOnlyAddressBook savedData = storage.readAddressBook().orElseThrow();
        assertEquals(new AddressBook(model.getAddressBook()), new AddressBook(savedData));
    }

    @Test
    public void execute_deleteLeave_deletesAndPersistsLeave() throws Exception {
        logic.execute("add n/Alice p/91234567 e/alice@example.com a/123 Main Street le/14");
        logic.execute("leave add 1 from/05-10-2026 to/07-10-2026");

        logic.execute("leave delete 1");

        assertEquals(0, model.getAddressBook().getLeaveList().size());
        JsonAddressBookStorage storage = new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        ReadOnlyAddressBook savedData = storage.readAddressBook().orElseThrow();
        assertEquals(new AddressBook(model.getAddressBook()), new AddressBook(savedData));
    }

    @Test
    public void execute_summaryAliasesWithTasksAndActiveFilter_preservesModelAndSavedData() throws Exception {
        Employee alice = new EmployeeBuilder().withName("Alice").withTags("Engineering", "engineering")
                .withTasks(new Task("Report")).build();
        Employee bob = new EmployeeBuilder().withName("Bob").withTags()
                .withTasks(new Task("Report"), new Task("Review")).build();
        model.addEmployee(alice);
        model.addEmployee(bob);
        Employee storedAlice = model.getAddressBook().getEmployeeList().get(0);
        AddressBook before = new AddressBook(model.getAddressBook());
        for (String selection : new String[] {"find Alice", "filter t/engineering"}) {
            logic.execute(selection);
            Path dataFile = temporaryFolder.resolve("addressBook.json");
            String savedData = Files.readString(dataFile);
            for (String command : new String[] {SummaryCommand.COMMAND_WORD, SummaryCommand.COMMAND_ALIAS}) {
                CommandResult result = logic.execute(command + " scope/all");
                assertEquals(CommandResult.DisplayFormat.TABLE, result.getDisplayFormat());
                assertSummaryValue(result.getFeedbackToUser(), "Employees", "2");
                assertSummaryValue(result.getFeedbackToUser(), "Without tags", "1");
                assertSummaryValue(result.getFeedbackToUser(), "Assigned tasks", "3");
                assertSummaryValue(result.getFeedbackToUser(), "Employees with tasks", "2");
                assertSummaryValue(result.getFeedbackToUser(), "engineering", "1");
                assertEquals(List.of(storedAlice), logic.getFilteredEmployeeList());
                assertEquals(before, model.getAddressBook());
                assertEquals(savedData, Files.readString(dataFile));
            }
        }
    }

    @Test
    public void execute_invalidSummaryWithTasks_preservesModelFilterAndSavedData() throws Exception {
        model.addEmployee(new EmployeeBuilder().withName("Alice").withTasks(new Task("Report")).build());
        model.addEmployee(new EmployeeBuilder().withName("Bob").withTasks(new Task("Review")).build());
        logic.execute("find Alice");
        AddressBook before = new AddressBook(model.getAddressBook());
        List<Employee> displayedEmployees = List.copyOf(model.getFilteredEmployeeList());
        Path dataFile = temporaryFolder.resolve("addressBook.json");
        String savedData = Files.readString(dataFile);

        for (String command : new String[] {SummaryCommand.COMMAND_WORD, SummaryCommand.COMMAND_ALIAS}) {
            assertThrows(ParseException.class,
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, SummaryCommand.MESSAGE_USAGE), () ->
                    logic.execute(command + " 1"));
            assertEquals(before, model.getAddressBook());
            assertEquals(displayedEmployees, model.getFilteredEmployeeList());
            assertEquals(savedData, Files.readString(dataFile));
        }
    }

    @Test
    public void execute_summaryAfterTaskCommands_recomputesAndPreservesSavedTasks() throws Exception {
        model.addEmployee(new EmployeeBuilder().withName("Alice").withTags().build());
        model.addEmployee(new EmployeeBuilder().withName("Bob").withTags()
                .withTasks(new Task("Review")).build());
        logic.execute("find Alice");
        logic.execute("task add 1 d/Report");
        AddressBook before = new AddressBook(model.getAddressBook());
        Path dataFile = temporaryFolder.resolve("addressBook.json");
        String savedData = Files.readString(dataFile);

        String result = logic.execute("summary scope/all").getFeedbackToUser();
        assertSummaryValue(result, "Assigned tasks", "2");
        assertSummaryValue(result, "Employees with tasks", "2");
        assertEquals(before, model.getAddressBook());
        assertEquals(savedData, Files.readString(dataFile));

        logic.execute("task delete 1 i/1");
        before = new AddressBook(model.getAddressBook());
        savedData = Files.readString(dataFile);
        result = logic.execute("summary scope/all").getFeedbackToUser();
        assertSummaryValue(result, "Assigned tasks", "1");
        assertSummaryValue(result, "Employees with tasks", "1");
        assertEquals(before, model.getAddressBook());
        assertEquals(savedData, Files.readString(dataFile));
        JsonAddressBookStorage storage = new JsonAddressBookStorage(dataFile);
        assertEquals(before, new AddressBook(storage.readAddressBook().orElseThrow()));
    }

    @Test
    public void execute_summaryAfterRepeatedSortsWithTies_preservesFilterAndSavedData() throws Exception {
        logic.execute("add n/Carol p/91234561 e/carol@example.com a/One le/14 s/1000 t/finance");
        logic.execute("add n/Bob p/91234562 e/bob@example.com a/Two le/14 s/1000 t/remote");
        logic.execute("add n/Alice p/91234563 e/alice@example.com a/Three le/14 s/1000 t/engineering");
        logic.execute("sort n/asc");
        logic.execute("sort s/asc");
        logic.execute("list");
        Path dataFile = temporaryFolder.resolve("addressBook.json");
        String savedData = Files.readString(dataFile);
        AddressBook before = new AddressBook(model.getAddressBook());

        for (String alias : List.of("summary", "/summary")) {
            for (String selection : List.of("i/1", "e/1")) {
                String filtered = logic.execute(alias + " " + selection).getFeedbackToUser();
                String all = logic.execute(alias + " scope/all " + selection).getFeedbackToUser();
                assertEquals(filtered.replace("Scope: Filtered employees", "Scope: All employees"), all);
            }
        }

        logic.execute("find Alice Carol");
        List<Employee> displayed = List.copyOf(model.getFilteredEmployeeList());
        assertSummaryValue(logic.execute("summary scope/all i/2").getFeedbackToUser(), "remote", "1");
        assertEquals(displayed, model.getFilteredEmployeeList());
        assertEquals(before, model.getAddressBook());
        assertEquals(savedData, Files.readString(dataFile));
    }

    @Test
    public void execute_summaryAliases_summarizeDisplayedEmployeesWithoutChangingFilterOrSavedData() throws Exception {
        Employee alice = new EmployeeBuilder().withName("Alice").withTags("Engineering", "engineering").build();
        Employee bob = new EmployeeBuilder().withName("Bob").withTags().build();
        model.addEmployee(alice);
        model.addEmployee(bob);
        Employee storedAlice = model.getAddressBook().getEmployeeList().get(0);
        Employee storedBob = model.getAddressBook().getEmployeeList().get(1);
        logic.execute("find Alice");
        Path dataFile = temporaryFolder.resolve("addressBook.json");
        String savedData = Files.readString(dataFile);
        for (String command : new String[] {SummaryCommand.COMMAND_WORD, SummaryCommand.COMMAND_ALIAS}) {
            CommandResult result = logic.execute(command);
            assertEquals(CommandResult.DisplayFormat.TABLE, result.getDisplayFormat());
            assertTrue(result.getFeedbackToUser().startsWith("Employee summary\nScope: Filtered employees\n\n"));
            assertSummaryValue(result.getFeedbackToUser(), "Employees", "1");
            assertSummaryValue(result.getFeedbackToUser(), "engineering", "1");
            assertEquals(List.of(storedAlice), logic.getFilteredEmployeeList());
            assertEquals(List.of(storedAlice, storedBob), model.getAddressBook().getEmployeeList());
            assertEquals(savedData, Files.readString(dataFile));
        }
    }

    @Test
    public void execute_scopedSummaryAfterFindAndFilter_preservesDataAndDisplayedEmployees() throws Exception {
        Employee alice = new EmployeeBuilder().withName("Alice").withTags("engineering").withSalary("1000")
                .withTasks(new Task("Report"), new Task("Review")).build();
        Employee bob = new EmployeeBuilder().withName("Bob").withTags("remote").withSalary("3000")
                .withTasks(new Task("Review")).build();
        model.addEmployee(alice);
        model.addEmployee(bob);
        Employee storedBob = model.getAddressBook().getEmployeeList().get(1);
        AddressBook before = new AddressBook(model.getAddressBook());
        for (String selection : new String[] {"find Bob", "filter t/remote"}) {
            logic.execute(selection);
            Path dataFile = temporaryFolder.resolve("addressBook.json");
            String savedData = Files.readString(dataFile);
            for (String alias : new String[] {"summary", "/summary"}) {
                String filtered = logic.execute(alias + " scope/filtered").getFeedbackToUser();
                assertEquals(filtered, logic.execute(alias).getFeedbackToUser());
                assertTrue(filtered.startsWith("Employee summary\nScope: Filtered employees\n\n"));
                assertSummaryValue(filtered, "Employees", "1");
                assertSummaryValue(filtered, "remote", "1");
                assertSummaryValue(filtered, "Assigned tasks", "1");
                assertSummaryValue(filtered, "Total salary", "$3000.00");
                assertSummaryValue(filtered, "Average salary", "$3000.00");
                String all = logic.execute(alias + " scope/all").getFeedbackToUser();
                assertTrue(all.startsWith("Employee summary\nScope: All employees\n\n"));
                assertSummaryValue(all, "Employees", "2");
                assertSummaryValue(all, "engineering", "1");
                assertSummaryValue(all, "remote", "1");
                assertSummaryValue(all, "Assigned tasks", "3");
                assertSummaryValue(all, "Total salary", "$4000.00");
                assertSummaryValue(all, "Average salary", "$2000.00");
                assertEquals(before, model.getAddressBook());
                assertEquals(List.of(storedBob), model.getFilteredEmployeeList());
                assertEquals(savedData, Files.readString(dataFile));
            }
        }
    }

    @Test
    public void execute_storageThrowsIoException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_IO_EXCEPTION, String.format(
                LogicManager.FILE_OPS_ERROR_FORMAT, DUMMY_IO_EXCEPTION.getMessage()));
    }

    @Test
    public void execute_storageThrowsAdException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_AD_EXCEPTION, String.format(
                LogicManager.FILE_OPS_PERMISSION_ERROR_FORMAT, DUMMY_AD_EXCEPTION.getMessage()));
    }

    @Test
    public void execute_saveFailure_restoresDataFilterAndSort() throws Exception {
        logic.execute("add n/Alice p/91234567 e/alice@example.com a/Main Street le/14");
        logic.execute("add n/Bob p/91234568 e/bob@example.com a/Main Street le/14");
        logic.execute("leave add 1 from/05-10-2026 to/07-10-2026");
        logic.execute("sort n/asc");
        logic.execute("find Alice");
        AddressBook before = new AddressBook(model.getAddressBook());
        List<Employee> displayed = List.copyOf(model.getFilteredEmployeeList());
        JsonAddressBookStorage failingStorage = new JsonAddressBookStorage(temporaryFolder.resolve("unused.json")) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
                throw DUMMY_IO_EXCEPTION;
            }
        };
        logic = new LogicManager(model, new StorageManager(failingStorage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("prefs.json"))));

        for (String command : List.of(
                "add n/Charlie p/81234567 e/charlie@example.com a/Main Street le/14",
                "salary 1 s/5000", "delete 1", "task add 1 d/Report", "leave delete 1",
                "leave add 1 from/12-10-2026 to/13-10-2026", "list", "sort n/des")) {
            assertThrows(CommandException.class, () -> logic.execute(command));
            assertEquals(before, model.getAddressBook());
            assertEquals(displayed, model.getFilteredEmployeeList());
            model.updateFilteredEmployeeList(Model.PREDICATE_SHOW_ALL_EMPLOYEES);
            assertEquals(before.getEmployeeList(), model.getFilteredEmployeeList());
            model.updateFilteredEmployeeList(employee -> employee.getName().fullName.equals("Alice"));
        }
    }

    @Test
    public void getFilteredEmployeeList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> logic.getFilteredEmployeeList().remove(0));
    }

    @Test
    public void execute_sameSalaryCommand_preservesFilteredModelAndStoredData() throws Exception {
        logic.execute("add n/Alice p/91234567 e/alice@example.com a/123 Main Street le/14 s/1000");
        logic.execute("add n/Bob p/92345678 e/bob@example.com a/456 Main Street le/14 s/2000");
        logic.execute("find Bob");
        List<Employee> originalEmployees = List.copyOf(model.getAddressBook().getEmployeeList());
        List<Employee> originalFilteredEmployees = List.copyOf(model.getFilteredEmployeeList());
        Path storagePath = temporaryFolder.resolve("addressBook.json");
        String originalStoredData = Files.readString(storagePath);

        CommandResult result = logic.execute("salary 1 s/002000");

        assertEquals("Bob's salary is already $2000. No changes made.", result.getFeedbackToUser());
        assertEquals(originalEmployees, model.getAddressBook().getEmployeeList());
        assertEquals(originalFilteredEmployees, model.getFilteredEmployeeList());
        assertEquals(originalStoredData, Files.readString(storagePath));
        ReadOnlyAddressBook saved = new JsonAddressBookStorage(storagePath).readAddressBook().orElseThrow();
        assertEquals(originalEmployees, saved.getEmployeeList());
    }

    @Test
    public void execute_salaryCommand_updatesModelAndStorage() throws Exception {
        // Original employee
        Employee charlie = new EmployeeBuilder()
                .withName("Charlie")
                .withSalary("1000")
                .withTags("Engineering", "FullTime")
                .build()
                .addTask(new Task("Prepare  REPORT"))
                .addTask(new Task("Review   Budget"));
        model.addEmployee(charlie);
        List<String> originalTaskDetails = charlie.getTasks().stream().map(Task::getDetails).toList();

        // Expected employee: only the salary changes
        Employee expected = new EmployeeBuilder(model.getAddressBook().getEmployeeList().get(0))
                .withSalary("5000")
                .build();

        assertEquals("$1000", charlie.getSalary().toString());

        // Expected feedback for tested command
        CommandResult expectedFeedback = new CommandResult(
                String.format(SalaryCommand.MESSAGE_SUCCESS,
                        expected.getName(), charlie.getSalary(), expected.getSalary()));

        // Asserts that command feedback is correct
        assertEquals(expectedFeedback, logic.execute("salary 1 s/005000"));

        // Asserts that logic changes Salary object
        assertEquals("5000",
                model.getAddressBook().getEmployeeList().get(0).getSalary().salary);

        // Assert that logic rejects invalid parameters
        assertThrows(ParseException.class, () ->
                logic.execute("salary 1 s/abc"));

        JsonAddressBookStorage storage =
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        // Take saved data from local storage
        ReadOnlyAddressBook saved = storage.readAddressBook().orElseThrow();

        // Asserts that storage safely saves salary
        assertEquals("$5000", saved.getEmployeeList().get(0).getSalary().toString());

        // Asserts that other fields of employee are preserved after salary command
        assertEquals(expected, model.getAddressBook().getEmployeeList().get(0));
        assertEquals(originalTaskDetails, model.getAddressBook().getEmployeeList().get(0)
                .getTasks().stream().map(Task::getDetails).toList());
        assertEquals(originalTaskDetails, saved.getEmployeeList().get(0)
                .getTasks().stream().map(Task::getDetails).toList());

        // Asserts that the saved address book matches the model
        assertEquals(model.getAddressBook(), saved);
    }

    @Test
    public void execute_salaryWithUnsupportedField_preservesModelListAndSavedData() throws Exception {
        model.addEmployee(new EmployeeBuilder().withName("Alice").withSalary("1000")
                .withTasks(new Task("Report")).build());
        model.addEmployee(new EmployeeBuilder().withName("Bob").withSalary("2000").build());
        Path dataFile = temporaryFolder.resolve("addressBook.json");

        for (String selection : List.of("list", "find Bob", "sort s/des")) {
            logic.execute(selection);
            AddressBook before = new AddressBook(model.getAddressBook());
            List<Employee> displayedEmployees = List.copyOf(model.getFilteredEmployeeList());
            String savedData = Files.readString(dataFile);

            for (String args : List.of("1 s/5000 n/Charlie", "1 n/Charlie s/5000",
                    "1 s/-1 n/Charlie", "1 s/5000 x/test", "0 s/5000 n/Charlie",
                    "1 s/5000 s/4000 n/Charlie", "1 n/Charlie")) {
                assertThrows(ParseException.class, "Unsupported field. The salary command only accepts s/.", () ->
                        logic.execute("salary " + args));
                assertEquals(before, model.getAddressBook());
                assertEquals(displayedEmployees, model.getFilteredEmployeeList());
                assertEquals(savedData, Files.readString(dataFile));
            }
        }

        logic.execute("salary 1 s/003000");
        assertEquals("3000", model.getAddressBook().getEmployeeList().get(1).getSalary().salary);
        assertEquals("1000", model.getAddressBook().getEmployeeList().get(0).getSalary().salary);
        JsonAddressBookStorage storage = new JsonAddressBookStorage(dataFile);
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
    }

    /**
     * Executes the command and confirms that
     * - no exceptions are thrown <br>
     * - the feedback message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandSuccess(String inputCommand, String expectedMessage,
            Model expectedModel) throws CommandException, ParseException {
        CommandResult result = logic.execute(inputCommand);
        assertEquals(expectedMessage, result.getFeedbackToUser());
        assertEquals(expectedModel, model);
    }

    /**
     * Executes the command, confirms that a ParseException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertParseException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, ParseException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that a CommandException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, CommandException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that the exception is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage) {
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        assertCommandFailure(inputCommand, expectedException, expectedMessage, expectedModel);
    }

    /**
     * Executes the command and confirms that
     * - the {@code expectedException} is thrown <br>
     * - the resulting error message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandSuccess(String, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage, Model expectedModel) {
        assertThrows(expectedException, expectedMessage, () -> logic.execute(inputCommand));
        assertEquals(expectedModel, model);
    }

    /**
     * Tests the Logic component's handling of an {@code IOException} thrown by the Storage component.
     *
     * @param e the exception to be thrown by the Storage component
     * @param expectedMessage the message expected inside exception thrown by the Logic component
     */
    private void assertCommandFailureForExceptionFromStorage(IOException e, String expectedMessage) {
        Path prefPath = temporaryFolder.resolve("ExceptionUserPrefs.json");

        // Inject LogicManager with a JsonAddressBookStorage that throws the IOException e when saving
        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(prefPath) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
                throw e;
            }
        };

        JsonUserPrefsStorage userPrefsStorage =
                new JsonUserPrefsStorage(temporaryFolder.resolve("ExceptionUserPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage);

        logic = new LogicManager(model, storage);

        // Triggers the saveAddressBook method by executing an add command
        String addCommand = AddCommand.COMMAND_WORD + NAME_DESC_AMY + PHONE_DESC_AMY
                + EMAIL_DESC_AMY + ADDRESS_DESC_AMY + LEAVE_ENTITLEMENT_DESC_AMY;
        ModelManager expectedModel = new ModelManager();
        assertCommandFailure(addCommand, CommandException.class, expectedMessage, expectedModel);
    }

}
