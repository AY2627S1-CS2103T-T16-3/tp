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
 * Adds a task to an employee identified by the displayed employee index.
 */
public class AddTaskCommand extends Command {

    public static final String SUBCOMMAND_WORD = "add";
    public static final String COMMAND_WORD = TaskCommand.COMMAND_WORD + " " + SUBCOMMAND_WORD;
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Adds a task to the employee identified by the displayed index.\n"
            + "Parameters: EMPLOYEE_INDEX (must be a positive integer) d/DETAILS\n"
            + "Example: " + COMMAND_WORD + " 1 d/Prepare report";
    public static final String MESSAGE_SUCCESS = "Added task to %1$s: %2$s";
    public static final String MESSAGE_DUPLICATE_TASK = "This task already exists for the employee.";

    private final Index employeeIndex;
    private final Task task;

    /**
     * Creates a command to add {@code task} to the selected employee.
     *
     * @param employeeIndex Index of the employee in the displayed list.
     * @param task Task to add to the employee.
     */
    public AddTaskCommand(Index employeeIndex, Task task) {
        this.employeeIndex = requireNonNull(employeeIndex);
        this.task = requireNonNull(task);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Employee> employees = model.getFilteredEmployeeList();
        if (employeeIndex.getZeroBased() >= employees.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_EMPLOYEE_DISPLAYED_INDEX);
        }

        Employee employee = employees.get(employeeIndex.getZeroBased());
        Employee updatedEmployee;
        try {
            updatedEmployee = employee.addTask(task);
        } catch (IllegalArgumentException exception) {
            throw new CommandException(MESSAGE_DUPLICATE_TASK, exception);
        }
        model.setEmployee(employee, updatedEmployee);
        return new CommandResult(String.format(MESSAGE_SUCCESS,
                employee.getName().fullName, task.getDetails()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof AddTaskCommand otherCommand)) {
            return false;
        }
        return employeeIndex.equals(otherCommand.employeeIndex) && task.equals(otherCommand.task);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeIndex, task);
    }
}
