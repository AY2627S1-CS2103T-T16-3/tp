package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalEmployees.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.employee.Employee;
import seedu.address.model.employee.Task;

public class MarkTaskCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_pendingTask_marksTaskAsDone() {
        Employee employee = model.getFilteredEmployeeList().get(0);
        model.setEmployee(employee, employee.addTask(new Task("Prepare report")));
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        Employee expectedEmployee = expectedModel.getFilteredEmployeeList().get(0);
        expectedModel.setEmployee(expectedEmployee,
                expectedEmployee.updateTask(0, expectedEmployee.getTasks().get(0).markDone()));

        assertCommandSuccess(new MarkTaskCommand(Index.fromOneBased(1), Index.fromOneBased(1)), model,
                String.format(MarkTaskCommand.MESSAGE_SUCCESS, employee.getName().fullName, 1,
                        new Task("Prepare report").markDone()), expectedModel);
    }

    @Test
    public void execute_completedTask_isIdempotent() {
        Employee employee = model.getFilteredEmployeeList().get(0);
        model.setEmployee(employee, employee.addTask(new Task("Prepare report").markDone()));
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        assertCommandSuccess(new MarkTaskCommand(Index.fromOneBased(1), Index.fromOneBased(1)), model,
                String.format(MarkTaskCommand.MESSAGE_SUCCESS, employee.getName().fullName, 1,
                        new Task("Prepare report").markDone()), expectedModel);
    }

    @Test
    public void execute_invalidIndexes_throwsCommandException() {
        assertCommandFailure(new MarkTaskCommand(Index.fromOneBased(model.getFilteredEmployeeList().size() + 1),
                Index.fromOneBased(1)), model, Messages.MESSAGE_INVALID_EMPLOYEE_DISPLAYED_INDEX);
        assertCommandFailure(new MarkTaskCommand(Index.fromOneBased(1), Index.fromOneBased(1)), model,
                MarkTaskCommand.MESSAGE_INVALID_TASK_INDEX);
    }

    @Test
    public void equals() {
        MarkTaskCommand command = new MarkTaskCommand(Index.fromOneBased(1), Index.fromOneBased(1));
        assertEquals(command, command);
        assertEquals(command, new MarkTaskCommand(Index.fromOneBased(1), Index.fromOneBased(1)));
        assertNotEquals(command, new MarkTaskCommand(Index.fromOneBased(2), Index.fromOneBased(1)));
        assertNotEquals(command, new MarkTaskCommand(Index.fromOneBased(1), Index.fromOneBased(2)));
        assertNotEquals(command, new Object());
    }
}
