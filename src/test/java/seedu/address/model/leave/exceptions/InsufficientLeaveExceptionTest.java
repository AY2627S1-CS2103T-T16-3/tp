package seedu.address.model.leave.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class InsufficientLeaveExceptionTest {

    @Test
    public void constructor_validValues_storesValues() {
        InsufficientLeaveException exception = new InsufficientLeaveException(3, 2);

        assertEquals(3, exception.getRequestedDays());
        assertEquals(2, exception.getRemainingDays());
    }

    @Test
    public void constructor_invalidValues_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new InsufficientLeaveException(-1, 0));
        assertThrows(AssertionError.class, () -> new InsufficientLeaveException(1, -1));
        assertThrows(AssertionError.class, () -> new InsufficientLeaveException(1, 1));
        assertThrows(AssertionError.class, () -> new InsufficientLeaveException(1, 2));
    }
}
