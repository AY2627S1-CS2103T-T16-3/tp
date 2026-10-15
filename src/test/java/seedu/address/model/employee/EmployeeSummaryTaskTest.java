package seedu.address.model.employee;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.EmployeeBuilder;

public class EmployeeSummaryTaskTest {
    @Test
    public void fromEmployees_empty_returnsZeroTaskCounts() {
        assertCounts(EmployeeSummary.fromEmployees(List.of()), 0, 0, 0, 0);
    }

    @Test
    public void fromEmployees_noTasks_countsAllEmployeesAsWithoutTasks() {
        assertCounts(EmployeeSummary.fromEmployees(List.of(employee(), employee())), 0, 0, 2, 0);
    }

    @Test
    public void fromEmployees_mixedTasks_countsAssignmentsNotUniqueDescriptions() {
        Employee alice = employee(new Task("Report"), new Task("Review"));
        Employee bob = employee(new Task("Report"));
        assertCounts(EmployeeSummary.fromEmployees(List.of(alice, bob, employee())), 3, 2, 1, 2);
    }

    @Test
    public void fromEmployees_tiedMaximum_returnsCorrectCounts() {
        Employee alice = employee(new Task("Report"), new Task("Review"));
        Employee bob = employee(new Task("Meeting"), new Task("Budget"));
        assertCounts(EmployeeSummary.fromEmployees(List.of(alice, bob)), 4, 2, 0, 2);
    }

    @Test
    public void fromEmployees_snapshot_keepsCountsAfterInputChanges() {
        List<Employee> employees = new ArrayList<>(List.of(employee(new Task("Report"))));
        EmployeeSummary summary = EmployeeSummary.fromEmployees(employees);
        employees.clear();
        assertCounts(summary, 1, 1, 0, 1);
        assertCounts(EmployeeSummary.fromEmployees(employees), 0, 0, 0, 0);
    }

    private Employee employee(Task... tasks) {
        return new EmployeeBuilder().withTasks(tasks).build();
    }

    private void assertCounts(EmployeeSummary summary, long total, int withTasks, int withoutTasks, int highest) {
        assertEquals(total, summary.getTotalTasks());
        assertEquals(withTasks, summary.getEmployeesWithTasks());
        assertEquals(withoutTasks, summary.getEmployeesWithoutTasks());
        assertEquals(highest, summary.getHighestTaskCount());
    }
}
