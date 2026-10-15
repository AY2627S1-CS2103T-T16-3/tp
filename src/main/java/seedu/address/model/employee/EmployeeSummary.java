package seedu.address.model.employee;

import static java.util.Objects.requireNonNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * An immutable snapshot of employee counts, independent of presentation and list filtering.
 */
public final class EmployeeSummary {
    private final int totalEmployees;
    private final int employeesWithoutTags;
    private final SortedMap<String, Integer> tagCounts;
    private final IntSummaryStatistics salaryStatistics;
    private final long totalTasks;
    private final int employeesWithTasks;
    private final int highestTaskCount;

    private EmployeeSummary(int totalEmployees, int employeesWithoutTags, SortedMap<String, Integer> tagCounts,
            long totalTasks, int employeesWithTasks, int highestTaskCount, IntSummaryStatistics salaryStatistics) {
        this.totalEmployees = totalEmployees;
        this.employeesWithoutTags = employeesWithoutTags;
        this.tagCounts = Collections.unmodifiableSortedMap(new TreeMap<>(tagCounts));
        this.totalTasks = totalTasks;
        this.employeesWithTasks = employeesWithTasks;
        this.highestTaskCount = highestTaskCount;
        this.salaryStatistics = salaryStatistics;
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
        IntSummaryStatistics salaries = employees.stream()
                .mapToInt(employee -> Integer.parseInt(employee.getSalary().salary))
                .summaryStatistics();
        return new EmployeeSummary(employees.size(), untagged, counts,
                totalTasks, employeesWithTasks, highestTaskCount, salaries);
    }

    public int getTotalEmployees() {
        return totalEmployees;
    }

    /**
     * Returns only the employees whose tag set is empty, rather than the total headcount.
     */
    public int getEmployeesWithoutTags() {
        return employeesWithoutTags;
    }

    public long getTotalSalary() {
        return salaryStatistics.getSum();
    }

    /**
     * Returns the average rounded to two decimal places using HALF_UP, or empty for no employees.
     * Zero salaries are included. No floating-point conversion is performed.
     */
    public Optional<BigDecimal> getAverageSalary() {
        if (totalEmployees == 0) {
            return Optional.empty();
        }
        return Optional.of(BigDecimal.valueOf(getTotalSalary())
                .divide(BigDecimal.valueOf(totalEmployees), 2, RoundingMode.HALF_UP));
    }

    public OptionalInt getLowestSalary() {
        return totalEmployees == 0 ? OptionalInt.empty() : OptionalInt.of(salaryStatistics.getMin());
    }

    public OptionalInt getHighestSalary() {
        return totalEmployees == 0 ? OptionalInt.empty() : OptionalInt.of(salaryStatistics.getMax());
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
