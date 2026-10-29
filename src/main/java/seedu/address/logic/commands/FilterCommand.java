package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.employee.EmployeeMatchesCriteriaPredicate;

/**
 * Lists employees matching all specified field criteria.
 */
public class FilterCommand extends Command {

    public static final String COMMAND_WORD = "filter";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Match all supplied filters. Provide at least one.\n"
            + "Fields: n/NAME p/PHONE e/EMAIL a/ADDRESS t/TAG task/DETAILS s/SALARY le/DAYS\n"
            + "Text Fields: Partial Matches Allowed; Tags: Whole match Required (case-insensitive). "
            + "Only t/ or task/ fields may be repeated.\n"
            + "Salary (0-50000) and leave (0-365) fields only: supports exact, <, <=, >, >=, "
            + "and inclusive range MIN-MAX.\n"
            + "Example: filter task/report s/3000-5000 le/>=14";

    private final EmployeeMatchesCriteriaPredicate predicate;

    public FilterCommand(EmployeeMatchesCriteriaPredicate predicate) {
        this.predicate = requireNonNull(predicate);
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredEmployeeList(predicate);
        return new CommandResult(String.format(Messages.MESSAGE_EMPLOYEES_LISTED_OVERVIEW,
                model.getFilteredEmployeeList().size()));
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof FilterCommand otherCommand
                && predicate.equals(otherCommand.predicate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("predicate", predicate).toString();
    }
}
