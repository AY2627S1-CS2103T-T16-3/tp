package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.employee.Task;

public class JsonAdaptedTaskTest {

    @Test
    public void toModelType_validDetails_returnsTask() throws Exception {
        JsonAdaptedTask task = new JsonAdaptedTask("Prepare report");

        assertEquals(new Task("Prepare report"), task.toModelType());
    }

    @Test
    public void toModelType_invalidDetails_throwsIllegalValueException() {
        JsonAdaptedTask task = new JsonAdaptedTask(" ");

        assertThrows(IllegalValueException.class, task::toModelType);
    }

    @Test
    public void toModelType_nullDetails_throwsIllegalValueException() {
        JsonAdaptedTask task = new JsonAdaptedTask((String) null);

        assertThrows(IllegalValueException.class, task::toModelType);
    }
}
