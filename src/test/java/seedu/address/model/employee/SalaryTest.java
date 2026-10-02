package seedu.address.model.employee;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class SalaryTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Salary(null));
    }

    @Test
    public void isValidSalary() {
        assertThrows(NullPointerException.class, () -> Salary.isValidSalary(null));

        String[] invalidSalaries = {"", " ", "-1", "+1", "1.234", "1,000", "$100", "1e3",
            ".50", "1.", " 100", "100 ", "0.00", "3000.5", "3000.50", "2147483648",
            "99999999999999999999", "50001", "2147483647"};
        for (String salary : invalidSalaries) {
            assertFalse(Salary.isValidSalary(salary));
            assertThrows(IllegalArgumentException.class, () -> new Salary(salary));
        }

        String[] validSalaries = {"0", "3000", "03000", "49999", "50000"};
        for (String salary : validSalaries) {
            assertTrue(Salary.isValidSalary(salary));
        }
    }

    @Test
    public void equals() {
        Salary salary = new Salary("3000");
        assertTrue(salary.equals(salary));
        assertTrue(salary.equals(new Salary("3000")));
        assertTrue(salary.equals(new Salary("03000")));
        assertFalse(salary.equals(null));
        assertFalse(salary.equals("3000"));
        assertFalse(salary.equals(new Salary("3001")));
        assertEquals(salary.hashCode(), new Salary("03000").hashCode());
    }

    @Test
    public void toString_returnsWholeNumber() {
        assertEquals("3000", new Salary("3000").toString());
        assertEquals("3000", new Salary("03000").toString());
        assertEquals("0", new Salary("0").toString());
        assertEquals("50000", new Salary("50000").toString());
    }
}
