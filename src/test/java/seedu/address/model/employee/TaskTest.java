package seedu.address.model.employee;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
}
