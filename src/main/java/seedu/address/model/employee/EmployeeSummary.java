package seedu.address.model.employee;

import static java.util.Objects.requireNonNull;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * An immutable snapshot of employee counts, independent of presentation and list filtering.
 */
public final class EmployeeSummary {
    private final int totalEmployees;
    private final int employeesWithoutTags;
    private final SortedMap<String, Integer> tagCounts;

    private EmployeeSummary(int totalEmployees, int employeesWithoutTags, SortedMap<String, Integer> tagCounts) {
        this.totalEmployees = totalEmployees;
        this.employeesWithoutTags = employeesWithoutTags;
        this.tagCounts = Collections.unmodifiableSortedMap(new TreeMap<>(tagCounts));
    }

    /**
     * Counts employees and their tags without modifying the input.
     * Tags are grouped case-insensitively and each employee counts at most once per group.
     */
    public static EmployeeSummary fromEmployees(List<Employee> employees) {
        requireNonNull(employees);
        int untagged = 0;
        SortedMap<String, Integer> counts = new TreeMap<>();
        for (Employee employee : employees) {
            if (employee.getTags().isEmpty()) {
                untagged++;
            }
            employee.getTags().stream()
                    .map(tag -> tag.tagName.toLowerCase(Locale.ROOT))
                    .distinct()
                    .forEach(tag -> counts.merge(tag, 1, Integer::sum));
        }
        return new EmployeeSummary(employees.size(), untagged, counts);
    }

    public int getTotalEmployees() {
        return totalEmployees;
    }

    public int getEmployeesWithoutTags() {
        return employeesWithoutTags;
    }

    /**
     * Returns unmodifiable tag counts in alphabetical order, with lowercase group labels.
     */
    public SortedMap<String, Integer> getTagCounts() {
        return tagCounts;
    }
}
