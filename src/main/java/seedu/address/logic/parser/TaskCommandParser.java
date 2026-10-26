package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.logic.commands.AddTaskCommand;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.DeleteTaskCommand;
import seedu.address.logic.commands.ListTasksCommand;
import seedu.address.logic.commands.MarkTaskCommand;
import seedu.address.logic.commands.UnmarkTaskCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/** Parses subcommands under the {@code task} command group. */
public class TaskCommandParser implements Parser<Command> {

    public static final String MESSAGE_USAGE = "Available task commands:\n\n"
            + AddTaskCommand.MESSAGE_USAGE + "\n\n"
            + DeleteTaskCommand.MESSAGE_USAGE + "\n\n"
            + ListTasksCommand.MESSAGE_USAGE + "\n\n"
            + MarkTaskCommand.MESSAGE_USAGE + "\n\n"
            + UnmarkTaskCommand.MESSAGE_USAGE;

    private static final Pattern SUBCOMMAND_FORMAT = Pattern.compile("(?<subcommandWord>\\S+)(?<arguments>.*)");

    @Override
    public Command parse(String args) throws ParseException {
        Matcher matcher = SUBCOMMAND_FORMAT.matcher(args.trim());
        if (!matcher.matches()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, MESSAGE_USAGE));
        }

        String subcommandWord = matcher.group("subcommandWord");
        String arguments = matcher.group("arguments");
        return switch (subcommandWord) {
        case AddTaskCommand.SUBCOMMAND_WORD -> new AddTaskCommandParser().parse(arguments);
        case DeleteTaskCommand.SUBCOMMAND_WORD -> new DeleteTaskCommandParser().parse(arguments);
        case ListTasksCommand.SUBCOMMAND_WORD -> new ListTasksCommandParser().parse(arguments);
        case MarkTaskCommand.SUBCOMMAND_WORD -> new MarkTaskCommandParser().parse(arguments);
        case UnmarkTaskCommand.SUBCOMMAND_WORD -> new UnmarkTaskCommandParser().parse(arguments);
        default -> throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, MESSAGE_USAGE));
        };
    }
}
