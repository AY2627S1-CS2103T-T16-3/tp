package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TASK_INDEX;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.MarkTaskCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses arguments for {@link MarkTaskCommand}.
 */
public class MarkTaskCommandParser implements Parser<MarkTaskCommand> {

    /**
     * Parses the employee and task indexes for a task mark command.
     *
     * @param args Arguments containing the employee and task indexes.
     * @return The parsed task mark command.
     * @throws ParseException If the arguments are invalid.
     */
    @Override
    public MarkTaskCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_TASK_INDEX);
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_TASK_INDEX);

        if (argMultimap.getPreamble().isEmpty() || argMultimap.getValue(PREFIX_TASK_INDEX).isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, MarkTaskCommand.MESSAGE_USAGE));
        }

        try {
            Index employeeIndex = ParserUtil.parseIndex(argMultimap.getPreamble());
            Index taskIndex = ParserUtil.parseIndex(argMultimap.getValue(PREFIX_TASK_INDEX).get());
            return new MarkTaskCommand(employeeIndex, taskIndex);
        } catch (ParseException exception) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    MarkTaskCommand.MESSAGE_USAGE), exception);
        }
    }
}
