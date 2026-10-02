package seedu.address.model.employee;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Represents an employee's salary.
 * Guarantees: immutable; valid as declared in {@link #isValidSalary(String)}.
 */
public class Salary {

    public static final String MESSAGE_CONSTRAINTS =
            "Salary should be a non-negative number with at most two decimal places";
    public static final String VALIDATION_REGEX = "[0-9]+(\\.[0-9]{1,2})?";

    public final Integer value;

    /**
     * Constructs a {@code Salary}.
     *
     * @param salary A valid salary amount.
     */
    public Salary(String salary) {
        requireNonNull(salary);
        checkArgument(isValidSalary(salary), MESSAGE_CONSTRAINTS);
        // Normalize the scale so equivalent amounts have equal values and hash codes.
        value = Integer.valueOf(salary);
    }

    /**
     * Returns true if a given string is a valid salary.
     */
    public static boolean isValidSalary(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return value.toPlainString();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Salary otherSalary)) {
            return false;
        }

        return value.equals(otherSalary.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
