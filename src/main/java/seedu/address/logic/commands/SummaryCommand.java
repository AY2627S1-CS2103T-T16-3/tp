package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.model.Model;
import seedu.address.model.employee.EmployeeSummary;

/**
 * Displays a company-wide employee summary without changing records or the current filter.
 */
public class SummaryCommand extends Command {
    public static final String COMMAND_WORD = "summary";
    public static final String COMMAND_ALIAS = "/summary";
    public static final String MESSAGE_USAGE = COMMAND_ALIAS
            + ": Shows total employees, employees without tags, and employee counts by tag.\n"
            + "Usage: /summary (or summary). No arguments are accepted.";

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        EmployeeSummary summary = EmployeeSummary.fromEmployees(model.getAddressBook().getEmployeeList());
        return new CommandResult(formatSummary(summary));
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
