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
    public void constructor_withoutStatus_createsPendingTask() {
        assertFalse(new Task("Prepare report").isDone());
    }

    @Test
    public void constructor_withDoneStatus_createsCompletedTask() {
        assertTrue(new Task("Prepare report").markDone().isDone());
    }

    @Test
    public void markDone_returnsCompletedCopy() {
        Task task = new Task("Prepare report");

        assertEquals(new Task("Prepare report").markDone(), task.markDone());
        assertFalse(task.isDone());
    }

    @Test
    public void markUndone_returnsPendingCopy() {
        Task task = new Task("Prepare report").markDone();

        assertEquals(new Task("Prepare report"), task.markUndone());
        assertTrue(task.isDone());
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
    public void equals_unicodeWhitespaceDifferences_returnsTrue() {
        Task firstTask = new Task("Prepare report");
        Task secondTask = new Task("\u2003Prepare\u2003\u2003report\u2003");

        assertEquals(firstTask, secondTask);
        assertEquals(firstTask.hashCode(), secondTask.hashCode());
    }

    @Test
    public void equals_differentDetails_returnsFalse() {
        assertFalse(new Task("Prepare report").equals(new Task("Submit report")));
    }

    @Test
    public void hasSameDetails_caseAndWhitespaceDifferences_returnsTrue() {
        assertTrue(new Task("Prepare   Report").hasSameDetails(new Task(" prepare report ").markDone()));
    }

    @Test
    public void hasSameDetails_differentDetails_returnsFalse() {
        assertFalse(new Task("Prepare report").hasSameDetails(new Task("Submit report").markDone()));
    }

    @Test
    public void equals_differentStatus_returnsFalse() {
        assertFalse(new Task("Prepare report").equals(new Task("Prepare report").markDone()));
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
        assertEquals("[ ] Prepare report", new Task("Prepare report").toString());
        assertEquals("[x] Prepare report", new Task("Prepare report").markDone().toString());
    }
}
