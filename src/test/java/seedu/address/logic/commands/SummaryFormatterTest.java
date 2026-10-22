package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.employee.Employee;
import seedu.address.model.employee.EmployeeSummary;
import seedu.address.model.employee.Task;
import seedu.address.testutil.EmployeeBuilder;

public class SummaryFormatterTest {
    @Test
    public void format_emptySummary_returnsEssentialZeroValues() {
        String expected = """
                +----------------------+-------+
                | Metric               | Value |
                +----------------------+-------+
                | Employees            |     0 |
                | Without tags         |     0 |
                | Assigned tasks       |     0 |
                | Employees with tasks |     0 |
                | Total salary         | $0.00 |
                | Average salary       |   N/A |
                +----------------------+-------+""";
        assertEquals(expected, SummaryFormatter.format(EmployeeSummary.fromEmployees(List.of())));
    }

    @Test
    public void format_mixedEmployees_returnsAlignedMetricsAndSortedTags() {
        Employee alice = new EmployeeBuilder().withName("Alice").withTags("Remote", "Engineering", "engineering")
                .withSalary("1000").withTasks(new Task("Report")).build();
        Employee bob = new EmployeeBuilder().withName("Bob").withTags("engineering", "intern")
                .withSalary("3000").withTasks(new Task("Report"), new Task("Review")).build();
        Employee carol = new EmployeeBuilder().withName("Carol").withTags().build();
        String expected = """
                +----------------------+----------+
                | Metric               |    Value |
                +----------------------+----------+
                | Employees            |        3 |
                | Without tags         |        1 |
                | Assigned tasks       |        3 |
                | Employees with tasks |        2 |
                | Total salary         | $4000.00 |
                | Average salary       | $1333.33 |
                +----------------------+----------+

                +-------------+-----------+
                | Tag         | Employees |
                +-------------+-----------+
                | engineering |         2 |
                | intern      |         1 |
                | remote      |         1 |
                +-------------+-----------+""";
        assertEquals(expected, SummaryFormatter.format(EmployeeSummary.fromEmployees(List.of(alice, bob, carol))));
    }

    @Test
    public void format_noTags_omitsTagTableAndExplanations() {
        String result = SummaryFormatter.format(EmployeeSummary.fromEmployees(List.of(new EmployeeBuilder().build())));
        assertFalse(result.contains("| Tag"));
        assertFalse(result.contains("No tags"));
        assertFalse(result.contains("Lowest"));
        assertFalse(result.contains("Highest"));
        assertFalse(result.contains("Most tasks"));
    }

    @Test
    public void format_longTagAndLargeTotal_expandsColumnsWithoutTruncation() {
        String tag = "engineering".repeat(6);
        Employee employee = new EmployeeBuilder().withTags(tag).withSalary("50000").build();
        String result = SummaryFormatter.format(EmployeeSummary.fromEmployees(Collections.nCopies(50000, employee)));
        assertTrue(result.contains(tag));
        assertTrue(result.contains("$2500000000.00"));
        assertTrue(result.contains("$50000.00"));
        for (String table : result.split("\n\n")) {
            assertEquals(1, table.lines().map(String::length).distinct().count());
        }
    }

    @Test
    public void format_nullSummary_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> SummaryFormatter.format(null));
    }
}
