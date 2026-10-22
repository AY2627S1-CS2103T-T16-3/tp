package seedu.address.model.employee;

import static java.util.Objects.requireNonNull;

import java.util.Locale;
import java.util.Objects;

/**
 * Represents a task assigned to an employee.
 * Guarantees: details are trimmed, non-empty, and no longer than 120 characters.
 */
public class Task {

    public static final int MAX_DETAILS_LENGTH = 120;
    public static final String MESSAGE_CONSTRAINTS = "Task details should not be blank and should not exceed "
            + MAX_DETAILS_LENGTH + " characters.";

    private final String details;
    private final boolean isDone;

    /**
     * Constructs a task with the given details.
     *
     * @param details Details of the task.
     */
    public Task(String details) {
        this(details, false);
    }

    /**
     * Constructs a task with the given details and completion status.
     *
     * @param details Details of the task.
     * @param isDone Whether the task is completed.
     */
    private Task(String details, boolean isDone) {
        requireNonNull(details);
        String trimmedDetails = details.trim();
        if (!isValidDetails(trimmedDetails)) {
            throw new IllegalArgumentException(MESSAGE_CONSTRAINTS);
        }
        this.details = trimmedDetails;
        this.isDone = isDone;
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

    /**
     * Returns the task details.
     */
    public String getDetails() {
        return details;
    }

    /** Returns true if this task is completed. */
    public boolean isDone() {
        return isDone;
    }

    /** Returns a copy of this task marked as completed. */
    public Task markDone() {
        return new Task(details, true);
    }

    /** Returns a copy of this task marked as not completed. */
    public Task markUndone() {
        return new Task(details, false);
    }

    /**
     * Returns true if this task is completed.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns a copy of this task marked as completed.
     */
    public Task markDone() {
        return new Task(details, true);
    }

    /**
     * Returns a copy of this task marked as not completed.
     */
    public Task markUndone() {
        return new Task(details, false);
    }

    /**
     * Returns a normalized representation used for duplicate detection.
     *
     * @return Lowercase details with consecutive whitespace collapsed.
     */
    public String getNormalizedDetails() {
        return details.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }

    /**
     * Returns true if this task has the same normalized details as the given task.
     *
     * @param otherTask Task whose details are compared with this task.
     * @return True if the tasks have the same normalized details.
     */
    public boolean hasSameDetails(Task otherTask) {
        requireNonNull(otherTask);
        return getNormalizedDetails().equals(otherTask.getNormalizedDetails());
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Task otherTask)) {
            return false;
        }
        return hasSameDetails(otherTask) && isDone == otherTask.isDone;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNormalizedDetails(), isDone);
    }

    @Override
    public String toString() {
        return (isDone ? "[x] " : "[ ] ") + details;
    }
}
