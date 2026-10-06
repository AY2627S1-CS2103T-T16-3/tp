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
    public static final String MESSAGE_SUCCESS = "Updated salary to %1$s for employee: %2$s";

    public static final String MESSAGE_ARGUMENTS = "Index: %1$d, Salary: %2$s";
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
     * @param index of the employee in the filtered employee list whose salary is to be edited
     * @param salary the new salary for the employee
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
        Employee editedEmployee = new Employee(
                employeeToEdit.getName(), employeeToEdit.getPhone(), employeeToEdit.getEmail(),
                employeeToEdit.getAddress(), employeeToEdit.getTags(), salary);

        model.setEmployee(employeeToEdit, editedEmployee);
        model.updateFilteredEmployeeList(PREDICATE_SHOW_ALL_EMPLOYEES);

        return new CommandResult(generateSuccessMessage(editedEmployee));

    }

    /**
     * Returns a success message identifying the employee whose salary was updated.
     */
    private String generateSuccessMessage(Employee employeeToEdit) {
        return String.format(MESSAGE_SUCCESS, salary, Messages.format(employeeToEdit));
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

        SalaryCommand e = (SalaryCommand) other;
        return salary.equals(e.salary)
                && index.equals(e.index);
    }

}
