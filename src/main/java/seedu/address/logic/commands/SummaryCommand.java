package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
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
            + "Usage: summary [scope/all|scope/filtered] [i/INDEX...] [e/INDEX...] (alias: /summary).\n"
            + "Default scope: filtered. Indexes must be positive integers in the selected scope.\n"
            + "i/ includes only the listed indexes; e/ excludes them. Exclusions take precedence.\n"
            + "Example: summary i/1 2 3 e/2";
    public static final String MESSAGE_INVALID_INDEX = "Index %1$d is invalid. %2$s: %3$d. Valid indexes: %4$s.";

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
    private final Set<Index> includedIndexes;
    private final Set<Index> excludedIndexes;

    public SummaryCommand() {
        this(Scope.FILTERED);
    }

    public SummaryCommand(Scope scope) {
        this(scope, Set.of(), Set.of());
    }

    /**
     * Creates a summary with index selections relative to the chosen scope at execution time.
     * An empty inclusion set includes everyone; exclusions always take precedence.
     */
    public SummaryCommand(Scope scope, Set<Index> includedIndexes, Set<Index> excludedIndexes) {
        this.scope = requireNonNull(scope);
        this.includedIndexes = Set.copyOf(includedIndexes);
        this.excludedIndexes = Set.copyOf(excludedIndexes);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Employee> employees = scope == Scope.ALL
                ? model.getAddressBook().getEmployeeList() : model.getFilteredEmployeeList();
        EmployeeSummary summary = EmployeeSummary.fromEmployees(selectEmployees(employees));
        return new CommandResult(formatSummary(summary) + formatTaskSummary(summary) + formatSalarySummary(summary));
    }

    /** Validates every supplied index before applying inclusion and exclusion to the same list. */
    private List<Employee> selectEmployees(List<Employee> employees) throws CommandException {
        OptionalInt invalidIndex = Stream.concat(includedIndexes.stream(), excludedIndexes.stream())
                .mapToInt(Index::getOneBased)
                .filter(index -> index > employees.size())
                .min();
        if (invalidIndex.isPresent()) {
            String validRange = employees.isEmpty() ? "none" : "1-" + employees.size();
            throw new CommandException(String.format(MESSAGE_INVALID_INDEX,
                    invalidIndex.getAsInt(), scope.label, employees.size(), validRange));
        }
        return IntStream.range(0, employees.size())
                .mapToObj(Index::fromZeroBased)
                .filter(index -> includedIndexes.isEmpty() || includedIndexes.contains(index))
                .filter(index -> !excludedIndexes.contains(index))
                .map(index -> employees.get(index.getZeroBased()))
                .toList();
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
                .append(scope.label);
        if (!includedIndexes.isEmpty()) {
            result.append("\nIncluded indexes: ").append(formatIndexes(includedIndexes));
        }
        if (!excludedIndexes.isEmpty()) {
            result.append("\nExcluded indexes: ").append(formatIndexes(excludedIndexes));
        }
        result.append("\nTotal employees: ")
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

    private List<Integer> formatIndexes(Set<Index> indexes) {
        return indexes.stream().map(Index::getOneBased).sorted().toList();
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof SummaryCommand otherCommand
                && scope == otherCommand.scope
                && includedIndexes.equals(otherCommand.includedIndexes)
                && excludedIndexes.equals(otherCommand.excludedIndexes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scope, includedIndexes, excludedIndexes);
    }
}
