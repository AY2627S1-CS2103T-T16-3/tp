package seedu.address.model.employee;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class EmployeeIdTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new EmployeeId(null));
    }

    @Test
    public void isValidEmployeeId() {
        assertThrows(NullPointerException.class, () -> EmployeeId.isValidEmployeeId(null));

        String[] invalidIds = {"", " ", "E001", "-1", "+1", "1.0", "1 2", " 123", "123 "};
        for (String employeeId : invalidIds) {
            assertFalse(EmployeeId.isValidEmployeeId(employeeId));
            assertThrows(IllegalArgumentException.class, () -> new EmployeeId(employeeId));
        }

        assertTrue(EmployeeId.isValidEmployeeId("0"));
        assertTrue(EmployeeId.isValidEmployeeId("00123"));
        assertTrue(EmployeeId.isValidEmployeeId("12345678901234567890"));
    }

    @Test
    public void toString_preservesLeadingZeros() {
        assertEquals("00123", new EmployeeId("00123").toString());
    }

    @Test
    public void equals() {
        EmployeeId employeeId = new EmployeeId("00123");
        assertTrue(employeeId.equals(employeeId));
        assertTrue(employeeId.equals(new EmployeeId("00123")));
        assertFalse(employeeId.equals(null));
        assertFalse(employeeId.equals("00123"));
        assertFalse(employeeId.equals(new EmployeeId("123")));
        assertEquals(employeeId.hashCode(), new EmployeeId("00123").hashCode());
    }
}
