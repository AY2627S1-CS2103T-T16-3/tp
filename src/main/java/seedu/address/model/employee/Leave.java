package seedu.address.model.employee;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents an Employee's non-negative whole-number count of leave days.
 * Guarantees: immutable; is valid as declared in {@link #isValidLeave(String)}
 */
public class Leave {


    public static final String MESSAGE_CONSTRAINTS =
            "Leave must be a non-negative whole-number count of days";
    public static final String VALIDATION_REGEX = "[0-9]+";
    public final String value;

    /**
     * Constructs a {@code Leave}.
     *
     * @param leave A valid count of leave days.
     */
    public Leave(String leave) {
        requireNonNull(leave);
        checkArgument(isValidLeave(leave), MESSAGE_CONSTRAINTS);
        value = leave;
    }

    /**
     * Returns true if a given string is a valid count of leave days.
     */
    public static boolean isValidLeave(String test) {
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
