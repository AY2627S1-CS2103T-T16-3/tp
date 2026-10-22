package seedu.address.testutil;

import static org.junit.jupiter.api.Assertions.assertTrue;

/** Assertions for summary values that do not depend on column padding. */
public class SummaryTestUtil {
    /** Asserts that a two-column table contains the given label and value. */
    public static void assertSummaryValue(String summary, String label, String value) {
        assertTrue(summary.lines().anyMatch(line -> {
            String[] cells = line.split("\\|", -1);
            return cells.length == 4 && cells[1].strip().equals(label) && cells[2].strip().equals(value);
        }), () -> "Missing summary row: " + label + " = " + value + "\n" + summary);
    }
}
