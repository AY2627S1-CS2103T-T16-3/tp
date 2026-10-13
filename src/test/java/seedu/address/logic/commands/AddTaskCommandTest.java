package seedu.address.logic.commands;

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
                        employee.getName().fullName, "Prepare report"), expectedModel);
    }

    @Test
    public void execute_duplicateTask_throwsCommandException() {
        Employee employee = model.getFilteredEmployeeList().get(0);
        model.setEmployee(employee, employee.addTask(new Task("Prepare report")));

        assertCommandFailure(new AddTaskCommand(Index.fromOneBased(1), new Task(" prepare  REPORT ")),
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
        Employee employee = model.getFilteredEmployeeList().get(0);
        model.updateFilteredEmployeeList(candidate -> candidate.getName().equals(employee.getName()));

        new AddTaskCommand(Index.fromOneBased(1), new Task("Prepare report")).execute(model);

        org.junit.jupiter.api.Assertions.assertEquals(1, model.getFilteredEmployeeList().size());
        org.junit.jupiter.api.Assertions.assertEquals("Prepare report",
                model.getFilteredEmployeeList().get(0).getTasks().get(0).getDetails());
    }
}
