package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.testutil.TypicalEmployees.ALICE;
import static seedu.address.testutil.TypicalEmployees.BENSON;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneOffset;
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

    private static final LocalDate FIXED_DATE = LocalDate.of(2026, 10, 6);
    private static final Clock FIXED_CLOCK = Clock.fixed(
            FIXED_DATE.atStartOfDay().toInstant(ZoneOffset.UTC), ZoneOffset.UTC);
    private static final LeavePeriod PAST_PERIOD = new LeavePeriod(
            LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 2));
    private static final LeavePeriod ONGOING_PERIOD = new LeavePeriod(
            LocalDate.of(2026, 10, 5), LocalDate.of(2026, 10, 7));
    private static final LeavePeriod UPCOMING_PERIOD = new LeavePeriod(
            LocalDate.of(2026, 10, 8), LocalDate.of(2026, 10, 9));

    @Test
    public void constructor_nullClock_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ListLeaveCommand(null));
    }

    @Test
    public void execute_noLeave_returnsNoLeaveMessage() {
        Model model = new ModelManager();

        CommandResult result = new ListLeaveCommand().execute(model);

        assertEquals(ListLeaveCommand.MESSAGE_NO_LEAVE, result.getFeedbackToUser());
    }

    @Test
    public void execute_multipleLeaves_listsInIdOrderWithStatusesFromClock() {
        Leave upcomingLeave = new Leave(new LeaveId(3), new EmployeeId(1), UPCOMING_PERIOD);
        Leave pastLeave = new Leave(new LeaveId(1), new EmployeeId(1), PAST_PERIOD);
        Leave ongoingLeave = new Leave(new LeaveId(2), new EmployeeId(1), ONGOING_PERIOD);
        AddressBook addressBook = new AddressBook(List.of(ALICE), 2,
                List.of(upcomingLeave, pastLeave, ongoingLeave), 4);
        Model model = new ModelManager(addressBook, new UserPrefs());
        String expectedRecords = "ID 1 | Alice Pauline | 01-10-2026 to 02-10-2026 | 2 working day(s) | Past\n"
                + "ID 2 | Alice Pauline | 05-10-2026 to 07-10-2026 | 3 working day(s) | Ongoing\n"
                + "ID 3 | Alice Pauline | 08-10-2026 to 09-10-2026 | 2 working day(s) | Upcoming";

        CommandResult result = new ListLeaveCommand(FIXED_CLOCK).execute(model);

        assertEquals(String.format(ListLeaveCommand.MESSAGE_SUCCESS, expectedRecords), result.getFeedbackToUser());
    }

    @Test
    public void execute_filteredEmployees_listsOnlyMatchingLeaves() {
        Leave aliceLeave = new Leave(new LeaveId(1), new EmployeeId(1), PAST_PERIOD);
        Leave bensonLeave = new Leave(new LeaveId(2), new EmployeeId(2), ONGOING_PERIOD);
        AddressBook addressBook = new AddressBook(List.of(ALICE, BENSON), 3,
                List.of(aliceLeave, bensonLeave), 3);
        Model model = new ModelManager(addressBook, new UserPrefs());
        model.updateFilteredEmployeeList(employee -> employee.getName().equals(ALICE.getName()));
        String expectedRecord = "ID 1 | Alice Pauline | 01-10-2026 to 02-10-2026 | 2 working day(s) | Past";

        CommandResult result = new ListLeaveCommand(FIXED_CLOCK).execute(model);

        assertEquals(String.format(ListLeaveCommand.MESSAGE_SUCCESS, expectedRecord), result.getFeedbackToUser());
    }

    @Test
    public void execute_noLeaveForFilteredEmployees_returnsNoLeaveMessage() {
        Leave bensonLeave = new Leave(new LeaveId(1), new EmployeeId(2), ONGOING_PERIOD);
        AddressBook addressBook = new AddressBook(List.of(ALICE, BENSON), 3, List.of(bensonLeave), 2);
        Model model = new ModelManager(addressBook, new UserPrefs());
        model.updateFilteredEmployeeList(employee -> employee.getName().equals(ALICE.getName()));

        CommandResult result = new ListLeaveCommand(FIXED_CLOCK).execute(model);

        assertEquals(ListLeaveCommand.MESSAGE_NO_LEAVE, result.getFeedbackToUser());
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
