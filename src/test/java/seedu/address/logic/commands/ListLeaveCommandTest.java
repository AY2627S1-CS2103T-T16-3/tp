package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.TypicalEmployees.ALICE;
import static seedu.address.testutil.TypicalEmployees.BENSON;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.employee.EmployeeId;
import seedu.address.model.leave.Leave;
import seedu.address.model.leave.LeaveId;
import seedu.address.model.leave.LeavePeriod;

public class ListLeaveCommandTest {

    private static final LeavePeriod EARLIER_PERIOD = new LeavePeriod(
            LocalDate.of(2020, 10, 5), LocalDate.of(2020, 10, 7));
    private static final LeavePeriod LATER_PERIOD = new LeavePeriod(
            LocalDate.of(2099, 10, 5), LocalDate.of(2099, 10, 7));

    @Test
    public void execute_noLeave_returnsNoLeaveMessage() {
        Model model = new ModelManager();

        CommandResult result = new ListLeaveCommand().execute(model);

        assertEquals(ListLeaveCommand.MESSAGE_NO_LEAVE, result.getFeedbackToUser());
    }

    @Test
    public void execute_multipleLeaves_listsInIdOrderWithoutEmployeeIds() {
        Leave laterId = new Leave(new LeaveId(2), new EmployeeId(2), LATER_PERIOD);
        Leave earlierId = new Leave(new LeaveId(1), new EmployeeId(1), EARLIER_PERIOD);
        AddressBook addressBook = new AddressBook(List.of(ALICE, BENSON), 3,
                List.of(laterId, earlierId), 3);
        Model model = new ModelManager(addressBook, new UserPrefs());
        String expectedRecords = "ID 1 | Alice Pauline | 05-10-2020 to 07-10-2020 | 3 working day(s) | Past\n"
                + "ID 2 | Benson Meier | 05-10-2099 to 07-10-2099 | 3 working day(s) | Upcoming";

        CommandResult result = new ListLeaveCommand().execute(model);

        assertEquals(String.format(ListLeaveCommand.MESSAGE_SUCCESS, expectedRecords), result.getFeedbackToUser());
    }

    @Test
    public void getStatus_dateRelativeToPeriod_returnsStatus() {
        LocalDate startDate = LocalDate.of(2026, 10, 5);
        LocalDate endDate = LocalDate.of(2026, 10, 7);
        LeavePeriod period = new LeavePeriod(startDate, endDate);

        assertEquals("Upcoming", ListLeaveCommand.getStatus(period, startDate.minusDays(1)));
        assertEquals("Ongoing", ListLeaveCommand.getStatus(period, startDate));
        assertEquals("Ongoing", ListLeaveCommand.getStatus(period, endDate));
        assertEquals("Past", ListLeaveCommand.getStatus(period, endDate.plusDays(1)));
    }
}
