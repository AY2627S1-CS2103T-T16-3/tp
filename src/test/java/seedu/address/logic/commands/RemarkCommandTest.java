package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showEmployeeAtIndex;
import static seedu.address.testutil.TypicalEmployees.getTypicalAddressBook;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_EMPLOYEE;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.employee.Employee;
import seedu.address.testutil.EmployeeBuilder;

public class RemarkCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_addRemarkFilteredList_success() {
        showEmployeeAtIndex(model, INDEX_FIRST_EMPLOYEE);
        assertRemarkSuccess("Likes to swim", RemarkCommand.MESSAGE_ADD_REMARK_SUCCESS);
    }

    @Test
    public void execute_removeExistingRemark_success() {
        Employee original = model.getFilteredEmployeeList().get(0);
        model.setEmployee(original, new EmployeeBuilder(original).withRemark("Likes to swim").build());
        assertRemarkSuccess("", RemarkCommand.MESSAGE_DELETE_REMARK_SUCCESS);
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        Index index = Index.fromOneBased(model.getFilteredEmployeeList().size() + 1);
        assertCommandFailure(new RemarkCommand(index, "Likes to swim"), model,
                Messages.MESSAGE_INVALID_EMPLOYEE_DISPLAYED_INDEX);
    }

    private void assertRemarkSuccess(String remark, String message) {
        Employee original = model.getFilteredEmployeeList().get(0);
        Employee edited = new EmployeeBuilder(original).withRemark(remark).build();
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setEmployee(original, edited);
        assertCommandSuccess(new RemarkCommand(INDEX_FIRST_EMPLOYEE, remark), model,
                String.format(message, Messages.format(edited)), expectedModel);
    }
}
