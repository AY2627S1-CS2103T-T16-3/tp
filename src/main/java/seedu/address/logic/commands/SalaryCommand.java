package seedu.address.logic.commands;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_EMPLOYEES;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.employee.Employee;
import seedu.address.model.employee.Salary;

/**
 * Represents a salary command targeting an employee by their displayed list index.
 */
public class SalaryCommand extends Command {

    public static final String COMMAND_WORD = "salary";
    public static final String MESSAGE_SUCCESS = "Updated %1$s's salary from %2$s to %3$s.";
    public static final String MESSAGE_NO_CHANGE = "%1$s's salary is already %2$s. No changes made.";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Edits the salary of the employee identified "
            + "by the index number used in the last employee listing. "
            + "Existing salary will be overwritten by the input.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "s/SALARY\n"
            + "Example: " + COMMAND_WORD + " 1 s/5000";

    private final Index index;
    private final Salary salary;

    /**
     * Creates a command to update the salary of the employee at the given displayed index.
     *
     * @param index of the employee in the filtered employee list whose salary is to be edited.
     * @param salary the new salary for the employee.
     */
    public SalaryCommand(Index index, Salary salary) {
        requireAllNonNull(index, salary);

        this.index = index;
        this.salary = salary;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        List<Employee> lastShownList = model.getFilteredEmployeeList();
        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_EMPLOYEE_DISPLAYED_INDEX);
        }
        Employee employeeToEdit = lastShownList.get(index.getZeroBased());
        if (employeeToEdit.getSalary().equals(salary)) {
            return new CommandResult(String.format(MESSAGE_NO_CHANGE, employeeToEdit.getName(), salary));
        }
        Employee editedEmployee = employeeToEdit.withSalary(salary);

        model.setEmployee(employeeToEdit, editedEmployee);
        model.updateFilteredEmployeeList(PREDICATE_SHOW_ALL_EMPLOYEES);

        return new CommandResult(generateSuccessMessage(employeeToEdit));

    }

    /**
     * Returns a success message identifying the employee and the old and new salaries.
     */
    private String generateSuccessMessage(Employee employeeToEdit) {
        return String.format(MESSAGE_SUCCESS, employeeToEdit.getName(), employeeToEdit.getSalary(), salary);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof SalaryCommand)) {
            return false;
        }

        SalaryCommand otherSalaryCommand = (SalaryCommand) other;
        return salary.equals(otherSalaryCommand.salary)
                && index.equals(otherSalaryCommand.index);
    }

}
