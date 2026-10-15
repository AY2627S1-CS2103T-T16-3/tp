package seedu.address.model.employee;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class EmployeeIdTest {

    @Test
    public void constructor_nonPositiveValue_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, EmployeeId.MESSAGE_CONSTRAINTS, () -> new EmployeeId(0));
        assertThrows(IllegalArgumentException.class, EmployeeId.MESSAGE_CONSTRAINTS, () -> new EmployeeId(-1));
    }

    @Test
    public void isValidEmployeeId() {
        assertFalse(EmployeeId.isValidEmployeeId(Integer.MIN_VALUE));
        assertFalse(EmployeeId.isValidEmployeeId(0));
        assertTrue(EmployeeId.isValidEmployeeId(1));
        assertTrue(EmployeeId.isValidEmployeeId(Integer.MAX_VALUE));
    }

    @Test
    public void getValue_returnsConstructedValue() {
        assertEquals(12, new EmployeeId(12).getValue());
    }

    @Test
    public void equals() {
        EmployeeId employeeId = new EmployeeId(12);

        assertTrue(employeeId.equals(employeeId));
        assertTrue(employeeId.equals(new EmployeeId(12)));
        assertFalse(employeeId.equals(new EmployeeId(13)));
        assertFalse(employeeId.equals(null));
        assertFalse(employeeId.equals(12));
    }

    @Test
    public void hashCode_sameValue_sameHashCode() {
        assertEquals(new EmployeeId(12).hashCode(), new EmployeeId(12).hashCode());
    }

    @Test
    public void toStringMethod() {
        assertEquals("12", new EmployeeId(12).toString());
    }
}
