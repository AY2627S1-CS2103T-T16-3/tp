package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TASK_INDEX;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.UnmarkTaskCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses arguments for {@link UnmarkTaskCommand}.
 */
public class UnmarkTaskCommandParser implements Parser<UnmarkTaskCommand> {

    /**
     * Parses the employee and task indexes for an unmark-task command.
     *
     * @param args Arguments containing the employee and task indexes.
     * @return The parsed unmark-task command.
     * @throws ParseException If the arguments are invalid.
     */
    @Override
    public UnmarkTaskCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_TASK_INDEX);
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_TASK_INDEX);

        if (argMultimap.getPreamble().isEmpty() || argMultimap.getValue(PREFIX_TASK_INDEX).isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    UnmarkTaskCommand.MESSAGE_USAGE));
        }

        try {
            Index employeeIndex = ParserUtil.parseIndex(argMultimap.getPreamble());
            Index taskIndex = ParserUtil.parseIndex(argMultimap.getValue(PREFIX_TASK_INDEX).get());
            return new UnmarkTaskCommand(employeeIndex, taskIndex);
        } catch (ParseException exception) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    UnmarkTaskCommand.MESSAGE_USAGE), exception);
        }
    }
}
