package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TASK_DETAILS;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.AddTaskCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.employee.Task;

/**
 * Parses arguments for {@link AddTaskCommand}.
 */
public class AddTaskCommandParser implements Parser<AddTaskCommand> {

    private static final Pattern PREFIX_PATTERN = Pattern.compile("(?<!\\S)([A-Za-z]+/)");
    private static final String MESSAGE_UNSUPPORTED_PREFIX = "Unsupported prefix in task details: %s\n"
            + "To use it as task text, escape the slash as %s.";

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
        verifyNoUnsupportedPrefixes(args);
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
        String taskDetails = ParserUtil.unescapeSlashes(argMultimap.getValue(PREFIX_TASK_DETAILS).get());
        Task task = ParserUtil.parseTask(taskDetails);
        return new AddTaskCommand(employeeIndex, task);
    }

    private void verifyNoUnsupportedPrefixes(String args) throws ParseException {
        Matcher matcher = PREFIX_PATTERN.matcher(args);
        while (matcher.find()) {
            String prefix = matcher.group(1);
            if (!prefix.equals(PREFIX_TASK_DETAILS.getPrefix())) {
                throw new ParseException(String.format(MESSAGE_UNSUPPORTED_PREFIX,
                        prefix, prefix.replace("/", "\\/")));
            }
        }
    }
}
