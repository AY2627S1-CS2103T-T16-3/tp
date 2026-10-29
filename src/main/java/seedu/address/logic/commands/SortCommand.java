package seedu.address.logic.commands;

import java.math.BigInteger;
import java.util.Comparator;
import static java.util.Objects.requireNonNull;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.Model;
import seedu.address.model.employee.Employee;

/**
 * Sorts the currently displayed employees by a single field without changing
 * the active filter.
 */
public class SortCommand extends Command {

    public static final String COMMAND_WORD = "sort";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Sorts the currently displayed employees by exactly one field.\n"
            + "Parameters: n/ORDER or p/ORDER or e/ORDER or a/ORDER or s/ORDER or le/ORDER\n"
            + "ORDER must be asc (ascending) or des (descending).\n"
            + "Salary, phone, and annual leave entitlement sort numerically;\n"
            + "Name, email and address sort alphabetically, ignoring case.\n"
            + "Example: sort s/asc";
    public static final String MESSAGE_SUCCESS = "Sorted displayed employees by %s in %s order.";

    /**
     * Fields with a single value that can be used to order employees.
     */
    public enum Field {
        NAME("name"), PHONE("phone number"), EMAIL("email"), ADDRESS("address"),
        SALARY("salary"), LEAVE_ENTITLEMENT("annual leave entitlement");

        private final String displayName;

        Field(String displayName) {
            this.displayName = displayName;
        }
    }

    private final Field field;
    private final boolean ascending;

    /**
     * Creates a sort command for the specified field and direction.
     */
    public SortCommand(Field field, boolean ascending) {
        this.field = requireNonNull(field);
        this.ascending = ascending;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        Comparator<Employee> comparator = switch (field) {
            case NAME ->
                Comparator.comparing(employee -> employee.getName().fullName, String.CASE_INSENSITIVE_ORDER);
            case PHONE ->
                Comparator.comparing(employee -> new BigInteger(employee.getPhone().value));
            case EMAIL ->
                Comparator.comparing(employee -> employee.getEmail().value, String.CASE_INSENSITIVE_ORDER);
            case ADDRESS ->
                Comparator.comparing(employee -> employee.getAddress().value,
                String.CASE_INSENSITIVE_ORDER);
            case SALARY ->
                Comparator.comparingInt(employee -> Integer.parseInt(employee.getSalary().salary));
            case LEAVE_ENTITLEMENT ->
                Comparator.comparingInt(employee -> employee.getLeaveEntitlement().value);
        };
        model.sortFilteredEmployeeList(ascending ? comparator : comparator.reversed());
        return new CommandResult(String.format(MESSAGE_SUCCESS, field.displayName,
                ascending ? "ascending" : "descending"));
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof SortCommand otherCommand
                && field == otherCommand.field && ascending == otherCommand.ascending;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("field", field).add("ascending", ascending).toString();
    }
}
