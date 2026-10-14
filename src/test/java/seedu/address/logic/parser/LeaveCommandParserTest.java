package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ListLeaveCommand;
import seedu.address.logic.parser.exceptions.ParseException;

public class LeaveCommandParserTest {

    private final LeaveCommandParser parser = new LeaveCommandParser();

    @Test
    public void parse_missingSubcommand_throwsUsageMessage() {
        assertThrows(ParseException.class,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, LeaveCommandParser.MESSAGE_USAGE), () ->
                    parser.parse(""));
    }

    @Test
    public void parse_unknownSubcommand_throwsUsageMessage() {
        assertThrows(ParseException.class,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, LeaveCommandParser.MESSAGE_USAGE), () ->
                    parser.parse("remove 1"));
    }

    @Test
    public void parse_listWithoutArguments_returnsListLeaveCommand() throws Exception {
        assertInstanceOf(ListLeaveCommand.class, parser.parse("list"));
    }

    @Test
    public void parse_listWithArguments_throwsUsageMessage() {
        assertThrows(ParseException.class,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, LeaveCommandParser.MESSAGE_USAGE), () ->
                    parser.parse("list 1"));
    }
}
