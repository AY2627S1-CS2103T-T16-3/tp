package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class RemarkTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Remark(null));
    }

    @Test
    public void equals() {
        Remark remark = new Remark("Hello");

        assertTrue(remark.equals(remark));
        assertTrue(remark.equals(new Remark("Hello")));
        assertFalse(remark.equals(null));
        assertFalse(remark.equals(1));
        assertFalse(remark.equals(new Remark("Bye")));
    }

    @Test
    public void toStringMethod() {
        assertEquals("Likes baseball", new Remark("Likes baseball").toString());
    }
}
