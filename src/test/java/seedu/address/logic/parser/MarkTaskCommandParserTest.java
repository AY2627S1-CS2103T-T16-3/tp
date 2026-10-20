package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.getErrorMessageForDuplicatePrefixes;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TASK_INDEX;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.MarkTaskCommand;

public class MarkTaskCommandParserTest {

    private final MarkTaskCommandParser parser = new MarkTaskCommandParser();

    @Test
    public void parse_validArgs_returnsCommand() {
        assertParseSuccess(parser, "1 i/2",
                new MarkTaskCommand(Index.fromOneBased(1), Index.fromOneBased(2)));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        assertParseFailure(parser, "", "Invalid command format!\n" + MarkTaskCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "i/1", "Invalid command format!\n" + MarkTaskCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "1", "Invalid command format!\n" + MarkTaskCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "1 i/1 i/2", getErrorMessageForDuplicatePrefixes(PREFIX_TASK_INDEX));
        assertParseFailure(parser, "zero i/1",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, MarkTaskCommand.MESSAGE_USAGE));
        assertParseFailure(parser, "1 i/zero",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, MarkTaskCommand.MESSAGE_USAGE));
    }
}
