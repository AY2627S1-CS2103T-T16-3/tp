package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Objects;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.employee.Employee;
import seedu.address.model.employee.Task;

/**
 * Marks a task as not done for an employee identified by the displayed employee index.
 */
public class UnmarkTaskCommand extends Command {

    public static final String COMMAND_WORD = "unmark-task";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Marks a task as not done for the employee identified by the displayed index.\n"
            + "Parameters: EMPLOYEE_INDEX (must be a positive integer) "
            + "i/TASK_INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1 i/1";
    public static final String MESSAGE_SUCCESS = "Unmarked task for %1$s:\n%2$d. %3$s";
    public static final String MESSAGE_INVALID_TASK_INDEX = "The task index provided is invalid.";

    private final Index employeeIndex;
    private final Index taskIndex;

    /**
     * Creates a command to mark a task as not done.
     *
     * @param employeeIndex Index of the employee in the displayed list.
     * @param taskIndex Index of the task in the employee's task list.
     */
    public UnmarkTaskCommand(Index employeeIndex, Index taskIndex) {
        this.employeeIndex = requireNonNull(employeeIndex);
        this.taskIndex = requireNonNull(taskIndex);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Employee> employees = model.getFilteredEmployeeList();
        if (employeeIndex.getZeroBased() >= employees.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_EMPLOYEE_DISPLAYED_INDEX);
        }

        Employee employee = employees.get(employeeIndex.getZeroBased());
        if (taskIndex.getZeroBased() >= employee.getTasks().size()) {
            throw new CommandException(MESSAGE_INVALID_TASK_INDEX);
        }

        Task updatedTask = employee.getTasks().get(taskIndex.getZeroBased()).markUndone();
        Employee updatedEmployee = employee.updateTask(taskIndex.getZeroBased(), updatedTask);
        model.setEmployee(employee, updatedEmployee);
        return new CommandResult(String.format(MESSAGE_SUCCESS, employee.getName().fullName,
                taskIndex.getOneBased(), updatedTask));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof UnmarkTaskCommand otherCommand)) {
            return false;
        }
        return employeeIndex.equals(otherCommand.employeeIndex) && taskIndex.equals(otherCommand.taskIndex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeIndex, taskIndex);
    }
}
