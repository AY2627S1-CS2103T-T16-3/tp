package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TASK_DETAILS;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.AddTaskCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.employee.Task;

/**
 * Parses arguments for {@link AddTaskCommand}.
 */
public class AddTaskCommandParser implements Parser<AddTaskCommand> {

    /**
     * Parses the employee index and task details for a task add command.
     *
     * @param args Arguments containing the employee index and task details.
     * @return The parsed task add command.
     * @throws ParseException If the arguments are invalid.
     */
    @Override
    public AddTaskCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_TASK_DETAILS);
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_TASK_DETAILS);

        if (argMultimap.getPreamble().isEmpty() || argMultimap.getValue(PREFIX_TASK_DETAILS).isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddTaskCommand.MESSAGE_USAGE));
        }

        Index employeeIndex;
        try {
            employeeIndex = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (ParseException e) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddTaskCommand.MESSAGE_USAGE), e);
        }
        Task task = ParserUtil.parseTask(argMultimap.getValue(PREFIX_TASK_DETAILS).get());
        return new AddTaskCommand(employeeIndex, task);
    }
}
