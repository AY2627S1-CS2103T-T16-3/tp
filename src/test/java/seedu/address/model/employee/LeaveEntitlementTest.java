package seedu.address.model.employee;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class LeaveEntitlementTest {

    @Test
    public void constructor_invalidValue_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new LeaveEntitlement(-1));
        assertThrows(IllegalArgumentException.class, () -> new LeaveEntitlement(366));
    }

    @Test
    public void isValidLeaveEntitlement() {
        assertFalse(LeaveEntitlement.isValidLeaveEntitlement(-1));
        assertTrue(LeaveEntitlement.isValidLeaveEntitlement(0));
        assertTrue(LeaveEntitlement.isValidLeaveEntitlement(365));
        assertFalse(LeaveEntitlement.isValidLeaveEntitlement(366));
    }

    @Test
    public void equalityAndHashCode() {
        LeaveEntitlement entitlement = new LeaveEntitlement(14);

        assertEquals(entitlement, new LeaveEntitlement(14));
        assertEquals(entitlement.hashCode(), new LeaveEntitlement(14).hashCode());
        assertFalse(entitlement.equals(new LeaveEntitlement(15)));
        assertFalse(entitlement.equals(null));
    }

    @Test
    public void toStringMethod() {
        assertEquals("14", new LeaveEntitlement(14).toString());
    }
}
