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
import static seedu.address.testutil.TypicalEmployees.AMY;

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
    public void execute_summaryAliasesWithTasksAndActiveFilter_preservesModelAndSavedData() throws Exception {
        Employee alice = new EmployeeBuilder().withName("Alice").withTags("Engineering", "engineering")
                .withTasks(new Task("Report")).build();
        Employee bob = new EmployeeBuilder().withName("Bob").withTags()
                .withTasks(new Task("Report"), new Task("Review")).build();
        model.addEmployee(alice);
        model.addEmployee(bob);
        Employee storedAlice = model.getAddressBook().getEmployeeList().get(0);
        AddressBook before = new AddressBook(model.getAddressBook());
        CommandResult expected = new CommandResult("Employee summary\nTotal employees: 2\n"
                + "Employees without tags: 1\n\nEmployees by tag:\n  engineering: 1\n\n"
                + "Employees may have multiple tags; tag counts can overlap.\n\nTask overview\n"
                + "Total assigned tasks: 3\nEmployees with tasks: 2\nEmployees without tasks: 0\n"
                + "Most tasks assigned to one employee: 2\n\nSalary overview\n"
                + "Total recorded salary: $0.00\nAverage salary: $0.00\nLowest salary: $0.00\nHighest salary: $0.00");

        for (String selection : new String[] {"find Alice", "filter t/engineering"}) {
            logic.execute(selection);
            Path dataFile = temporaryFolder.resolve("addressBook.json");
            String savedData = Files.readString(dataFile);
            for (String command : new String[] {SummaryCommand.COMMAND_WORD, SummaryCommand.COMMAND_ALIAS}) {
                assertEquals(expected, logic.execute(command));
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
        logic.execute("add-task 1 d/Report");
        AddressBook before = new AddressBook(model.getAddressBook());
        Path dataFile = temporaryFolder.resolve("addressBook.json");
        String savedData = Files.readString(dataFile);

        assertTrue(logic.execute("summary").getFeedbackToUser().contains("Total assigned tasks: 2\n"
                + "Employees with tasks: 2\nEmployees without tasks: 0\nMost tasks assigned to one employee: 1"));
        assertEquals(before, model.getAddressBook());
        assertEquals(savedData, Files.readString(dataFile));

        logic.execute("delete-task 1 i/1");
        before = new AddressBook(model.getAddressBook());
        savedData = Files.readString(dataFile);
        assertTrue(logic.execute("summary").getFeedbackToUser().contains("Total assigned tasks: 1\n"
                + "Employees with tasks: 1\nEmployees without tasks: 1\nMost tasks assigned to one employee: 1"));
        assertEquals(before, model.getAddressBook());
        assertEquals(savedData, Files.readString(dataFile));
        JsonAddressBookStorage storage = new JsonAddressBookStorage(dataFile);
        assertEquals(before, new AddressBook(storage.readAddressBook().orElseThrow()));
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
    public void getFilteredEmployeeList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> logic.getFilteredEmployeeList().remove(0));
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
                        expected.getSalary(), Messages.format(expected)));

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
        Employee expectedEmployee = new EmployeeBuilder(AMY).withoutEmployeeId().withTags().build();
        ModelManager expectedModel = new ModelManager();
        expectedModel.addEmployee(expectedEmployee);
        assertCommandFailure(addCommand, CommandException.class, expectedMessage, expectedModel);
    }
}
