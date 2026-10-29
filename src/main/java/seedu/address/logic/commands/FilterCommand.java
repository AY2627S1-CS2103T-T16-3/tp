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
    public static final String MESSAGE_LEADING_ZEROS =
            "Leading zeros are not allowed in salary or leave filters. Use 14 instead of 014; 0 is allowed.";
    public static final String MESSAGE_TEXT_COMPARATOR =
            "Comparators are only allowed for salary (s/) and annual leave entitlement (le/) filters.";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Match all supplied filters. Provide at least one.\n"
            + "Fields: n/NAME p/PHONE e/EMAIL a/ADDRESS t/TAG task/DETAILS s/SALARY le/DAYS\n"
            + "Text: partial match; tags: whole match (case-insensitive). Repeat only t/ or task/.\n"
            + "Values must follow field rules: names allow letters, digits and spaces; tags are alphanumeric; "
            + "phones need at least 3 digits; emails must be valid; tasks allow up to 120 characters.\n"
            + "Use \\/ in task text when a slash could be mistaken for a field prefix.\n"
            + "Only salary (0-50000) and leave (0-365) support exact values, <, <=, >, >=, "
            + "and inclusive ranges MIN-MAX. No leading zeros; 0 is valid.\n"
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
