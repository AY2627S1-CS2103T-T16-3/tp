package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.FilterCommand;
import seedu.address.model.employee.EmployeeMatchesCriteriaPredicate;
import seedu.address.model.employee.EmployeeMatchesCriteriaPredicate.Field;

/**
 * Tests validation of filter criteria and conversion to a filter command.
 */
public class FilterCommandParserTest {

    private final FilterCommandParser parser = new FilterCommandParser();

    @Test
    public void parse_missingCriteriaOrPreamble_throwsParseException() {
        for (String input : List.of("", "   ", "Alice", "Alice n/Bob")) {
            assertInvalidFormat(input);
        }
    }

    @Test
    public void parse_emptyField_throwsParseException() {
        for (String input : List.of("n/", "p/ ", "e/", "a/", "t/", "n/Alice p/", "t/friends t/")) {
            assertInvalidFormat(input);
        }
    }

    @Test
    public void parse_unknownPrefix_throwsParseException() {
        assertInvalidFormat("x/Alice");
        assertInvalidFormat("n/Alice x/value");
        assertInvalidFormat("phone/91235321");
    }

    @Test
    public void parse_duplicateScalarFields_throwsParseException() {
        for (Prefix prefix : List.of(PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS)) {
            assertParseFailure(parser, " " + prefix.getPrefix() + "first " + prefix.getPrefix() + "second",
                    Messages.getErrorMessageForDuplicatePrefixes(prefix));
        }
    }

    @Test
    public void parse_allFields_returnsFilterCommand() {
        FilterCommand expected = new FilterCommand(new EmployeeMatchesCriteriaPredicate(Map.of(
                Field.NAME, List.of("Alice Pauline"), Field.PHONE, List.of("91235321"),
                Field.EMAIL, List.of("example.com"), Field.ADDRESS, List.of("Jurong West"),
                Field.TAG, List.of("friends", "colleagues"))));
        assertParseSuccess(parser,
                "n/Alice Pauline p/91235321 e/example.com a/Jurong West t/friends t/colleagues", expected);
    }

    @Test
    public void parse_partialValues_doesNotRequireCompletePersonFields() {
        assertParseSuccess(parser, "p/9", new FilterCommand(
                new EmployeeMatchesCriteriaPredicate(Map.of(Field.PHONE, List.of("9")))));
        assertParseSuccess(parser, "e/@example", new FilterCommand(
                new EmployeeMatchesCriteriaPredicate(Map.of(Field.EMAIL, List.of("@example")))));
    }

    @Test
    public void parse_whitespaceAndPrefixOrder_returnsSameCommand() throws Exception {
        assertEquals(parser.parse("n/Alice p/912"), parser.parse("\t p/912\n n/Alice   "));
    }

    private void assertInvalidFormat(String input) {
        assertParseFailure(parser, input, String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterCommand.MESSAGE_USAGE));
    }
}
