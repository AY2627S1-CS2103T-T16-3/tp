package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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

public class DeleteTaskCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validTaskIndex_deletesTask() {
        Employee employee = model.getFilteredEmployeeList().get(0);
        Employee employeeWithTasks = employee.addTask(new Task("Prepare report"))
                .addTask(new Task("Submit report"));
        model.setEmployee(employee, employeeWithTasks);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        Employee expectedEmployee = expectedModel.getFilteredEmployeeList().get(0);
        expectedModel.setEmployee(expectedEmployee, expectedEmployee.deleteTask(0));

        assertCommandSuccess(new DeleteTaskCommand(Index.fromOneBased(1), Index.fromOneBased(1)),
                model, String.format(DeleteTaskCommand.MESSAGE_SUCCESS,
                        employee.getName().fullName, 1, "[ ] Prepare report"), expectedModel);
    }

    @Test
    public void execute_invalidEmployeeIndex_throwsCommandException() {
        Index invalidIndex = Index.fromOneBased(model.getFilteredEmployeeList().size() + 1);
        assertCommandFailure(new DeleteTaskCommand(invalidIndex, Index.fromOneBased(1)), model,
                Messages.MESSAGE_INVALID_EMPLOYEE_DISPLAYED_INDEX);
    }

    @Test
    public void execute_invalidTaskIndex_throwsCommandException() {
        Employee employee = model.getFilteredEmployeeList().get(0);
        model.setEmployee(employee, employee.addTask(new Task("Prepare report")));

        assertCommandFailure(new DeleteTaskCommand(Index.fromOneBased(1), Index.fromOneBased(2)),
                model, DeleteTaskCommand.MESSAGE_INVALID_TASK_INDEX);
    }

    @Test
    public void execute_noTasks_throwsCommandException() {
        assertCommandFailure(new DeleteTaskCommand(Index.fromOneBased(1), Index.fromOneBased(1)),
                model, DeleteTaskCommand.MESSAGE_INVALID_TASK_INDEX);
    }

    @Test
    public void execute_filteredList_preservesFilter() throws CommandException {
        Employee employee = TypicalEmployees.BENSON;
        Employee otherEmployee = TypicalEmployees.ALICE;
        Employee employeeWithTask = employee.addTask(new Task("Prepare report"));
        model.setEmployee(employee, employeeWithTask);
        model.updateFilteredEmployeeList(candidate -> candidate.getName().equals(employeeWithTask.getName()));

        new DeleteTaskCommand(Index.fromOneBased(1), Index.fromOneBased(1)).execute(model);

        assertEquals(1, model.getFilteredEmployeeList().size());
        assertTrue(model.getFilteredEmployeeList().get(0).getTasks().isEmpty());
        assertTrue(model.getAddressBook().getEmployeeList().contains(otherEmployee));
    }

    @Test
    public void equals_sameValues_returnsTrue() {
        DeleteTaskCommand command = new DeleteTaskCommand(Index.fromOneBased(1), Index.fromOneBased(1));
        assertEquals(command, new DeleteTaskCommand(Index.fromOneBased(1), Index.fromOneBased(1)));
    }

    @Test
    public void equals_differentValues_returnsFalse() {
        DeleteTaskCommand command = new DeleteTaskCommand(Index.fromOneBased(1), Index.fromOneBased(1));
        assertNotEquals(command, new DeleteTaskCommand(Index.fromOneBased(2), Index.fromOneBased(1)));
        assertNotEquals(command, new DeleteTaskCommand(Index.fromOneBased(1), Index.fromOneBased(2)));
        assertNotEquals(command, new Object());
    }

    @Test
    public void hashCode_sameValues_returnsSameHashCode() {
        DeleteTaskCommand command = new DeleteTaskCommand(Index.fromOneBased(1), Index.fromOneBased(1));
        assertEquals(command.hashCode(),
                new DeleteTaskCommand(Index.fromOneBased(1), Index.fromOneBased(1)).hashCode());
    }
}
