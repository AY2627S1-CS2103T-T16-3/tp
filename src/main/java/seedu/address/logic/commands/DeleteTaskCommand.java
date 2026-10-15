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

/** Deletes a task from an employee identified by the displayed employee index. */
public class DeleteTaskCommand extends Command {

    public static final String COMMAND_WORD = "delete-task";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes a task from the employee identified by the displayed index.\n"
            + "Parameters: EMPLOYEE_INDEX (must be a positive integer) i/TASK_INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1 i/1";
    public static final String MESSAGE_SUCCESS = "Deleted task from %1$s: %2$s";
    public static final String MESSAGE_INVALID_TASK_INDEX = "The task index provided is invalid.";

    private final Index employeeIndex;
    private final Index taskIndex;

    /**
     * Creates a command to delete a task from the selected employee.
     *
     * @param employeeIndex Index of the employee in the displayed list.
     * @param taskIndex Index of the task in the employee's task list.
     */
    public DeleteTaskCommand(Index employeeIndex, Index taskIndex) {
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

        Task task = employee.getTasks().get(taskIndex.getZeroBased());
        Employee updatedEmployee = employee.deleteTask(taskIndex.getZeroBased());
        model.setEmployee(employee, updatedEmployee);
        return new CommandResult(String.format(MESSAGE_SUCCESS,
                employee.getName().fullName, task.getDetails()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof DeleteTaskCommand otherCommand)) {
            return false;
        }
        return employeeIndex.equals(otherCommand.employeeIndex)
                && taskIndex.equals(otherCommand.taskIndex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeIndex, taskIndex);
    }
}
