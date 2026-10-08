package seedu.address.model.employee;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.EmployeeBuilder;

public class EmployeeSummaryTest {
    @Test
    public void fromEmployees_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> EmployeeSummary.fromEmployees(null));
    }

    @Test
    public void fromEmployees_emptyList_returnsZeroCounts() {
        EmployeeSummary summary = EmployeeSummary.fromEmployees(List.of());
        assertEquals(0, summary.getTotalEmployees());
        assertEquals(0, summary.getEmployeesWithoutTags());
        assertEquals(Map.of(), summary.getTagCounts());
    }

    @Test
    public void fromEmployees_noTags_countsEveryEmployeeAsUntagged() {
        EmployeeSummary summary = EmployeeSummary.fromEmployees(List.of(
                employee("Alice"), employee("Bob")));
        assertEquals(2, summary.getTotalEmployees());
        assertEquals(2, summary.getEmployeesWithoutTags());
        assertEquals(Map.of(), summary.getTagCounts());
    }

    @Test
    public void fromEmployees_overlappingCaseVariants_countsEmployeeOncePerGroupInSortedOrder() {
        EmployeeSummary summary = EmployeeSummary.fromEmployees(List.of(
                employee("Alice", "Remote", "Engineering", "engineering"),
                employee("Bob", "ENGINEERING", "intern"), employee("Carol")));
        assertEquals(3, summary.getTotalEmployees());
        assertEquals(1, summary.getEmployeesWithoutTags());
        assertEquals(Map.of("engineering", 2, "intern", 1, "remote", 1), summary.getTagCounts());
        assertEquals(List.of("engineering", "intern", "remote"),
                new ArrayList<>(summary.getTagCounts().keySet()));
    }

    @Test
    public void fromEmployees_differentInputOrder_returnsSameCountsAndOrder() {
        Employee alice = employee("Alice", "zebra", "Alpha");
        Employee bob = employee("Bob", "alpha");
        EmployeeSummary first = EmployeeSummary.fromEmployees(List.of(alice, bob));
        EmployeeSummary reversed = EmployeeSummary.fromEmployees(List.of(bob, alice));
        assertEquals(first.getTagCounts(), reversed.getTagCounts());
        assertEquals(List.of("alpha", "zebra"), new ArrayList<>(reversed.getTagCounts().keySet()));
    }

    @Test
    public void fromEmployees_turkishDefaultLocale_usesLocaleIndependentGrouping() {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            EmployeeSummary summary = EmployeeSummary.fromEmployees(List.of(employee("Alice", "INTERN", "intern")));
            assertEquals(Map.of("intern", 1), summary.getTagCounts());
        } finally {
            Locale.setDefault(original);
        }
    }

    @Test
    public void fromEmployees_snapshot_doesNotChangeInputOrExposeMutableCounts() {
        Employee alice = employee("Alice", "Engineering");
        List<Employee> employees = new ArrayList<>(List.of(alice));
        EmployeeSummary summary = EmployeeSummary.fromEmployees(employees);
        assertEquals(List.of(alice), employees);
        assertEquals("Engineering", alice.getTags().iterator().next().tagName);
        employees.clear();
        assertEquals(1, summary.getTotalEmployees());
        assertEquals(Map.of("engineering", 1), summary.getTagCounts());
        assertThrows(UnsupportedOperationException.class, () -> summary.getTagCounts().put("remote", 1));
    }

    private Employee employee(String name, String... tags) {
        return new EmployeeBuilder().withName(name).withTags(tags).build();
    }
}
