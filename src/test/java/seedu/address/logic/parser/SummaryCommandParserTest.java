package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.SummaryCommand;
import seedu.address.logic.parser.exceptions.ParseException;

public class SummaryCommandParserTest {
    private final SummaryCommandParser parser = new SummaryCommandParser();

    @Test
    public void parse_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void parse_emptyOrWhitespace_returnsSummaryCommand() throws Exception {
        for (String input : new String[] {"", " ", "\t\r\n"}) {
            assertInstanceOf(SummaryCommand.class, parser.parse(input));
        }
    }

    @Test
    public void parse_arguments_throwsUsageMessage() {
        for (String input : new String[] {" 1", " t/engineering", " all ", " /summary", "\nextra"}) {
            assertThrows(ParseException.class,
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, SummaryCommand.MESSAGE_USAGE), () ->
                    parser.parse(input));
        }
    }
}
