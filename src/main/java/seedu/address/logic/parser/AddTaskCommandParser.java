package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TASK_DETAILS;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.AddTaskCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/** Parses arguments for {@link AddTaskCommand}. */
public class AddTaskCommandParser implements Parser<AddTaskCommand> {

    /**
     * Parses the employee index and task details for an add-task command.
     *
     * @param args Arguments containing the employee index and task details.
     * @return The parsed add-task command.
     * @throws ParseException If the arguments are invalid.
     */
    @Override
    public AddTaskCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_TASK_DETAILS);
        try {
            argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_TASK_DETAILS);
            Index employeeIndex = ParserUtil.parseIndex(argMultimap.getPreamble());
            String details = argMultimap.getValue(PREFIX_TASK_DETAILS)
                    .orElseThrow(() -> new ParseException("Task details are missing."));
            return new AddTaskCommand(employeeIndex, ParserUtil.parseTask(details));
        } catch (ParseException exception) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    AddTaskCommand.MESSAGE_USAGE), exception);
        }
    }
}
