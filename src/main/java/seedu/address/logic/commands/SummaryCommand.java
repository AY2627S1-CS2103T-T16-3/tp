package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.math.BigDecimal;
import java.util.List;

import seedu.address.model.Model;
import seedu.address.model.employee.Employee;
import seedu.address.model.employee.EmployeeSummary;

/**
 * Displays an employee summary for the selected scope without changing records or the current filter.
 */
public class SummaryCommand extends Command {
    public static final String COMMAND_WORD = "summary";
    public static final String COMMAND_ALIAS = "/summary";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Shows employee counts, tags, assigned task counts, and recorded salary statistics.\n"
            + "Usage: summary [scope/all|scope/filtered] (alias: /summary). Default scope: filtered.";

    /** Selects the employee collection to summarize at execution time. */
    public enum Scope {
        ALL("All employees"),
        FILTERED("Filtered employees");

        private final String label;

        Scope(String label) {
            this.label = label;
        }
    }

    private final Scope scope;

    public SummaryCommand() {
        this(Scope.FILTERED);
    }

    public SummaryCommand(Scope scope) {
        this.scope = requireNonNull(scope);
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        List<Employee> employees = scope == Scope.ALL
                ? model.getAddressBook().getEmployeeList() : model.getFilteredEmployeeList();
        EmployeeSummary summary = EmployeeSummary.fromEmployees(employees);
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
        StringBuilder result = new StringBuilder("Employee summary\nScope: ")
                .append(scope.label)
                .append("\nTotal employees: ")
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

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof SummaryCommand otherCommand && scope == otherCommand.scope;
    }

    @Override
    public int hashCode() {
        return scope.hashCode();
    }
}
