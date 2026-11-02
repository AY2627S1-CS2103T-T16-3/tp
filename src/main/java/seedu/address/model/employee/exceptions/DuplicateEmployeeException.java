package seedu.address.model.employee.exceptions;

/**
 * Signals that an operation would give employees a shared phone number or email address.
 * Contact conflicts are separate from internal employee identity; names may be shared.
 */
public class DuplicateEmployeeException extends RuntimeException {
    public DuplicateEmployeeException() {
        super("Operation would result in duplicate employees");
    }
}
