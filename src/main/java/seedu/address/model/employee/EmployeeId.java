package seedu.address.model.employee;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents an Employee's ID, preserving leading zeros.
 * Guarantees: immutable; is valid as declared in {@link #isValidEmployeeId(String)}
 */
public class EmployeeId {


    public static final String MESSAGE_CONSTRAINTS =
            "Employee IDs must contain one or more digits";
    public static final String VALIDATION_REGEX = "[0-9]+";
    public final String value;

    /**
     * Constructs a {@code EmployeeId}.
     *
     * @param employeeId A valid employee ID.
     */
    public EmployeeId(String employeeId) {
        requireNonNull(employeeId);
        checkArgument(isValidEmployeeId(employeeId), MESSAGE_CONSTRAINTS);
        value = employeeId;
    }

    /**
     * Returns true if a given string is a valid employee ID.
     */
    public static boolean isValidEmployeeId(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EmployeeId otherEmployeeId)) {
            return false;
        }

        return value.equals(otherEmployeeId.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
