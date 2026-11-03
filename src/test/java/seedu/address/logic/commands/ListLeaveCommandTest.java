package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.testutil.TypicalEmployees.ALICE;
import static seedu.address.testutil.TypicalEmployees.BENSON;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Comparator;
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
    public void execute_emptyAddressBook_returnsNoEmployeesMessage() {
        Model model = new ModelManager();

        CommandResult result = new ListLeaveCommand().execute(model);

        assertEquals(ListLeaveCommand.MESSAGE_NO_EMPLOYEES, result.getFeedbackToUser());
    }

    @Test
    public void execute_noDisplayedEmployees_returnsNoEmployeesMessageWithoutChangingRecords() {
        Model model = new ModelManager(new AddressBook(List.of(ALICE), 2), new UserPrefs());
        model.updateFilteredEmployeeList(employee -> false);
        AddressBook before = new AddressBook(model.getAddressBook());

        CommandResult result = new ListLeaveCommand(FIXED_CLOCK).execute(model);

        assertEquals(ListLeaveCommand.MESSAGE_NO_EMPLOYEES, result.getFeedbackToUser());
        assertEquals(before, model.getAddressBook());
        assertEquals(List.of(), model.getFilteredEmployeeList());
    }

    @Test
    public void execute_noLeave_showsFullEntitlement() {
        Model model = new ModelManager(new AddressBook(List.of(ALICE), 2), new UserPrefs());

        CommandResult result = new ListLeaveCommand(FIXED_CLOCK).execute(model);

        assertEquals("Remaining annual leave (2026):\nAlice Pauline (alice@example.com): 14 of 14 day(s) remaining.\n\n"
                + ListLeaveCommand.MESSAGE_NO_LEAVE, result.getFeedbackToUser());
    }

    @Test
    public void execute_multipleLeaves_listsInIdOrderWithStatusesFromClock() {
        Leave upcomingLeave = new Leave(new LeaveId(3), new EmployeeId(1), UPCOMING_PERIOD);
        Leave pastLeave = new Leave(new LeaveId(1), new EmployeeId(1), PAST_PERIOD);
        Leave ongoingLeave = new Leave(new LeaveId(2), new EmployeeId(1), ONGOING_PERIOD);
        AddressBook addressBook = new AddressBook(List.of(ALICE), 2,
                List.of(upcomingLeave, pastLeave, ongoingLeave), 4);
        Model model = new ModelManager(addressBook, new UserPrefs());
        String expectedRecords = "Leave ID 1 | Alice Pauline (alice@example.com) | "
                + "01-10-2026 to 02-10-2026 | 2 working day(s) | Past\n"
                + "Leave ID 2 | Alice Pauline (alice@example.com) | "
                + "05-10-2026 to 07-10-2026 | 3 working day(s) | Ongoing\n"
                + "Leave ID 3 | Alice Pauline (alice@example.com) | "
                + "08-10-2026 to 09-10-2026 | 2 working day(s) | Upcoming";

        CommandResult result = new ListLeaveCommand(FIXED_CLOCK).execute(model);

        assertEquals("Remaining annual leave (2026):\nAlice Pauline (alice@example.com): 7 of 14 day(s) remaining.\n\n"
                + String.format(ListLeaveCommand.MESSAGE_SUCCESS, expectedRecords), result.getFeedbackToUser());
    }

    @Test
    public void execute_filteredEmployees_listsOnlyMatchingLeaves() {
        Leave aliceLeave = new Leave(new LeaveId(1), new EmployeeId(1), PAST_PERIOD);
        Leave bensonLeave = new Leave(new LeaveId(2), new EmployeeId(2), ONGOING_PERIOD);
        AddressBook addressBook = new AddressBook(List.of(ALICE, BENSON), 3,
                List.of(aliceLeave, bensonLeave), 3);
        Model model = new ModelManager(addressBook, new UserPrefs());
        model.updateFilteredEmployeeList(employee -> employee.getName().equals(ALICE.getName()));
        String expectedRecord = "Leave ID 1 | Alice Pauline (alice@example.com) | "
                + "01-10-2026 to 02-10-2026 | 2 working day(s) | Past";

        CommandResult result = new ListLeaveCommand(FIXED_CLOCK).execute(model);

        assertEquals("Remaining annual leave (2026):\nAlice Pauline (alice@example.com): 12 of 14 day(s) remaining.\n\n"
                + String.format(ListLeaveCommand.MESSAGE_SUCCESS, expectedRecord), result.getFeedbackToUser());
    }

    @Test
    public void execute_noLeaveForFilteredEmployees_showsTheirFullEntitlement() {
        Leave bensonLeave = new Leave(new LeaveId(1), new EmployeeId(2), ONGOING_PERIOD);
        AddressBook addressBook = new AddressBook(List.of(ALICE, BENSON), 3, List.of(bensonLeave), 2);
        Model model = new ModelManager(addressBook, new UserPrefs());
        model.updateFilteredEmployeeList(employee -> employee.getName().equals(ALICE.getName()));

        CommandResult result = new ListLeaveCommand(FIXED_CLOCK).execute(model);

        assertEquals("Remaining annual leave (2026):\nAlice Pauline (alice@example.com): 14 of 14 day(s) remaining.\n\n"
                + ListLeaveCommand.MESSAGE_NO_LEAVE, result.getFeedbackToUser());
    }

    @Test
    public void execute_leaveInDifferentYears_calculatesOnlyClockYearAndRetainsAllRecords() {
        Leave currentLeave = new Leave(new LeaveId(1), new EmployeeId(1), PAST_PERIOD);
        Leave nextYearLeave = new Leave(new LeaveId(2), new EmployeeId(1), new LeavePeriod(
                LocalDate.of(2027, 1, 4), LocalDate.of(2027, 1, 6)));
        Model model = new ModelManager(new AddressBook(List.of(ALICE), 2,
                List.of(currentLeave, nextYearLeave), 3), new UserPrefs());
        String records = "Leave records:\n"
                + "Leave ID 1 | Alice Pauline (alice@example.com) | "
                + "01-10-2026 to 02-10-2026 | 2 working day(s) | Past\n"
                + "Leave ID 2 | Alice Pauline (alice@example.com) | "
                + "04-01-2027 to 06-01-2027 | 3 working day(s) | Upcoming";

        assertEquals("Remaining annual leave (2026):\n"
                + "Alice Pauline (alice@example.com): 12 of 14 day(s) remaining.\n\n" + records,
                new ListLeaveCommand(FIXED_CLOCK).execute(model).getFeedbackToUser());

        Clock nextYearClock = Clock.fixed(LocalDate.of(2027, 1, 1).atStartOfDay().toInstant(ZoneOffset.UTC),
                ZoneOffset.UTC);
        assertEquals("Remaining annual leave (2027):\n"
                + "Alice Pauline (alice@example.com): 11 of 14 day(s) remaining.\n\n" + records,
                new ListLeaveCommand(nextYearClock).execute(model).getFeedbackToUser());
    }

    @Test
    public void execute_onlyOtherYearLeaves_showsFullCurrentYearEntitlementAndRetainsRecords() {
        Leave previousYearLeave = new Leave(new LeaveId(1), new EmployeeId(1), new LeavePeriod(
                LocalDate.of(2025, 10, 1), LocalDate.of(2025, 10, 2)));
        Leave nextYearLeave = new Leave(new LeaveId(2), new EmployeeId(1), new LeavePeriod(
                LocalDate.of(2027, 1, 4), LocalDate.of(2027, 1, 6)));
        Model model = new ModelManager(new AddressBook(List.of(ALICE), 2,
                List.of(previousYearLeave, nextYearLeave), 3), new UserPrefs());

        CommandResult result = new ListLeaveCommand(FIXED_CLOCK).execute(model);

        assertEquals("Remaining annual leave (2026):\nAlice Pauline (alice@example.com): 14 of 14 day(s) remaining.\n\n"
                + "Leave records:\n"
                + "Leave ID 1 | Alice Pauline (alice@example.com) | "
                + "01-10-2025 to 02-10-2025 | 2 working day(s) | Past\n"
                + "Leave ID 2 | Alice Pauline (alice@example.com) | "
                + "04-01-2027 to 06-01-2027 | 3 working day(s) | Upcoming",
                result.getFeedbackToUser());
    }

    @Test
    public void execute_leaveForOneEmployee_doesNotReduceAnotherEmployeesBalance() {
        Leave bensonLeave = new Leave(new LeaveId(1), new EmployeeId(2), ONGOING_PERIOD);
        Model model = new ModelManager(new AddressBook(List.of(ALICE, BENSON), 3,
                List.of(bensonLeave), 2), new UserPrefs());

        CommandResult result = new ListLeaveCommand(FIXED_CLOCK).execute(model);

        assertEquals("Remaining annual leave (2026):\nAlice Pauline (alice@example.com): 14 of 14 day(s) remaining.\n"
                + "Benson Meier (johnd@example.com): 11 of 14 day(s) remaining.\n\n"
                + "Leave records:\nLeave ID 1 | Benson Meier (johnd@example.com) | "
                + "05-10-2026 to 07-10-2026 | 3 working day(s) | Ongoing",
                result.getFeedbackToUser());
    }

    @Test
    public void execute_employeesWithSameName_showsDistinctEmailsForBalancesAndRecords() {
        Employee otherAlice = new EmployeeBuilder(ALICE).withEmployeeId(2)
                .withPhone("91234567").withEmail("alice.two@example.com").build();
        Leave firstLeave = new Leave(new LeaveId(1), new EmployeeId(1), PAST_PERIOD);
        Leave secondLeave = new Leave(new LeaveId(2), new EmployeeId(2), ONGOING_PERIOD);
        Model model = new ModelManager(new AddressBook(List.of(ALICE, otherAlice), 3,
                List.of(firstLeave, secondLeave), 3), new UserPrefs());

        CommandResult result = new ListLeaveCommand(FIXED_CLOCK).execute(model);

        assertEquals("Remaining annual leave (2026):\n"
                + "Alice Pauline (alice@example.com): 12 of 14 day(s) remaining.\n"
                + "Alice Pauline (alice.two@example.com): 11 of 14 day(s) remaining.\n\n"
                + "Leave records:\n"
                + "Leave ID 1 | Alice Pauline (alice@example.com) | "
                + "01-10-2026 to 02-10-2026 | 2 working day(s) | Past\n"
                + "Leave ID 2 | Alice Pauline (alice.two@example.com) | "
                + "05-10-2026 to 07-10-2026 | 3 working day(s) | Ongoing",
                result.getFeedbackToUser());
    }

    @Test
    public void execute_entitlementReducedBelowRecordedLeave_showsZeroRemaining() {
        Employee employee = new EmployeeBuilder(ALICE).withLeaveEntitlement(1).build();
        Leave leave = new Leave(new LeaveId(1), new EmployeeId(1), PAST_PERIOD);
        Model model = new ModelManager(new AddressBook(List.of(employee), 2, List.of(leave), 2), new UserPrefs());

        CommandResult result = new ListLeaveCommand(FIXED_CLOCK).execute(model);

        assertEquals("Remaining annual leave (2026):\nAlice Pauline (alice@example.com): 0 of 1 day(s) remaining.\n\n"
                + "Leave records:\nLeave ID 1 | Alice Pauline (alice@example.com) | "
                + "01-10-2026 to 02-10-2026 | 2 working day(s) | Past",
                result.getFeedbackToUser());
    }

    @Test
    public void execute_zeroEntitlementAndNoLeave_showsZeroRemaining() {
        Employee employee = new EmployeeBuilder(ALICE).withLeaveEntitlement(0).build();
        Model model = new ModelManager(new AddressBook(List.of(employee), 2), new UserPrefs());

        assertEquals("Remaining annual leave (2026):\nAlice Pauline (alice@example.com): 0 of 0 day(s) remaining.\n\n"
                + ListLeaveCommand.MESSAGE_NO_LEAVE,
                new ListLeaveCommand(FIXED_CLOCK).execute(model).getFeedbackToUser());
    }

    @Test
    public void execute_sortedFilteredEmployees_preservesViewRecordsAndIdCounters() {
        Employee benson = new EmployeeBuilder(BENSON).withLeaveEntitlement(21).build();
        Employee hidden = new EmployeeBuilder().withName("Hidden Employee").withEmployeeId(3)
                .withLeaveEntitlement(0).build();
        Model model = new ModelManager(new AddressBook(List.of(ALICE, benson, hidden), 20, List.of(), 30),
                new UserPrefs());
        model.updateFilteredEmployeeList(employee -> employee.getLeaveEntitlement().value > 0);
        model.sortFilteredEmployeeList(Comparator.comparing((Employee employee) -> employee.getName().fullName)
                .reversed());
        AddressBook before = new AddressBook(model.getAddressBook());
        var displayed = model.getFilteredEmployeeList();

        CommandResult result = new ListLeaveCommand(FIXED_CLOCK).execute(model);

        assertEquals("Remaining annual leave (2026):\nBenson Meier (johnd@example.com): 21 of 21 day(s) remaining.\n"
                + "Alice Pauline (alice@example.com): 14 of 14 day(s) remaining.\n\n"
                + ListLeaveCommand.MESSAGE_NO_LEAVE,
                result.getFeedbackToUser());
        assertEquals(before, model.getAddressBook());
        assertSame(displayed, model.getFilteredEmployeeList());
        assertEquals(List.of(benson, ALICE), displayed);
        assertEquals(20, model.getAddressBook().getNextEmployeeId());
        assertEquals(30, model.getAddressBook().getNextLeaveId());
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
