package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import seedu.address.model.employee.EmployeeSummary;

/** Formats essential summary figures as ASCII tables with right-aligned values. */
final class SummaryFormatter {
    private SummaryFormatter() {
    }

    /** Returns a metrics table followed by a tag table when tags are present. */
    static String format(EmployeeSummary summary) {
        requireNonNull(summary);
        Map<String, String> metrics = new LinkedHashMap<>();
        metrics.put("Employees", String.valueOf(summary.getTotalEmployees()));
        metrics.put("Without tags", String.valueOf(summary.getEmployeesWithoutTags()));
        metrics.put("Assigned tasks", String.valueOf(summary.getTotalTasks()));
        metrics.put("Employees with tasks", String.valueOf(summary.getEmployeesWithTasks()));
        metrics.put("Leave records", String.valueOf(summary.getTotalLeaveRecords()));
        metrics.put("Total salary", "$" + BigDecimal.valueOf(summary.getTotalSalary()).setScale(2).toPlainString());
        metrics.put("Average salary", summary.getAverageSalary()
                .map(value -> "$" + value.toPlainString()).orElse("N/A"));

        String result = formatTable("Metric", "Value", metrics);
        if (summary.getTagCounts().isEmpty()) {
            return result;
        }
        Map<String, String> tags = new LinkedHashMap<>();
        summary.getTagCounts().forEach((tag, count) -> tags.put(tag, count.toString()));
        return result + "\n\n" + formatTable("Tag", "Employees", tags);
    }

    private static String formatTable(String labelHeader, String valueHeader, Map<String, String> rows) {
        int labelWidth = columnWidth(labelHeader, rows.keySet());
        int valueWidth = columnWidth(valueHeader, rows.values());
        String border = "+" + "-".repeat(labelWidth + 2) + "+" + "-".repeat(valueWidth + 2) + "+";
        String rowFormat = "| %-" + labelWidth + "s | %" + valueWidth + "s |";
        StringBuilder table = new StringBuilder(border).append("\n")
                .append(String.format(rowFormat, labelHeader, valueHeader)).append("\n").append(border);
        rows.forEach((label, value) -> table.append("\n").append(String.format(rowFormat, label, value)));
        return table.append("\n").append(border).toString();
    }

    private static int columnWidth(String header, Collection<String> values) {
        return Math.max(header.length(), values.stream().mapToInt(String::length).max().orElse(0));
    }
}
