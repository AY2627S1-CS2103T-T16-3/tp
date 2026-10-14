package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalEmployees.ALICE;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_EMPLOYEE;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.employee.Employee;
import seedu.address.model.leave.Leave;
import seedu.address.model.leave.LeavePeriod;
import seedu.address.testutil.EmployeeBuilder;

public class AddLeaveCommandTest {

    private static final LeavePeriod MONDAY_TO_WEDNESDAY = new LeavePeriod(
            LocalDate.of(2026, 10, 5), LocalDate.of(2026, 10, 7));

    @Test
    public void constructor_nullValue_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AddLeaveCommand(null, MONDAY_TO_WEDNESDAY));
        assertThrows(NullPointerException.class, () -> new AddLeaveCommand(INDEX_FIRST_EMPLOYEE, null));
    }

    @Test
    public void execute_validRequest_recordsLeave() throws Exception {
        AddressBook addressBook = new AddressBook();
        addressBook.addEmployee(ALICE);
        Model model = new ModelManager(addressBook, new UserPrefs());

        CommandResult result = new AddLeaveCommand(INDEX_FIRST_EMPLOYEE, MONDAY_TO_WEDNESDAY).execute(model);

        Leave addedLeave = model.getAddressBook().getLeaveList().get(0);
        int remainingDays = ALICE.getLeaveEntitlement().value - MONDAY_TO_WEDNESDAY.getWorkingDayCount();
        assertEquals(String.format(AddLeaveCommand.MESSAGE_SUCCESS, addedLeave.getLeaveId(), ALICE.getName(),
                MONDAY_TO_WEDNESDAY, 3, remainingDays, 2026), result.getFeedbackToUser());
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        Model model = new ModelManager();

        assertCommandFailure(new AddLeaveCommand(INDEX_FIRST_EMPLOYEE, MONDAY_TO_WEDNESDAY), model,
                Messages.MESSAGE_INVALID_EMPLOYEE_DISPLAYED_INDEX);
    }

    @Test
    public void execute_overlappingLeave_throwsCommandException() throws Exception {
        AddressBook addressBook = new AddressBook();
        addressBook.addEmployee(ALICE);
        Model model = new ModelManager(addressBook, new UserPrefs());
        AddLeaveCommand command = new AddLeaveCommand(INDEX_FIRST_EMPLOYEE, MONDAY_TO_WEDNESDAY);
        command.execute(model);

        assertCommandFailure(command, model, AddLeaveCommand.MESSAGE_OVERLAPPING_LEAVE);
    }

    @Test
    public void execute_insufficientEntitlement_throwsCommandException() {
        Employee employee = new EmployeeBuilder().withLeaveEntitlement(2).build();
        AddressBook addressBook = new AddressBook();
        addressBook.addEmployee(employee);
        Model model = new ModelManager(addressBook, new UserPrefs());
        String expectedMessage = String.format(AddLeaveCommand.MESSAGE_INSUFFICIENT_LEAVE,
                employee.getName(), 2, 2026, 3);

        assertCommandFailure(new AddLeaveCommand(INDEX_FIRST_EMPLOYEE, MONDAY_TO_WEDNESDAY), model,
                expectedMessage);
    }

    @Test
    public void toStringMethod() {
        AddLeaveCommand command = new AddLeaveCommand(INDEX_FIRST_EMPLOYEE, MONDAY_TO_WEDNESDAY);
        String expected = AddLeaveCommand.class.getCanonicalName()
                + "{employeeIndex=" + INDEX_FIRST_EMPLOYEE + ", leavePeriod=" + MONDAY_TO_WEDNESDAY + "}";

        assertEquals(expected, command.toString());
    }
}
