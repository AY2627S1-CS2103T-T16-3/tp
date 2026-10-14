package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalEmployees.ALICE;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.employee.EmployeeId;
import seedu.address.model.leave.Leave;
import seedu.address.model.leave.LeaveId;
import seedu.address.model.leave.LeavePeriod;

public class DeleteLeaveCommandTest {

    private static final LeavePeriod MONDAY_TO_WEDNESDAY = new LeavePeriod(
            LocalDate.of(2026, 10, 5), LocalDate.of(2026, 10, 7));

    @Test
    public void constructor_nullLeaveId_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new DeleteLeaveCommand(null));
    }

    @Test
    public void execute_existingLeave_deletesAndRestoresEntitlement() throws Exception {
        AddressBook addressBook = new AddressBook();
        addressBook.addEmployee(ALICE);
        Leave leave = addressBook.addLeave(new EmployeeId(1), MONDAY_TO_WEDNESDAY);
        Model model = new ModelManager(addressBook, new UserPrefs());
        String expectedMessage = String.format(DeleteLeaveCommand.MESSAGE_SUCCESS, leave.getLeaveId(),
                ALICE.getName(), MONDAY_TO_WEDNESDAY, ALICE.getLeaveEntitlement().value, 2026);

        CommandResult result = new DeleteLeaveCommand(leave.getLeaveId()).execute(model);

        assertEquals(expectedMessage, result.getFeedbackToUser());
        assertEquals(0, model.getAddressBook().getLeaveList().size());
        assertEquals(ALICE.getLeaveEntitlement().value, model.getRemainingLeave(new EmployeeId(1), 2026));
        assertEquals(2, model.getAddressBook().getNextLeaveId());
    }

    @Test
    public void execute_unknownLeaveId_throwsCommandException() {
        Model model = new ModelManager();
        LeaveId leaveId = new LeaveId(1);

        assertCommandFailure(new DeleteLeaveCommand(leaveId), model,
                String.format(DeleteLeaveCommand.MESSAGE_LEAVE_NOT_FOUND, leaveId));
    }

    @Test
    public void equals() {
        DeleteLeaveCommand command = new DeleteLeaveCommand(new LeaveId(1));

        assertEquals(command, command);
        assertEquals(command, new DeleteLeaveCommand(new LeaveId(1)));
        assertNotEquals(command, new DeleteLeaveCommand(new LeaveId(2)));
        assertNotEquals(command, null);
    }

    @Test
    public void toStringMethod() {
        LeaveId leaveId = new LeaveId(1);
        DeleteLeaveCommand command = new DeleteLeaveCommand(leaveId);

        assertEquals(DeleteLeaveCommand.class.getCanonicalName() + "{leaveId=" + leaveId + "}",
                command.toString());
    }
}
