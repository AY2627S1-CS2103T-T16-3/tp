package seedu.address.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.logic.parser.Prefix;
import seedu.address.model.employee.Employee;

/**
 * Container for user visible messages.
 */
public class Messages {

    public static final String MESSAGE_UNKNOWN_COMMAND = "Unknown command.";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT = "Invalid command format!\n%1$s";
    public static final String MESSAGE_INVALID_EMPLOYEE_DISPLAYED_INDEX = "The employee index provided is invalid.";
    public static final String MESSAGE_EMPLOYEES_LISTED_OVERVIEW = "%1$d employee(s) listed!";
    public static final String MESSAGE_DUPLICATE_FIELDS =
                "Multiple values specified for the following single-valued field(s): ";

    /**
     * Returns an error message indicating the duplicate prefixes.
     */
    public static String getErrorMessageForDuplicatePrefixes(Prefix... duplicatePrefixes) {
        assert duplicatePrefixes.length > 0;

        Set<String> duplicateFields =
                Stream.of(duplicatePrefixes).map(Prefix::toString).collect(Collectors.toSet());

        return MESSAGE_DUPLICATE_FIELDS + String.join(" ", duplicateFields);
    }

    /**
     * Lists each conflicting contact value and its existing owner in stored employee order.
     * For edits, callers must exclude the employee being edited from {@code existingEmployees}.
     * Returns an empty string if no contact details conflict.
     */
    public static String getContactConflictMessage(Employee candidate, List<Employee> existingEmployees) {
        List<String> conflicts = new ArrayList<>();
        for (Employee employee : existingEmployees) {
            if (!candidate.hasContactConflictWith(employee)) {
                continue;
            }
            if (candidate.getPhone().equals(employee.getPhone())) {
                conflicts.add(String.format("Phone number %s is already used by %s.",
                        candidate.getPhone(), employee.getName()));
            }
            if (candidate.getEmail().value.equalsIgnoreCase(employee.getEmail().value)) {
                conflicts.add(String.format("Email address %s is already used by %s.",
                        candidate.getEmail(), employee.getName()));
            }
        }
        return String.join("\n", conflicts);
    }

    /** Returns the employee's current name and email for leave feedback. */
    public static String formatEmployeeNameAndEmail(Employee employee) {
        return String.format("%s (%s)", employee.getName(), employee.getEmail());
    }

    /**
     * Formats the {@code employee} for display to the user.
     */
    public static String format(Employee employee) {
        final StringBuilder builder = new StringBuilder();
        builder.append(employee.getName())
                .append("; Phone: ")
                .append(employee.getPhone())
                .append("; Email: ")
                .append(employee.getEmail())
                .append("; Address: ")
                .append(employee.getAddress())
                .append("; Total annual leave: ")
                .append(employee.getLeaveEntitlement())
                .append(" day(s)")
                .append("; Tags: ");
        employee.getTags().forEach(builder::append);
        return builder.toString();
    }

}
