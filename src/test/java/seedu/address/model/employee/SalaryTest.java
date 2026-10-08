package seedu.address.model.employee;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

public class SalaryTest {

    private static final List<String> INVALID_SALARIES = List.of(
            "", " ", "-1", "-9999999", "50001", "99999", "1000000000", "2147483648",
            "999999999999999999999999", "abc", "12abc", "12a34", "5000$", "$5000",
            "1.5", "5,000", "+1", "1 000", " 5000 ", "000050001");

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Salary(null));
    }

    @Test
    public void constructor_invalidSalary_throwsIllegalArgumentException() {
        for (String salary : INVALID_SALARIES) {
            assertThrows(IllegalArgumentException.class, Salary.MESSAGE_CONSTRAINTS, () -> new Salary(salary));
        }
    }

    @Test
    public void isValidSalary_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> Salary.isValidSalary(null));
    }

    @Test
    public void isValidSalary_invalidValues_returnsFalse() {
        for (String salary : INVALID_SALARIES) {
            assertFalse(Salary.isValidSalary(salary), "Unexpected valid salary: " + salary);
        }
    }

    @Test
    public void isValidSalary_validValues_returnsTrue() {
        for (String salary : List.of("0", "1", "49999", "50000")) {
            assertTrue(Salary.isValidSalary(salary), "Unexpected invalid salary: " + salary);
            assertEquals(salary, new Salary(salary).salary);
        }
    }

    @Test
    public void constructor_leadingZeros_normalizesSalary() {
        assertEquals(new Salary("5000"), new Salary("05000"));
        assertEquals("1", new Salary("000000001").salary);
        assertEquals("0", new Salary("000000").salary);
        assertEquals("$5000", new Salary("00005000").toString());
        assertEquals(new Salary("1").hashCode(), new Salary("0001").hashCode());
        assertEquals("1", new Salary("0".repeat(1000) + "1").salary);
        assertEquals("0", new Salary("0".repeat(1000)).salary);
    }

    @Test
    public void equals() {
        Salary salary = new Salary("5000");
        assertTrue(salary.equals(salary));
        assertTrue(salary.equals(new Salary("5000")));
        assertFalse(salary.equals(null));
        assertFalse(salary.equals("5000"));
        assertFalse(salary.equals(new Salary("5001")));
    }

    @Test
    public void hashCode_equalSalaries_returnsSameHashCode() {
        assertEquals(new Salary("5000").hashCode(), new Salary("5000").hashCode());
    }

    @Test
    public void toString_returnsSalaryWithDollarPrefix() {
        assertEquals("$5000", new Salary("5000").toString());
    }
}
