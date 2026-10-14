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
    private final long totalTasks;
    private final int employeesWithTasks;
    private final int highestTaskCount;

    private EmployeeSummary(int totalEmployees, int employeesWithoutTags, SortedMap<String, Integer> tagCounts,
            long totalTasks, int employeesWithTasks, int highestTaskCount) {
        this.totalEmployees = totalEmployees;
        this.employeesWithoutTags = employeesWithoutTags;
        this.tagCounts = Collections.unmodifiableSortedMap(new TreeMap<>(tagCounts));
        this.totalTasks = totalTasks;
        this.employeesWithTasks = employeesWithTasks;
        this.highestTaskCount = highestTaskCount;
    }

    /**
     * Counts employees and their tags without modifying the input.
     * Tags are grouped case-insensitively and each employee counts at most once per group.
     */
    public static EmployeeSummary fromEmployees(List<Employee> employees) {
        requireNonNull(employees);
        int untagged = 0;
        SortedMap<String, Integer> counts = new TreeMap<>();
        long totalTasks = 0;
        int employeesWithTasks = 0;
        int highestTaskCount = 0;
        for (Employee employee : employees) {
            int taskCount = employee.getTasks().size();
            totalTasks += taskCount;
            if (taskCount > 0) {
                employeesWithTasks++;
            }
            highestTaskCount = Math.max(highestTaskCount, taskCount);
            if (employee.getTags().isEmpty()) {
                untagged++;
            }
            employee.getTags().stream()
                    .map(tag -> tag.tagName.toLowerCase(Locale.ROOT))
                    .distinct()
                    .forEach(tag -> counts.merge(tag, 1, Integer::sum));
        }
        return new EmployeeSummary(employees.size(), untagged, counts,
                totalTasks, employeesWithTasks, highestTaskCount);
    }

    public int getTotalEmployees() {
        return totalEmployees;
    }

    public int getEmployeesWithoutTags() {
        return employeesWithoutTags;
    }

    public long getTotalTasks() {
        return totalTasks;
    }

    public int getEmployeesWithTasks() {
        return employeesWithTasks;
    }

    public int getEmployeesWithoutTasks() {
        return totalEmployees - employeesWithTasks;
    }

    public int getHighestTaskCount() {
        return highestTaskCount;
    }

    /**
     * Returns unmodifiable tag counts in alphabetical order, with lowercase group labels.
     */
    public SortedMap<String, Integer> getTagCounts() {
        return tagCounts;
    }
}
