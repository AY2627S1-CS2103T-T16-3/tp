package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.logic.commands.ListTasksCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses arguments for {@link ListTasksCommand}.
 */
public class ListTasksCommandParser implements Parser<ListTasksCommand> {

    /**
     * Parses the employee index for a task list command.
     *
     * @param args Arguments containing the employee index.
     * @return The parsed task list command.
     * @throws ParseException If the arguments are invalid.
     */
    @Override
    public ListTasksCommand parse(String args) throws ParseException {
        try {
            return new ListTasksCommand(ParserUtil.parseIndex(args));
        } catch (ParseException exception) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    ListTasksCommand.MESSAGE_USAGE), exception);
        }
    }
}
