package seedu.address.model.employee;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.math.BigInteger;

/**
 * Represents an Employee's whole-number salary amount.
 * Guarantees: immutable; is valid as declared in {@link #isValidSalary(String)}
 */
public class Salary {


    public static final String MESSAGE_CONSTRAINTS =
            "Salary must be a whole-number amount from 0 to 50000 inclusive";
    public static final String VALIDATION_REGEX = "[0-9]+";
    public final String value;

    /**
     * Constructs a {@code Salary}.
     *
     * @param salary A valid salary amount.
     */
    public Salary(String salary) {
        requireNonNull(salary);
        checkArgument(isValidSalary(salary), MESSAGE_CONSTRAINTS);
        value = salary;
    }

    /**
     * Returns true if a given string is a valid salary amount.
     */
    public static boolean isValidSalary(String test) {
        return test.matches(VALIDATION_REGEX)
                && new BigInteger(test).compareTo(BigInteger.valueOf(50000)) <= 0;
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
