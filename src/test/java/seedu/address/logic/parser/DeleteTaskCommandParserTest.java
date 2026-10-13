package seedu.address.logic.parser;

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
        assertParseFailure(parser, "1 i/1 i/2", "Invalid command format!\n"
                + DeleteTaskCommand.MESSAGE_USAGE);
    }
}
