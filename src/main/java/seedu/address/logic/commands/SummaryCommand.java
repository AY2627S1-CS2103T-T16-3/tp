package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

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
        return new CommandResult("Employee summary\nScope: " + scope.label + "\n\n" + SummaryFormatter.format(summary),
                CommandResult.DisplayFormat.TABLE);
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
