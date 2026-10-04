package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_EMPLOYEES;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.employee.Employee;
import seedu.address.model.employee.Remark;

/**
 * Changes the remark of an existing employee in the address book.
 */
public class RemarkCommand extends Command {

    public static final String MESSAGE_ARGUMENTS = "Index: %1$d, Remark: %2$s";

    public static final String COMMAND_WORD = "remark";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Edits the remark of the employee identified "
            + "by the index number used in the last employee listing. "
            + "Existing remark will be overwritten by the input.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "r/ [REMARK]\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + "r/ Likes to swim.";

    public static final String MESSAGE_NOT_IMPLEMENTED_YET =
            "Remark command not implemented yet";

    public static final String MESSAGE_ADD_REMARK_SUCCESS = "Added remark to Employee: %1$s";
    public static final String MESSAGE_DELETE_REMARK_SUCCESS = "Removed remark from Employee: %1$s";

    private final Index index;
    private final String remark;

    /**
     * @param index of the employee in the filtered employee list to edit the remark
     * @param remark of the employee to be updated to
     */
    public RemarkCommand(Index index, String remark) {
        requireAllNonNull(index, remark);

        this.index = index;
        this.remark = remark;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Employee> lastShownList = model.getFilteredEmployeeList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_EMPLOYEE_DISPLAYED_INDEX);
        }

        Employee employeeToEdit = lastShownList.get(index.getZeroBased());
        Employee editedEmployee = new Employee(
                employeeToEdit.getName(), employeeToEdit.getPhone(), employeeToEdit.getEmail(),
                employeeToEdit.getAddress(), employeeToEdit.getTags(), new Remark(remark));

        model.setEmployee(employeeToEdit, editedEmployee);
        model.updateFilteredEmployeeList(PREDICATE_SHOW_ALL_EMPLOYEES);

        return new CommandResult(generateSuccessMessage(editedEmployee));
    }

    /**
     * Generates a command execution success message based on whether
     * the remark is added to or removed from
     * {@code employeeToEdit}.
     */
    private String generateSuccessMessage(Employee employeeToEdit) {
        String message = !remark.isEmpty() ? MESSAGE_ADD_REMARK_SUCCESS : MESSAGE_DELETE_REMARK_SUCCESS;
        return String.format(message, Messages.format(employeeToEdit));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof RemarkCommand)) {
            return false;
        }

        RemarkCommand e = (RemarkCommand) other;
        return index.equals(e.index)
                && remark.equals(e.remark);
    }

}
