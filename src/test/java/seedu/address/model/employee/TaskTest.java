package seedu.address.model.employee;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TaskTest {

    @Test
    public void constructor_validDetails_trimsDetails() {
        Task task = new Task("  Prepare report  ");

        assertEquals("Prepare report", task.getDetails());
    }

    @Test
    public void constructor_blankDetails_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Task("   "));
    }

    @Test
    public void constructor_detailsExceedingMaximumLength_throwsIllegalArgumentException() {
        String details = "a".repeat(Task.MAX_DETAILS_LENGTH + 1);

        assertThrows(IllegalArgumentException.class, () -> new Task(details));
    }

    @Test
    public void constructor_detailsAtMaximumLength_createsTask() {
        String details = "a".repeat(Task.MAX_DETAILS_LENGTH);

        assertEquals(details, new Task(details).getDetails());
    }

    @Test
    public void constructor_nullDetails_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Task(null));
    }

    @Test
    public void isValidDetails_invalidDetails_returnsFalse() {
        assertFalse(Task.isValidDetails(null));
        assertFalse(Task.isValidDetails("   "));
        assertFalse(Task.isValidDetails("a".repeat(Task.MAX_DETAILS_LENGTH + 1)));
    }

    @Test
    public void isValidDetails_validDetails_returnsTrue() {
        assertTrue(Task.isValidDetails("Prepare report"));
        assertTrue(Task.isValidDetails("a".repeat(Task.MAX_DETAILS_LENGTH)));
    }

    @Test
    public void equals_caseAndWhitespaceDifferences_returnsTrue() {
        Task firstTask = new Task("Prepare   Report");
        Task secondTask = new Task(" prepare report ");

        assertEquals(firstTask, secondTask);
        assertEquals(firstTask.hashCode(), secondTask.hashCode());
    }

    @Test
    public void equals_differentDetails_returnsFalse() {
        assertFalse(new Task("Prepare report").equals(new Task("Submit report")));
    }

    @Test
    public void equals_sameObject_returnsTrue() {
        Task task = new Task("Prepare report");

        assertTrue(task.equals(task));
    }

    @Test
    public void equals_differentType_returnsFalse() {
        assertFalse(new Task("Prepare report").equals("Prepare report"));
    }

    @Test
    public void toString_returnsTaskDetails() {
        assertEquals("Prepare report", new Task("Prepare report").toString());
    }
}
