package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_END_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_START_DATE;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.employee.Employee;
import seedu.address.model.employee.EmployeeId;
import seedu.address.model.leave.Leave;
import seedu.address.model.leave.LeavePeriod;
import seedu.address.model.leave.exceptions.InsufficientLeaveException;
import seedu.address.model.leave.exceptions.LeaveIdExhaustedException;
import seedu.address.model.leave.exceptions.OverlappingLeaveException;

/**
 * Records leave for an employee selected from the displayed employee list.
 */
public class AddLeaveCommand extends Command {

    public static final String COMMAND_WORD = "leave";
    public static final String SUBCOMMAND_WORD = "add";

    public static final String MESSAGE_USAGE = COMMAND_WORD + " " + SUBCOMMAND_WORD
            + ": Records leave for the employee at the displayed index.\n"
            + "Parameters: INDEX " + PREFIX_START_DATE + "START_DATE " + PREFIX_END_DATE + "END_DATE\n"
            + "Dates must use DD-MM-YYYY format.\n"
            + "Example: " + COMMAND_WORD + " " + SUBCOMMAND_WORD + " 1 "
            + PREFIX_START_DATE + "05-10-2026 " + PREFIX_END_DATE + "07-10-2026";

    public static final String MESSAGE_SUCCESS =
            "Leave recorded: ID %1$s for %2$s; period %3$s; %4$d working day(s); %5$d day(s) remaining in %6$d.";
    public static final String MESSAGE_OVERLAPPING_LEAVE =
            "This leave period overlaps an existing leave record for the employee.";
    public static final String MESSAGE_INSUFFICIENT_LEAVE =
            "%1$s has only %2$d annual leave day(s) remaining in %3$d, but this request needs %4$d.";
    public static final String MESSAGE_LEAVE_ID_EXHAUSTED =
            "No more leave IDs are available. The leave record was not added.";

    private final Index employeeIndex;
    private final LeavePeriod leavePeriod;

    /**
     * Creates an add-leave command for the employee at {@code employeeIndex}.
     */
    public AddLeaveCommand(Index employeeIndex, LeavePeriod leavePeriod) {
        requireNonNull(employeeIndex);
        requireNonNull(leavePeriod);
        this.employeeIndex = employeeIndex;
        this.leavePeriod = leavePeriod;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Employee> lastShownList = model.getFilteredEmployeeList();
        if (employeeIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_EMPLOYEE_DISPLAYED_INDEX);
        }

        Employee employee = lastShownList.get(employeeIndex.getZeroBased());
        EmployeeId employeeId = employee.getEmployeeId()
                .orElseThrow(() -> new IllegalStateException("Stored employee has no internal ID."));
        try {
            Leave addedLeave = model.addLeave(employeeId, leavePeriod);
            int remainingDays = model.getRemainingLeave(employeeId, leavePeriod.getYear());
            return new CommandResult(String.format(MESSAGE_SUCCESS, addedLeave.getLeaveId(), employee.getName(),
                    leavePeriod, leavePeriod.getWorkingDayCount(), remainingDays, leavePeriod.getYear()));
        } catch (OverlappingLeaveException e) {
            throw new CommandException(MESSAGE_OVERLAPPING_LEAVE);
        } catch (InsufficientLeaveException e) {
            throw new CommandException(String.format(MESSAGE_INSUFFICIENT_LEAVE, employee.getName(),
                    e.getRemainingDays(), leavePeriod.getYear(), e.getRequestedDays()));
        } catch (LeaveIdExhaustedException e) {
            throw new CommandException(MESSAGE_LEAVE_ID_EXHAUSTED);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof AddLeaveCommand otherCommand)) {
            return false;
        }
        return employeeIndex.equals(otherCommand.employeeIndex)
                && leavePeriod.equals(otherCommand.leavePeriod);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("employeeIndex", employeeIndex)
                .add("leavePeriod", leavePeriod)
                .toString();
    }
}
