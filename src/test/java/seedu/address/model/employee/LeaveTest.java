package seedu.address.model.employee;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class LeaveTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Leave(null));
    }

    @Test
    public void isValidLeave() {
        assertThrows(NullPointerException.class, () -> Leave.isValidLeave(null));

        String[] invalidLeaveDays = {"", " ", "-1", "+1", "1.5", "1.0", "one", "1 2",
            " 14", "14 ", "2147483648", "99999999999999999999"};
        for (String leave : invalidLeaveDays) {
            assertFalse(Leave.isValidLeave(leave));
            assertThrows(IllegalArgumentException.class, () -> new Leave(leave));
        }

        assertTrue(Leave.isValidLeave("0"));
        assertTrue(Leave.isValidLeave("14"));
        assertTrue(Leave.isValidLeave("014"));
        assertTrue(Leave.isValidLeave("2147483647"));
        assertEquals(Integer.valueOf(Integer.MAX_VALUE), new Leave("2147483647").value);
    }

    @Test
    public void toString_returnsNumberOfDays() {
        assertEquals("0", new Leave("0").toString());
        assertEquals("14", new Leave("014").toString());
    }

    @Test
    public void equals() {
        Leave leave = new Leave("14");
        assertTrue(leave.equals(leave));
        assertTrue(leave.equals(new Leave("014")));
        assertFalse(leave.equals(null));
        assertFalse(leave.equals("14"));
        assertFalse(leave.equals(new Leave("15")));
        assertEquals(leave.hashCode(), new Leave("014").hashCode());
    }
}
