package seedu.address.model.employee;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents an employee's salary in the address book.
 * Guarantees: immutable; valid as declared in {@link #isValidSalary(String)}.
 */
public class Salary {
    public static final String MESSAGE_CONSTRAINTS =
            "Salary must contain only digits and represent an amount from 0 to 50000";
    public static final String VALIDATION_REGEX = "[0-9]+";
    public final String salary;

    /**
     * Constructs a {@code Salary}, removing leading zeros.
     *
     * @param salary A valid salary amount.
     */
    public Salary(String salary) {
        requireNonNull(salary);
        checkArgument(isValidSalary(salary), MESSAGE_CONSTRAINTS);
        this.salary = Integer.toString(Integer.parseInt(removeLeadingZeros(salary)));
    }

    /**
     * Returns true if the string contains only digits and represents an amount from 0 to 50000.
     * Leading zeros are allowed. Oversized amounts are rejected before integer conversion.
     */
    public static boolean isValidSalary(String test) {
        if (!test.matches(VALIDATION_REGEX)) {
            return false;
        }
        String normalized = removeLeadingZeros(test);
        return normalized.length() <= 5 && Integer.parseInt(normalized) <= 50000;
    }

    /**
     * Removes leading zeros from a digit string, keeping one digit for zero.
     */
    private static String removeLeadingZeros(String value) {
        return value.replaceFirst("^0+(?!$)", "");
    }

    @Override
    public String toString() {
        return "$" + salary;
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

        return salary.equals(otherSalary.salary);
    }

    @Override
    public int hashCode() {
        return salary.hashCode();
    }

}
