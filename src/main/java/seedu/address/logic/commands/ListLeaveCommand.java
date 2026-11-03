package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.employee.Employee;
import seedu.address.model.employee.EmployeeId;
import seedu.address.model.leave.Leave;
import seedu.address.model.leave.LeavePeriod;

/**
 * Lists current-year balances and leave records for the currently displayed employees.
 */
public class ListLeaveCommand extends Command {

    public static final String SUBCOMMAND_WORD = "list";
    public static final String MESSAGE_USAGE = "leave " + SUBCOMMAND_WORD
            + ": Shows current-year leave balances and all leave records for the displayed employees.\n"
            + "  Example: leave " + SUBCOMMAND_WORD;
    public static final String MESSAGE_NO_EMPLOYEES = "No employees displayed.";
    public static final String MESSAGE_NO_LEAVE = "No leave records found.";
    public static final String MESSAGE_SUCCESS = "Leave records:\n%1$s";

    private final Clock clock;

    /** Creates a command that determines leave statuses using the system clock. */
    public ListLeaveCommand() {
        this(Clock.systemDefaultZone());
    }

    /** Creates a command that determines leave statuses using {@code clock}. */
    ListLeaveCommand(Clock clock) {
        this.clock = requireNonNull(clock);
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        List<Employee> displayedEmployees = model.getFilteredEmployeeList();
        if (displayedEmployees.isEmpty()) {
            return new CommandResult(MESSAGE_NO_EMPLOYEES);
        }
        LocalDate today = LocalDate.now(clock);
        String balances = formatBalances(model, displayedEmployees, today.getYear());

        Map<EmployeeId, Employee> employeesById = model.getAddressBook().getEmployeeList().stream()
                .collect(Collectors.toMap(employee -> employee.getEmployeeId().orElseThrow(), employee -> employee));
        Set<EmployeeId> displayedEmployeeIds = displayedEmployees.stream()
                .map(employee -> employee.getEmployeeId().orElseThrow())
                .collect(Collectors.toSet());
        List<Leave> matchingLeaves = model.getAddressBook().getLeaveList().stream()
                .filter(leave -> displayedEmployeeIds.contains(leave.getEmployeeId()))
                .sorted(Comparator.comparingInt(leave -> leave.getLeaveId().value))
                .toList();

        if (matchingLeaves.isEmpty()) {
            return new CommandResult(balances + "\n\n" + MESSAGE_NO_LEAVE);
        }

        String records = matchingLeaves.stream()
                .map(leave -> formatLeave(leave, employeesById, today))
                .collect(Collectors.joining("\n"));
        return new CommandResult(balances + "\n\n" + String.format(MESSAGE_SUCCESS, records));
    }

    private static String formatBalances(Model model, List<Employee> employees, int year) {
        String balances = employees.stream()
                .map(employee -> String.format("%s: %d of %d day(s) remaining.",
                        Messages.formatEmployeeNameAndEmail(employee),
                        model.getRemainingLeave(employee.getEmployeeId().orElseThrow(), year),
                        employee.getLeaveEntitlement().value))
                .collect(Collectors.joining("\n"));
        return String.format("Remaining annual leave (%d):\n%s", year, balances);
    }

    private static String formatLeave(Leave leave, Map<EmployeeId, Employee> employeesById, LocalDate today) {
        Employee employee = employeesById.get(leave.getEmployeeId());
        if (employee == null) {
            throw new IllegalStateException("Leave record references an unknown employee.");
        }
        LeavePeriod period = leave.getPeriod();
        return String.format("Leave ID %s | %s | %s | %d working day(s) | %s",
                leave.getLeaveId(), Messages.formatEmployeeNameAndEmail(employee), period, period.getWorkingDayCount(),
                getStatus(period, today));
    }

    static String getStatus(LeavePeriod period, LocalDate today) {
        requireNonNull(period);
        requireNonNull(today);
        if (today.isBefore(period.getStartDate())) {
            return "Upcoming";
        }
        if (today.isAfter(period.getEndDate())) {
            return "Past";
        }
        return "Ongoing";
    }
}
