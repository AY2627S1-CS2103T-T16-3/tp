package seedu.address.model.employee;

import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents the stable internal identifier assigned to an employee record.
 * This identifier is used only to associate persisted data with an employee; users select employees by displayed index.
 * Guarantees: immutable; is valid as declared in {@link #isValidEmployeeId(int)}.
 */
public final class EmployeeId {

    public static final String MESSAGE_CONSTRAINTS = "Employee ID must be a positive integer.";

    private final int value;

    /**
     * Constructs an internal {@code EmployeeId}.
     *
     * @param value A positive integer assigned to an employee record.
     */
    public EmployeeId(int value) {
        checkArgument(isValidEmployeeId(value), MESSAGE_CONSTRAINTS);
        this.value = value;
    }

    /**
     * Returns true if {@code value} can be used as an internal employee identifier.
     */
    public static boolean isValidEmployeeId(int value) {
        return value > 0;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof EmployeeId otherEmployeeId)) {
            return false;
        }

        return value == otherEmployeeId.value;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }
}
