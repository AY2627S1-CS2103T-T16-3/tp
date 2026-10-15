package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.getErrorMessageForDuplicatePrefixes;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TASK_INDEX;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.DeleteTaskCommand;

public class DeleteTaskCommandParserTest {

    private final DeleteTaskCommandParser parser = new DeleteTaskCommandParser();

    @Test
    public void parse_validArgs_returnsCommand() {
        assertParseSuccess(parser, "1 i/2",
                new DeleteTaskCommand(Index.fromOneBased(1), Index.fromOneBased(2)));
    }

    @Test
    public void parse_missingTaskIndex_throwsParseException() {
        assertParseFailure(parser, "1", "Invalid command format!\n"
                + DeleteTaskCommand.MESSAGE_USAGE);
    }

    @Test
    public void parse_duplicateTaskIndexPrefix_throwsParseException() {
        assertParseFailure(parser, "1 i/1 i/2",
                getErrorMessageForDuplicatePrefixes(PREFIX_TASK_INDEX));
    }

    @Test
    public void parse_invalidIndexes_throwsParseException() {
        assertParseFailure(parser, "zero i/1",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteTaskCommand.MESSAGE_USAGE));
        assertParseFailure(parser, "1 i/zero",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteTaskCommand.MESSAGE_USAGE));
    }
}
