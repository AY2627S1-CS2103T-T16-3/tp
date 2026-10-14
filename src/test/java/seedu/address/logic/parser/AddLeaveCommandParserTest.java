package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_END_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_START_DATE;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_EMPLOYEE;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.AddLeaveCommand;
import seedu.address.model.leave.LeavePeriod;

public class AddLeaveCommandParserTest {

    private final AddLeaveCommandParser parser = new AddLeaveCommandParser();

    @Test
    public void parse_allFieldsPresent_success() {
        AddLeaveCommand expectedCommand = new AddLeaveCommand(INDEX_FIRST_EMPLOYEE, new LeavePeriod(
                LocalDate.of(2026, 10, 5), LocalDate.of(2026, 10, 7)));

        assertParseSuccess(parser, "1 from/05-10-2026 to/07-10-2026", expectedCommand);
    }

    @Test
    public void parse_missingField_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddLeaveCommand.MESSAGE_USAGE);

        assertParseFailure(parser, "from/05-10-2026 to/07-10-2026", expectedMessage);
        assertParseFailure(parser, "1 to/07-10-2026", expectedMessage);
        assertParseFailure(parser, "1 from/05-10-2026", expectedMessage);
    }

    @Test
    public void parse_duplicateDatePrefix_failure() {
        assertParseFailure(parser, "1 from/05-10-2026 from/06-10-2026 to/07-10-2026",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_START_DATE));
        assertParseFailure(parser, "1 from/05-10-2026 to/07-10-2026 to/08-10-2026",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_END_DATE));
    }

    @Test
    public void parse_invalidIndex_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddLeaveCommand.MESSAGE_USAGE);

        assertParseFailure(parser, "0 from/05-10-2026 to/07-10-2026", expectedMessage);
        assertParseFailure(parser, "first from/05-10-2026 to/07-10-2026", expectedMessage);
    }

    @Test
    public void parse_invalidDate_failure() {
        assertParseFailure(parser, "1 from/31-02-2026 to/07-10-2026", ParserUtil.MESSAGE_INVALID_START_DATE);
        assertParseFailure(parser, "1 from/05-10-2026 to/31-02-2026", ParserUtil.MESSAGE_INVALID_END_DATE);
    }

    @Test
    public void parse_invalidPeriod_failure() {
        assertParseFailure(parser, "1 from/07-10-2026 to/05-10-2026", LeavePeriod.MESSAGE_START_AFTER_END);
        assertParseFailure(parser, "1 from/31-12-2026 to/01-01-2027", LeavePeriod.MESSAGE_DIFFERENT_YEARS);
        assertParseFailure(parser, "1 from/10-10-2026 to/11-10-2026", LeavePeriod.MESSAGE_NO_WORKING_DAY);
    }
}
