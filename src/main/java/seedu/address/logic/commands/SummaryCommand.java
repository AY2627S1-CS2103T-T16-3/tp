package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.math.BigDecimal;

import seedu.address.model.Model;
import seedu.address.model.employee.EmployeeSummary;

/**
 * Displays a company-wide employee summary without changing records or the current filter.
 */
public class SummaryCommand extends Command {
    public static final String COMMAND_WORD = "summary";
    public static final String COMMAND_ALIAS = "/summary";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Shows employee counts, tags, assigned task counts, and recorded salary statistics.\n"
            + "Usage: summary (alias: /summary). No arguments are accepted.";

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        EmployeeSummary summary = EmployeeSummary.fromEmployees(model.getAddressBook().getEmployeeList());
        return new CommandResult(formatSummary(summary) + formatTaskSummary(summary) + formatSalarySummary(summary));
    }

    private String formatTaskSummary(EmployeeSummary summary) {
        return "\n\nTask overview\nTotal assigned tasks: " + summary.getTotalTasks()
                + "\nEmployees with tasks: " + summary.getEmployeesWithTasks()
                + "\nEmployees without tasks: " + summary.getEmployeesWithoutTasks()
                + "\nMost tasks assigned to one employee: " + summary.getHighestTaskCount();
    }

    private String formatSalarySummary(EmployeeSummary summary) {
        String average = summary.getAverageSalary().map(value -> "$" + value.toPlainString()).orElse("N/A");
        String lowest = summary.getLowestSalary().isPresent()
                ? formatSalary(summary.getLowestSalary().getAsInt()) : "N/A";
        String highest = summary.getHighestSalary().isPresent()
                ? formatSalary(summary.getHighestSalary().getAsInt()) : "N/A";
        return "\n\nSalary overview\nTotal recorded salary: " + formatSalary(summary.getTotalSalary())
                + "\nAverage salary: " + average
                + "\nLowest salary: " + lowest
                + "\nHighest salary: " + highest;
    }

    private String formatSalary(long amount) {
        return "$" + BigDecimal.valueOf(amount).setScale(2).toPlainString();
    }

    private String formatSummary(EmployeeSummary summary) {
        StringBuilder result = new StringBuilder("Employee summary\nTotal employees: ")
                .append(summary.getTotalEmployees())
                .append("\nEmployees without tags: ")
                .append(summary.getEmployeesWithoutTags());
        if (summary.getTagCounts().isEmpty()) {
            return result.append("\n\nNo tags to summarize.").toString();
        }
        result.append("\n\nEmployees by tag:");
        summary.getTagCounts().forEach((tag, count) -> result.append("\n  ")
                .append(tag).append(": ").append(count));
        return result.append("\n\nEmployees may have multiple tags; tag counts can overlap.").toString();
    }
}
