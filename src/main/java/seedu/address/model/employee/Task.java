package seedu.address.model.employee;

import static java.util.Objects.requireNonNull;

import java.util.Locale;

/**
 * Represents a task assigned to an employee.
 * Guarantees: details are trimmed, non-empty, and no longer than 120 characters.
 */
public class Task {

    public static final int MAX_DETAILS_LENGTH = 120;
    public static final String MESSAGE_CONSTRAINTS = "Task details should not be blank and should not exceed "
            + MAX_DETAILS_LENGTH + " characters.";

    private final String details;

    /**
     * Constructs a task with the given details.
     *
     * @param details Details of the task.
     */
    public Task(String details) {
        requireNonNull(details);
        String trimmedDetails = details.trim();
        if (!isValidDetails(trimmedDetails)) {
            throw new IllegalArgumentException(MESSAGE_CONSTRAINTS);
        }
        this.details = trimmedDetails;
    }

    /**
     * Returns true if the given details are valid task details.
     *
     * @param details Details to validate.
     * @return True if the details are valid.
     */
    public static boolean isValidDetails(String details) {
        return details != null && !details.isBlank() && details.length() <= MAX_DETAILS_LENGTH;
    }

    /** Returns the task details. */
    public String getDetails() {
        return details;
    }

    /**
     * Returns a normalized representation used for duplicate detection.
     *
     * @return Lowercase details with consecutive whitespace collapsed.
     */
    public String getNormalizedDetails() {
        return details.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Task otherTask)) {
            return false;
        }
        return getNormalizedDetails().equals(otherTask.getNormalizedDetails());
    }

    @Override
    public int hashCode() {
        return getNormalizedDetails().hashCode();
    }

    @Override
    public String toString() {
        return details;
    }
}
