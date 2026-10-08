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
            + ": Lists employees matching all supplied criteria. Text fields use case-insensitive partial matches; "
            + "tags use case-insensitive whole tag matches.\n"
            + "Parameters: [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS] [t/TAG]...\n"
            + "At least one non-empty criterion is required. Only t/ may be repeated.\n"
            + "Example: " + COMMAND_WORD + " p/91235321";

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
