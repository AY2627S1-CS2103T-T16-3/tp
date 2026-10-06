package seedu.address.model.employee;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents an employee's salary in the address book.
 * Guarantees: immutable; valid as declared in {@link #isValidSalary(String)}.
 */
public class Salary {
    public static final String MESSAGE_CONSTRAINTS =
            "Salary should only contain digits, and should be at between 0 to 50000";
    public static final String VALIDATION_REGEX = "\\d{1,5}";
    public final String salary;

    /**
     * Constructs a {@code Salary}.
     *
     * @param salary A valid salary amount.
     */
    public Salary(String salary) {
        requireNonNull(salary);
        checkArgument(isValidSalary(salary), MESSAGE_CONSTRAINTS);
        this.salary = salary;
    }

    /**
     * Returns true if the string contains one to five digits and represents an amount from 0 to 50000.
     */
    public static boolean isValidSalary(String test) {
        return test.matches(VALIDATION_REGEX)
                && Integer.parseInt(test) <= 50000;
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
