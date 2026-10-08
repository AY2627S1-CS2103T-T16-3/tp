package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SALARY;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.SalaryCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.employee.Salary;

/**
 * Parses salary command arguments, identifying the employee by their displayed list index.
 */
public class SalaryCommandParser implements Parser<SalaryCommand> {

    /**
     * Parses a displayed employee index and a salary into a {@code SalaryCommand}.
     *
     * @throws ParseException if the index or salary is invalid, or the salary prefix is missing or repeated.
     */
    public SalaryCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_SALARY);

        Index index;
        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    SalaryCommand.MESSAGE_USAGE), pe);
        }

        if (argMultimap.getValue(PREFIX_SALARY).isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, SalaryCommand.MESSAGE_USAGE));
        }
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_SALARY);

        Salary salary = ParserUtil.parseSalary(argMultimap.getValue(PREFIX_SALARY).get());

        return new SalaryCommand(index, salary);
    }

}
