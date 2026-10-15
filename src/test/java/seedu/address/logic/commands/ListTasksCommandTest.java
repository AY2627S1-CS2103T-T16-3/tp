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

public class ListTasksCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_employeeWithTasks_listsTasksInOrder() {
        Employee employee = model.getFilteredEmployeeList().get(0);
        Employee updatedEmployee = employee.addTask(new Task("Prepare report"))
                .addTask(new Task("Submit report"));
        model.setEmployee(employee, updatedEmployee);

        String expected = "Tasks for " + employee.getName().fullName + ":\n"
                + "1. Prepare report\n2. Submit report";
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        assertCommandSuccess(new ListTasksCommand(Index.fromOneBased(1)), model, expected, expectedModel);
    }

    @Test
    public void execute_employeeWithoutTasks_returnsEmptyMessage() {
        Employee employee = model.getFilteredEmployeeList().get(0);
        String expected = String.format(ListTasksCommand.MESSAGE_NO_TASKS, employee.getName().fullName);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        assertCommandSuccess(new ListTasksCommand(Index.fromOneBased(1)), model, expected, expectedModel);
    }

    @Test
    public void execute_invalidEmployeeIndex_throwsCommandException() {
        Index invalidIndex = Index.fromOneBased(model.getFilteredEmployeeList().size() + 1);
        assertCommandFailure(new ListTasksCommand(invalidIndex), model,
                Messages.MESSAGE_INVALID_EMPLOYEE_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        ListTasksCommand first = new ListTasksCommand(Index.fromOneBased(1));
        assertEquals(first, new ListTasksCommand(Index.fromOneBased(1)));
        assertNotEquals(first, new ListTasksCommand(Index.fromOneBased(2)));
        assertNotEquals(first, new Object());
        assertEquals(first.hashCode(), new ListTasksCommand(Index.fromOneBased(1)).hashCode());
    }
}
