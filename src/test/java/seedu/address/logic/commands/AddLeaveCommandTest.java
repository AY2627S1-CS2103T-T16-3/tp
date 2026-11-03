package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalEmployees.ALICE;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_EMPLOYEE;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_EMPLOYEE;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.employee.Employee;
import seedu.address.model.employee.EmployeeId;
import seedu.address.model.leave.Leave;
import seedu.address.model.leave.LeaveId;
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
        assertEquals("Leave recorded: Leave ID 1 for Alice Pauline (alice@example.com); "
                + "period 05-10-2026 to 07-10-2026; "
                + "3 working day(s); 11 day(s) remaining in 2026.", result.getFeedbackToUser());
        assertEquals(new LeaveId(1), addedLeave.getLeaveId());
    }

    @Test
    public void execute_employeesWithSameName_showsSelectedEmployeesEmail() throws Exception {
        Employee otherAlice = new EmployeeBuilder(ALICE).withEmployeeId(2)
                .withPhone("91234567").withEmail("alice.two@example.com").build();
        Model model = new ModelManager(new AddressBook(List.of(ALICE, otherAlice), 3), new UserPrefs());

        CommandResult result = new AddLeaveCommand(INDEX_SECOND_EMPLOYEE, MONDAY_TO_WEDNESDAY).execute(model);

        assertEquals("Leave recorded: Leave ID 1 for Alice Pauline (alice.two@example.com); "
                + "period 05-10-2026 to 07-10-2026; 3 working day(s); 11 day(s) remaining in 2026.",
                result.getFeedbackToUser());
        assertEquals(new EmployeeId(2), model.getAddressBook().getLeaveList().get(0).getEmployeeId());
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
        String expectedMessage = "Amy Bee (amy@gmail.com) has only 2 annual leave day(s) remaining in 2026, "
                + "but this request needs 3.";

        assertCommandFailure(new AddLeaveCommand(INDEX_FIRST_EMPLOYEE, MONDAY_TO_WEDNESDAY), model,
                expectedMessage);
    }

    @Test
    public void execute_leaveIdExhausted_throwsCommandException() {
        AddressBook addressBook = new AddressBook(List.of(ALICE), 2, List.of(),
                (long) Integer.MAX_VALUE + 1);
        Model model = new ModelManager(addressBook, new UserPrefs());

        assertCommandFailure(new AddLeaveCommand(INDEX_FIRST_EMPLOYEE, MONDAY_TO_WEDNESDAY), model,
                AddLeaveCommand.MESSAGE_LEAVE_ID_EXHAUSTED);
    }

    @Test
    public void equals() {
        AddLeaveCommand command = new AddLeaveCommand(INDEX_FIRST_EMPLOYEE, MONDAY_TO_WEDNESDAY);
        LeavePeriod differentPeriod = new LeavePeriod(
                LocalDate.of(2026, 10, 8), LocalDate.of(2026, 10, 9));

        assertEquals(command, command);
        assertEquals(command, new AddLeaveCommand(INDEX_FIRST_EMPLOYEE, MONDAY_TO_WEDNESDAY));
        assertNotEquals(command, new AddLeaveCommand(INDEX_SECOND_EMPLOYEE, MONDAY_TO_WEDNESDAY));
        assertNotEquals(command, new AddLeaveCommand(INDEX_FIRST_EMPLOYEE, differentPeriod));
        assertNotEquals(command, null);
    }

    @Test
    public void toStringMethod() {
        AddLeaveCommand command = new AddLeaveCommand(INDEX_FIRST_EMPLOYEE, MONDAY_TO_WEDNESDAY);
        String expected = AddLeaveCommand.class.getCanonicalName()
                + "{employeeIndex=" + INDEX_FIRST_EMPLOYEE + ", leavePeriod=" + MONDAY_TO_WEDNESDAY + "}";

        assertEquals(expected, command.toString());
    }
}
