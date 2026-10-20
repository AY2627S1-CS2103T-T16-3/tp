package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.model.Model;
import seedu.address.model.employee.Employee;
import seedu.address.model.employee.EmployeeId;
import seedu.address.model.employee.Name;
import seedu.address.model.leave.Leave;
import seedu.address.model.leave.LeavePeriod;

/**
 * Lists all centrally stored leave records.
 */
public class ListLeaveCommand extends Command {

    public static final String SUBCOMMAND_WORD = "list";
    public static final String MESSAGE_USAGE = "leave " + SUBCOMMAND_WORD
            + ": Lists all leave records.\n"
            + "Example: leave " + SUBCOMMAND_WORD;
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

        Map<EmployeeId, Name> employeeNames = model.getAddressBook().getEmployeeList().stream()
                .collect(Collectors.toMap(employee -> employee.getEmployeeId().orElseThrow(), Employee::getName));
        LocalDate today = LocalDate.now(clock);
        Set<EmployeeId> displayedEmployeeIds = model.getFilteredEmployeeList().stream()
                .map(employee -> employee.getEmployeeId().orElseThrow())
                .collect(Collectors.toSet());
        List<Leave> matchingLeaves = model.getAddressBook().getLeaveList().stream()
                .filter(leave -> displayedEmployeeIds.contains(leave.getEmployeeId()))
                .sorted(Comparator.comparingInt(leave -> leave.getLeaveId().value))
                .toList();

        if (matchingLeaves.isEmpty()) {
            return new CommandResult(MESSAGE_NO_LEAVE);
        }

        String records = matchingLeaves.stream()
                .map(leave -> formatLeave(leave, employeeNames, today))
                .collect(Collectors.joining("\n"));
        return new CommandResult(String.format(MESSAGE_SUCCESS, records));
    }

    private static String formatLeave(Leave leave, Map<EmployeeId, Name> employeeNames, LocalDate today) {
        Name employeeName = employeeNames.get(leave.getEmployeeId());
        if (employeeName == null) {
            throw new IllegalStateException("Leave record references an unknown employee.");
        }
        LeavePeriod period = leave.getPeriod();
        return String.format("ID %s | %s | %s | %d working day(s) | %s",
                leave.getLeaveId(), employeeName, period, period.getWorkingDayCount(), getStatus(period, today));
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
