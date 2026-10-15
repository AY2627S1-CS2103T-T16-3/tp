package seedu.address.model.leave.exceptions;

/**
 * Signals that a leave request exceeds an employee's remaining entitlement.
 */
public class InsufficientLeaveException extends RuntimeException {

    private final int requestedDays;
    private final int remainingDays;

    /**
     * Creates an exception describing the requested and remaining leave days.
     */
    public InsufficientLeaveException(int requestedDays, int remainingDays) {
        assert requestedDays >= 0 : "Requested leave days must be non-negative.";
        assert remainingDays >= 0 : "Remaining leave days must be non-negative.";
        assert requestedDays > remainingDays : "Requested leave days must exceed remaining leave days.";
        this.requestedDays = requestedDays;
        this.remainingDays = remainingDays;
    }

    public int getRequestedDays() {
        return requestedDays;
    }

    public int getRemainingDays() {
        return remainingDays;
    }
}
