package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.employee.Employee;
import seedu.address.model.leave.Leave;
import seedu.address.model.leave.LeaveId;
import seedu.address.model.leave.exceptions.LeaveNotFoundException;

/**
 * Deletes the leave record identified by its stable leave ID.
 */
public class DeleteLeaveCommand extends Command {

    public static final String SUBCOMMAND_WORD = "delete";
    public static final String MESSAGE_USAGE = "leave " + SUBCOMMAND_WORD
            + ": Deletes the leave record identified by its leave ID.\n"
            + "  Parameters: LEAVE_ID (must be a positive integer)\n"
            + "  Example: leave " + SUBCOMMAND_WORD + " 1";
    public static final String MESSAGE_SUCCESS =
            "Deleted leave: ID %1$s for %2$s; period %3$s; %4$d day(s) remaining in %5$d.";
    public static final String MESSAGE_LEAVE_NOT_FOUND = "No leave record found with ID %1$s.";

    private final LeaveId leaveId;

    public DeleteLeaveCommand(LeaveId leaveId) {
        this.leaveId = requireNonNull(leaveId);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Leave deletedLeave;
        try {
            deletedLeave = model.deleteLeave(leaveId);
        } catch (LeaveNotFoundException e) {
            throw new CommandException(String.format(MESSAGE_LEAVE_NOT_FOUND, leaveId));
        }

        Employee employee = model.getAddressBook().getEmployeeList().stream()
                .filter(candidate -> candidate.getEmployeeId().orElseThrow().equals(deletedLeave.getEmployeeId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Leave record references an unknown employee."));
        int year = deletedLeave.getPeriod().getYear();
        int remainingDays = model.getRemainingLeave(deletedLeave.getEmployeeId(), year);
        return new CommandResult(String.format(MESSAGE_SUCCESS, leaveId, employee.getName(), deletedLeave.getPeriod(),
                remainingDays, year));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof DeleteLeaveCommand otherCommand)) {
            return false;
        }
        return leaveId.equals(otherCommand.leaveId);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("leaveId", leaveId)
                .toString();
    }
}
