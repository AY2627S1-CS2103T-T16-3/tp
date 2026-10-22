package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.employee.Task;

public class JsonAdaptedTaskTest {

    @Test
    public void toModelType_validDetails_returnsTask() throws Exception {
        JsonAdaptedTask task = new JsonAdaptedTask("Prepare report", false);

        assertEquals(new Task("Prepare report"), task.toModelType());
    }

    @Test
    public void toModelType_invalidDetails_throwsIllegalValueException() {
        JsonAdaptedTask task = new JsonAdaptedTask(" ", false);

        assertThrows(IllegalValueException.class, task::toModelType);
    }

    @Test
    public void toModelType_nullDetails_throwsIllegalValueException() {
        JsonAdaptedTask task = new JsonAdaptedTask(null, false);

        assertThrows(IllegalValueException.class, task::toModelType);
    }

    @Test
    public void jsonRoundTrip_taskPreserved() throws Exception {
        Task original = new Task("Prepare report");
        JsonAdaptedTask adapted = new JsonAdaptedTask(original);

        String json = JsonUtil.toJsonString(adapted);
        JsonAdaptedTask restored = JsonUtil.fromJsonString(json, JsonAdaptedTask.class);

        assertEquals(original, restored.toModelType());
    }

    @Test
    public void jsonRoundTrip_completedTaskPreserved() throws Exception {
        Task original = new Task("Prepare report").markDone();
        JsonAdaptedTask adapted = new JsonAdaptedTask(original);

        String json = JsonUtil.toJsonString(adapted);
        JsonAdaptedTask restored = JsonUtil.fromJsonString(json, JsonAdaptedTask.class);

        assertTrue(json.contains("\"isDone\"") && json.contains("true"));
        assertEquals(original, restored.toModelType());
    }

    @Test
    public void missingStatus_defaultsToPending() throws Exception {
        JsonAdaptedTask adapted = JsonUtil.fromJsonString(
                "{\"details\":\"Prepare report\"}", JsonAdaptedTask.class);

        assertEquals(new Task("Prepare report"), adapted.toModelType());
    }
}
