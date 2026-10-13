package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.stream.IntStream;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.employee.Employee;
import seedu.address.model.employee.Task;

/** Lists the tasks assigned to an employee. */
public class ListTasksCommand extends Command {

    public static final String COMMAND_WORD = "list-tasks";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Lists the tasks assigned to the employee identified by the displayed index.\n"
            + "Parameters: EMPLOYEE_INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";
    public static final String MESSAGE_NO_TASKS = "Employee %1$s has no tasks.";

    private final Index employeeIndex;

    /**
     * Creates a command to list the tasks assigned to the selected employee.
     *
     * @param employeeIndex Index of the employee in the displayed list.
     */
    public ListTasksCommand(Index employeeIndex) {
        this.employeeIndex = requireNonNull(employeeIndex);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Employee> employees = model.getFilteredEmployeeList();
        if (employeeIndex.getZeroBased() >= employees.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_EMPLOYEE_DISPLAYED_INDEX);
        }

        Employee employee = employees.get(employeeIndex.getZeroBased());
        List<Task> tasks = employee.getTasks();
        if (tasks.isEmpty()) {
            return new CommandResult(String.format(MESSAGE_NO_TASKS, employee.getName().fullName));
        }

        StringBuilder result = new StringBuilder("Tasks for ")
                .append(employee.getName().fullName)
                .append(":\n");
        IntStream.range(0, tasks.size())
                .forEach(index -> result.append(index + 1)
                        .append(". ")
                        .append(tasks.get(index).getDetails())
                        .append("\n"));
        result.deleteCharAt(result.length() - 1);
        return new CommandResult(result.toString());
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ListTasksCommand otherCommand)) {
            return false;
        }
        return employeeIndex.equals(otherCommand.employeeIndex);
    }

    @Override
    public int hashCode() {
        return employeeIndex.hashCode();
    }
}
