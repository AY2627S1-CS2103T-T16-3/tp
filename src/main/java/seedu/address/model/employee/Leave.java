package seedu.address.model.employee;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents an employee's number of leave days.
 * Guarantees: immutable; valid as declared in {@link #isValidLeave(String)}.
 */
public class Leave {

    public static final String MESSAGE_CONSTRAINTS =
            "Leave days should be a whole number from 0 to " + Integer.MAX_VALUE;
    public static final String VALIDATION_REGEX = "[0-9]+";

    public final Integer value;

    /**
     * Constructs a {@code Leave}.
     *
     * @param leave A valid number of leave days.
     */
    public Leave(String leave) {
        requireNonNull(leave);
        checkArgument(isValidLeave(leave), MESSAGE_CONSTRAINTS);
        value = Integer.valueOf(leave);
    }

    /**
     * Returns true if a given string is a valid number of leave days.
     */
    public static boolean isValidLeave(String test) {
        if (!test.matches(VALIDATION_REGEX)) {
            return false;
        }

        try {
            Integer.parseInt(test);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Leave otherLeave)) {
            return false;
        }

        return value.equals(otherLeave.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
