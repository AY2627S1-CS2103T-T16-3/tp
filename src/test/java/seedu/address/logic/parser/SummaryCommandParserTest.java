package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.getErrorMessageForDuplicatePrefixes;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EXCLUDE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INCLUDE;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
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
    public void parse_scopes_returnsMatchingCommand() throws Exception {
        assertEquals(new SummaryCommand(SummaryCommand.Scope.FILTERED), parser.parse(""));
        assertEquals(new SummaryCommand(SummaryCommand.Scope.ALL), parser.parse(" scope/all "));
        assertEquals(new SummaryCommand(SummaryCommand.Scope.FILTERED), parser.parse("\tscope/filtered\t"));
    }

    @Test
    public void parse_indexSelections_returnsMatchingCommand() throws Exception {
        Set<Index> firstAndThird = Set.of(Index.fromOneBased(1), Index.fromOneBased(3));
        Set<Index> second = Set.of(Index.fromOneBased(2));
        assertEquals(new SummaryCommand(SummaryCommand.Scope.FILTERED, firstAndThird, Set.of()),
                parser.parse("i/3 1 3"));
        assertEquals(new SummaryCommand(SummaryCommand.Scope.FILTERED, Set.of(), second), parser.parse("e/2"));
        assertEquals(new SummaryCommand(SummaryCommand.Scope.ALL, firstAndThird, second),
                parser.parse(" scope/all e/2 i/1 3 "));
        assertEquals(new SummaryCommand(SummaryCommand.Scope.FILTERED, firstAndThird, second),
                parser.parse("\tscope/filtered\ti/1\n3\te/2\t"));
        assertEquals(new SummaryCommand(SummaryCommand.Scope.FILTERED, second, second),
                parser.parse("i/2 e/2"));
    }

    @Test
    public void parse_invalidIndexSelections_throwsUsageMessage() {
        for (String input : new String[] {"i/", "e/", "i/0", "e/-1", "i/1 two", "e/1.5", "i/1,2",
            "i/2147483648", "e/1 2147483648", "i/1 extra", "i/1 t/team", "i/1 scope/all", "e/ i/1"}) {
            assertThrows(ParseException.class,
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, SummaryCommand.MESSAGE_USAGE), () ->
                    parser.parse(input));
        }
    }

    @Test
    public void parse_duplicatePrefixes_throwsDuplicatePrefixMessage() {
        assertThrows(ParseException.class, getErrorMessageForDuplicatePrefixes(PREFIX_INCLUDE), () ->
                parser.parse("i/1 i/2"));
        assertThrows(ParseException.class, getErrorMessageForDuplicatePrefixes(PREFIX_EXCLUDE), () ->
                parser.parse("e/1 e/2"));
    }

    @Test
    public void parse_invalidScopes_throwsUsageMessage() {
        for (String input : new String[] {"scope/", "scope/ALL", "scope/unknown", "scope/ filtered",
            "scope/all scope/all", "scope/filtered scope/all", "scope/all t/team", "scope/filtered extra",
            "extra scope/all", "scope/filtered\nextra", "s/filtered"}) {
            assertThrows(ParseException.class,
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, SummaryCommand.MESSAGE_USAGE), () ->
                    parser.parse(input));
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
