package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalEmployees.ALICE;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

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
        String expectedMessage = "Deleted leave: Leave ID 1 for Alice Pauline (alice@example.com); period "
                + "05-10-2026 to 07-10-2026; 14 day(s) remaining in 2026.";

        CommandResult result = new DeleteLeaveCommand(leave.getLeaveId()).execute(model);

        assertEquals(expectedMessage, result.getFeedbackToUser());
        assertEquals(0, model.getAddressBook().getLeaveList().size());
        assertEquals(ALICE.getLeaveEntitlement().value, model.getRemainingLeave(new EmployeeId(1), 2026));
        assertEquals(2, model.getAddressBook().getNextLeaveId());
    }

    @Test
    public void execute_employeesWithSameName_showsDeletedRecordsEmployeesEmail() throws Exception {
        Employee otherAlice = new EmployeeBuilder(ALICE).withEmployeeId(2)
                .withPhone("91234567").withEmail("alice.two@example.com").build();
        Leave firstLeave = new Leave(new LeaveId(1), new EmployeeId(1), MONDAY_TO_WEDNESDAY);
        Leave secondLeave = new Leave(new LeaveId(2), new EmployeeId(2), MONDAY_TO_WEDNESDAY);
        Model model = new ModelManager(new AddressBook(List.of(ALICE, otherAlice), 3,
                List.of(firstLeave, secondLeave), 3), new UserPrefs());

        CommandResult result = new DeleteLeaveCommand(new LeaveId(2)).execute(model);

        assertEquals("Deleted leave: Leave ID 2 for Alice Pauline (alice.two@example.com); period "
                + "05-10-2026 to 07-10-2026; 14 day(s) remaining in 2026.", result.getFeedbackToUser());
        assertEquals(List.of(firstLeave), model.getAddressBook().getLeaveList());
    }

    @Test
    public void execute_unknownLeaveId_throwsCommandException() {
        Model model = new ModelManager();
        LeaveId leaveId = new LeaveId(1);

        assertCommandFailure(new DeleteLeaveCommand(leaveId), model, "No leave record found with Leave ID 1.");
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
