package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TASK_INDEX;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.DeleteTaskCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses arguments for {@link DeleteTaskCommand}.
 */
public class DeleteTaskCommandParser implements Parser<DeleteTaskCommand> {

    /**
     * Parses the employee and task indexes for a task delete command.
     *
     * @param args Arguments containing the employee and task indexes.
     * @return The parsed task delete command.
     * @throws ParseException If the arguments are invalid.
     */
    @Override
    public DeleteTaskCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_TASK_INDEX);
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_TASK_INDEX);

        if (argMultimap.getPreamble().isEmpty() || argMultimap.getValue(PREFIX_TASK_INDEX).isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteTaskCommand.MESSAGE_USAGE));
        }

        try {
            Index employeeIndex = ParserUtil.parseIndex(argMultimap.getPreamble());
            Index taskIndex = ParserUtil.parseIndex(argMultimap.getValue(PREFIX_TASK_INDEX).get());
            return new DeleteTaskCommand(employeeIndex, taskIndex);
        } catch (ParseException e) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteTaskCommand.MESSAGE_USAGE));
        }
    }
}
