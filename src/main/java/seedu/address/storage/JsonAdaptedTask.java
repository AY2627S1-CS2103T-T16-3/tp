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

    /**
     * Constructs a JSON-adapted task with the given details.
     *
     * @param details Details of the task.
     */
    @JsonCreator
    public JsonAdaptedTask(@JsonProperty("details") String details) {
        this.details = details;
    }

    /**
     * Converts a given {@code Task} into this class for Jackson use.
     *
     * @param source Task to convert.
     */
    public JsonAdaptedTask(Task source) {
        details = source.getDetails();
    }

    /**
     * Converts this Jackson-friendly task object into a model task.
     *
     * @return The converted task.
     * @throws IllegalValueException If the details violate task constraints.
     */
    public Task toModelType() throws IllegalValueException {
        try {
            return new Task(details);
        } catch (NullPointerException | IllegalArgumentException exception) {
            throw new IllegalValueException(Task.MESSAGE_CONSTRAINTS, exception);
        }
    }
}
