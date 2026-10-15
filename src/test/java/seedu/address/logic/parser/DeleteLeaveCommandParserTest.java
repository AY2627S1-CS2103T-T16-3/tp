package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.DeleteLeaveCommand;
import seedu.address.model.leave.LeaveId;

public class DeleteLeaveCommandParserTest {

    private final DeleteLeaveCommandParser parser = new DeleteLeaveCommandParser();

    @Test
    public void parse_validLeaveId_returnsDeleteLeaveCommand() {
        assertParseSuccess(parser, "1", new DeleteLeaveCommand(new LeaveId(1)));
    }

    @Test
    public void parse_invalidLeaveId_throwsUsageMessage() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteLeaveCommand.MESSAGE_USAGE);

        assertParseFailure(parser, "", expectedMessage);
        assertParseFailure(parser, "0", expectedMessage);
        assertParseFailure(parser, "abc", expectedMessage);
    }
}
