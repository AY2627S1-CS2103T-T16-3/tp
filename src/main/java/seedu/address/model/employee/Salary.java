package seedu.address.model.employee;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents an employee's salary.
 * Guarantees: immutable; valid as declared in {@link #isValidSalary(String)}.
 */
public class Salary {

    public static final int MAX_SALARY = 50000;
    public static final String MESSAGE_CONSTRAINTS =
            "Salary should be a whole number from 0 to " + MAX_SALARY + " (inclusive)";
    public static final String VALIDATION_REGEX = "[0-9]+";

    public final Integer value;

    /**
     * Constructs a {@code Salary}.
     *
     * @param salary A valid salary amount.
     */
    public Salary(String salary) {
        requireNonNull(salary);
        checkArgument(isValidSalary(salary), MESSAGE_CONSTRAINTS);
        value = Integer.valueOf(salary);
    }

    /**
     * Returns true if a given string is a valid salary.
     */
    public static boolean isValidSalary(String test) {
        if (!test.matches(VALIDATION_REGEX)) {
            return false;
        }

        try {
            return Integer.parseInt(test) <= MAX_SALARY;
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
