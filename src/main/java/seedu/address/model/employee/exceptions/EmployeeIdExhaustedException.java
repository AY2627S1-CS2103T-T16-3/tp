package seedu.address.model.employee.exceptions;

/**
 * Signals that every supported employee ID is already in use.
 */
public class EmployeeIdExhaustedException extends RuntimeException {
    public EmployeeIdExhaustedException() {
        super("No employee IDs are available. Delete an employee before adding another.");
    }
}
