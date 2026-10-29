package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalEmployees.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.employee.Employee;
import seedu.address.model.employee.Task;
import seedu.address.testutil.TypicalEmployees;

public class AddTaskCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validTask_appendsTask() {
        Employee employee = model.getFilteredEmployeeList().get(0);
        Employee expectedEmployee = employee.addTask(new Task("Prepare report"));
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setEmployee(employee, expectedEmployee);

        assertCommandSuccess(new AddTaskCommand(Index.fromOneBased(1), new Task("Prepare report")),
                model, String.format(AddTaskCommand.MESSAGE_SUCCESS,
                        employee.getName().fullName, 1, "[ ] Prepare report"), expectedModel);
    }

    @Test
    public void execute_validTaskWithWhitespaceAndPunctuation_appendsTask() {
        String details = "                   task......Task                 ";
        Employee employee = model.getFilteredEmployeeList().get(0);
        Employee expectedEmployee = employee.addTask(new Task(details));
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setEmployee(employee, expectedEmployee);

        assertCommandSuccess(new AddTaskCommand(Index.fromOneBased(1), new Task(details)),
                model, String.format(AddTaskCommand.MESSAGE_SUCCESS,
                        employee.getName().fullName, 1, "[ ] task......Task"), expectedModel);
    }

    @Test
    public void execute_validTaskWithLeadingWhitespace_appendsTrimmedTask() {
        String details = " ".repeat(Task.MAX_DETAILS_LENGTH) + "T";
        Employee employee = model.getFilteredEmployeeList().get(0);
        Employee expectedEmployee = employee.addTask(new Task(details));
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setEmployee(employee, expectedEmployee);

        assertCommandSuccess(new AddTaskCommand(Index.fromOneBased(1), new Task(details)),
                model, String.format(AddTaskCommand.MESSAGE_SUCCESS,
                        employee.getName().fullName, 1, "[ ] T"), expectedModel);
    }

    @Test
    public void execute_invalidTask_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new Task("            "));
        assertThrows(IllegalArgumentException.class, () -> new Task(
                "a".repeat(Task.MAX_DETAILS_LENGTH + 1)));
    }

    @Test
    public void execute_duplicateTask_throwsCommandException() {
        Employee employee = model.getFilteredEmployeeList().get(0);
        model.setEmployee(employee, employee.addTask(new Task("Prepare report")));

        assertCommandFailure(new AddTaskCommand(Index.fromOneBased(1), new Task(" prepare  REPORT ")),
                model, AddTaskCommand.MESSAGE_DUPLICATE_TASK);
    }

    @Test
    public void execute_duplicateTaskWithUnicodeWhitespace_throwsCommandException() {
        Employee employee = model.getFilteredEmployeeList().get(0);
        model.setEmployee(employee, employee.addTask(new Task("Prepare report")));

        assertCommandFailure(new AddTaskCommand(Index.fromOneBased(1), new Task("Prepare\u2003\u2003report")),
                model, AddTaskCommand.MESSAGE_DUPLICATE_TASK);
    }

    @Test
    public void execute_invalidEmployeeIndex_throwsCommandException() {
        Index invalidIndex = Index.fromOneBased(model.getFilteredEmployeeList().size() + 1);
        assertCommandFailure(new AddTaskCommand(invalidIndex, new Task("Prepare report")), model,
                Messages.MESSAGE_INVALID_EMPLOYEE_DISPLAYED_INDEX);
    }

    @Test
    public void execute_filteredList_preservesFilter() throws CommandException {
        Employee employee = TypicalEmployees.BENSON;
        Employee otherEmployee = TypicalEmployees.ALICE;
        model.updateFilteredEmployeeList(candidate -> candidate.getName().equals(employee.getName()));

        new AddTaskCommand(Index.fromOneBased(1), new Task("Prepare report")).execute(model);

        assertEquals(1, model.getFilteredEmployeeList().size());
        assertEquals("Prepare report", model.getFilteredEmployeeList().get(0).getTasks().get(0).getDetails());
        assertTrue(model.getAddressBook().getEmployeeList().contains(otherEmployee));
    }

    @Test
    public void equals_sameValues_returnsTrue() {
        AddTaskCommand command = new AddTaskCommand(Index.fromOneBased(1), new Task("Prepare report"));
        assertEquals(command, new AddTaskCommand(Index.fromOneBased(1), new Task("Prepare report")));
    }

    @Test
    public void equals_differentValues_returnsFalse() {
        AddTaskCommand command = new AddTaskCommand(Index.fromOneBased(1), new Task("Prepare report"));
        assertNotEquals(command, new AddTaskCommand(Index.fromOneBased(2), new Task("Prepare report")));
        assertNotEquals(command, new AddTaskCommand(Index.fromOneBased(1), new Task("Submit report")));
        assertNotEquals(command, new Object());
    }

    @Test
    public void hashCode_sameValues_returnsSameHashCode() {
        AddTaskCommand command = new AddTaskCommand(Index.fromOneBased(1), new Task("Prepare report"));
        assertEquals(command.hashCode(),
                new AddTaskCommand(Index.fromOneBased(1), new Task("Prepare report")).hashCode());
    }
}
