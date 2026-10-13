package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TASK_INDEX;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.DeleteTaskCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/** Parses arguments for {@link DeleteTaskCommand}. */
public class DeleteTaskCommandParser implements Parser<DeleteTaskCommand> {

    /**
     * Parses the employee and task indexes for a delete-task command.
     *
     * @param args Arguments containing the employee and task indexes.
     * @return The parsed delete-task command.
     * @throws ParseException If the arguments are invalid.
     */
    @Override
    public DeleteTaskCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_TASK_INDEX);
        try {
            argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_TASK_INDEX);
            Index employeeIndex = ParserUtil.parseIndex(argMultimap.getPreamble());
            String taskIndex = argMultimap.getValue(PREFIX_TASK_INDEX)
                    .orElseThrow(() -> new ParseException("Task index is missing."));
            return new DeleteTaskCommand(employeeIndex, ParserUtil.parseIndex(taskIndex));
        } catch (ParseException exception) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    DeleteTaskCommand.MESSAGE_USAGE), exception);
        }
    }
}
