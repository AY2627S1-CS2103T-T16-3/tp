package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.logic.commands.SummaryCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses a summary command with an optional, single scope parameter.
 */
public class SummaryCommandParser implements Parser<SummaryCommand> {
    @Override
    public SummaryCommand parse(String args) throws ParseException {
        requireNonNull(args);
        return switch (args.strip()) {
            case "scope/all" -> new SummaryCommand(SummaryCommand.Scope.ALL);
            case "", "scope/filtered" -> new SummaryCommand(SummaryCommand.Scope.FILTERED);
            default -> throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, SummaryCommand.MESSAGE_USAGE));
        };
    }
}
