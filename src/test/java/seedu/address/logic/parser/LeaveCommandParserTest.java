package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

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
}
