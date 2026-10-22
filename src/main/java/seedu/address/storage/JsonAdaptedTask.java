package seedu.address.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.employee.Task;

/**
 * Jackson-friendly version of {@link Task}.
 */
class JsonAdaptedTask {

    private final String details;
    private final boolean isDone;

    /**
     * Constructs a JSON-adapted task with the given details and completion status.
     *
     * @param details Details of the task.
     * @param isDone Whether the task is completed.
     */
    @JsonCreator
    public JsonAdaptedTask(@JsonProperty("details") String details, @JsonProperty("isDone") Boolean isDone) {
        this.details = details;
        this.isDone = isDone != null && isDone;
    }

    /**
     * Converts a given {@code Task} into this class for Jackson use.
     *
     * @param source Task to convert.
     */
    public JsonAdaptedTask(Task source) {
        details = source.getDetails();
        isDone = source.isDone();
    }

    /**
     * Converts this Jackson-friendly task object into a model task.
     *
     * @return The converted task.
     * @throws IllegalValueException If the details violate task constraints.
     */
    public Task toModelType() throws IllegalValueException {
        try {
            Task task = new Task(details);
            return isDone ? task.markDone() : task;
        } catch (NullPointerException | IllegalArgumentException exception) {
            throw new IllegalValueException(Task.MESSAGE_CONSTRAINTS, exception);
        }
    }
}
