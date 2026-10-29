package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SALARY;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_EMPLOYEE;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.SalaryCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.employee.Salary;

public class SalaryCommandParserTest {

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, SalaryCommand.MESSAGE_USAGE);
    private static final String MESSAGE_UNSUPPORTED_FIELD =
            "Unsupported field. The salary command only accepts s/.";

    private final SalaryCommandParser parser = new SalaryCommandParser();

    @Test
    public void parse_leadingZeros_returnsNormalizedSalary() {
        assertParseSuccess(parser, "1 s/000000001", new SalaryCommand(INDEX_FIRST_EMPLOYEE, new Salary("1")));
        assertParseFailure(parser, "1 s/000050001", Salary.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void parse_validSalary_returnsSalaryCommand() {
        for (String salary : List.of("0", "1", "49999", "50000")) {
            assertParseSuccess(parser, "1 s/" + salary,
                    new SalaryCommand(INDEX_FIRST_EMPLOYEE, new Salary(salary)));
        }
    }

    @Test
    public void parse_whitespace_returnsSalaryCommand() {
        assertParseSuccess(parser, "  1   s/ 5000  ",
                new SalaryCommand(INDEX_FIRST_EMPLOYEE, new Salary("5000")));
    }

    @Test
    public void parse_invalidIndex_throwsParseException() {
        for (String index : List.of("", "0", "-1", "abc", "1.5", "2 2", "2147483648")) {
            assertParseFailure(parser, index + " s/5000", MESSAGE_INVALID_FORMAT);
        }
    }

    @Test
    public void parse_missingOrMalformedPrefix_throwsParseException() {
        // This parser receives the arguments after AddressBookParser removes the command word.
        for (String args : List.of("", "/r", "8333", "2 2", "/s /s /s /s 1",
                "/s 1 /s 2 /s 3", "/s2", "1 /s5000", "1 s5000")) {
            assertParseFailure(parser, args, MESSAGE_INVALID_FORMAT);
        }
    }

    @Test
    public void parse_invalidSalary_throwsParseException() {
        for (String salary : List.of("", " ", "-1", "-9999999", "50001", "1000000000",
                "2147483648", "abc", "12abc", "1.5", "$5000", "5000 extra", "5000n/Charlie")) {
            assertParseFailure(parser, "1 s/" + salary, Salary.MESSAGE_CONSTRAINTS);
        }
    }

    @Test
    public void parse_unsupportedFieldAfterSalary_throwsUnsupportedFieldMessage() {
        for (String field : List.of("n/Charlie", "p/91234567", "e/charlie@example.com",
                "a/123 Main Street", "t/friend", "le/14", "task/report", "n/")) {
            assertParseFailure(parser, "1 s/5000 " + field, MESSAGE_UNSUPPORTED_FIELD);
        }
    }

    @Test
    public void parse_unsupportedFieldBeforeSalary_throwsUnsupportedFieldMessage() {
        for (String args : List.of("1 n/Charlie s/5000", "n/Charlie 1 s/5000")) {
            assertParseFailure(parser, args, MESSAGE_UNSUPPORTED_FIELD);
        }
    }

    @Test
    public void parse_unknownField_throwsUnsupportedFieldMessage() {
        for (String args : List.of("1 s/5000 r/test", "1 s/5000 x/test", "1 x/test s/5000")) {
            assertParseFailure(parser, args, MESSAGE_UNSUPPORTED_FIELD);
        }
    }

    @Test
    public void parse_unsupportedFieldWithWhitespace_throwsUnsupportedFieldMessage() {
        for (String separator : List.of(" ", "  ", "\t", "\n", "\r\n")) {
            assertParseFailure(parser, "1 s/5000" + separator + "n/Charlie", MESSAGE_UNSUPPORTED_FIELD);
        }
    }

    @Test
    public void parse_uppercaseField_throwsUnsupportedFieldMessage() {
        for (String args : List.of("1 s/5000 N/Charlie", "1 S/5000")) {
            assertParseFailure(parser, args, MESSAGE_UNSUPPORTED_FIELD);
        }
    }

    @Test
    public void parse_otherErrorsWithUnsupportedField_throwsUnsupportedFieldMessage() {
        for (String args : List.of("0 s/5000 n/Charlie", "1 s/5000 s/4000 n/Charlie", "1 n/Charlie")) {
            assertParseFailure(parser, args, MESSAGE_UNSUPPORTED_FIELD);
        }
    }

    @Test
    public void parse_invalidSalaryWithUnsupportedField_throwsUnsupportedFieldMessage() {
        for (String salary : List.of("-1", "50001", "abc", "")) {
            assertParseFailure(parser, "1 s/" + salary + " n/Charlie", MESSAGE_UNSUPPORTED_FIELD);
        }
    }

    @Test
    public void parseCommand_unsupportedField_throwsUnsupportedFieldMessage() {
        AddressBookParser addressBookParser = new AddressBookParser();
        assertThrows(ParseException.class, MESSAGE_UNSUPPORTED_FIELD, () ->
                addressBookParser.parseCommand("salary 1 s/5000 n/Charlie"));
    }

    @Test
    public void parse_duplicateSalaryPrefix_throwsParseException() {
        for (String args : List.of("1 s/5000 s/6000", "1 s/5000 s/5000", "1 s/ s/5000",
                "1 s/ s/", "1 s/ s/ s/")) {
            assertParseFailure(parser, args, Messages.getErrorMessageForDuplicatePrefixes(PREFIX_SALARY));
        }
    }

    @Test
    public void parse_largeValidIndex_returnsSalaryCommand() {
        // List bounds are checked by SalaryCommand.execute, not the parser.
        assertParseSuccess(parser, "8333 s/5000", new SalaryCommand(Index.fromOneBased(8333), new Salary("5000")));
    }

    @Test
    public void parse_fullInvalidCommands_throwsParseException() {
        AddressBookParser addressBookParser = new AddressBookParser();
        for (String command : List.of("salary /r", "salary 8333", "salary 2 2",
                "salary /s /s /s /s 1", "salary /s 1 /s 2 /s 3", "salary /s2")) {
            assertThrows(ParseException.class, MESSAGE_INVALID_FORMAT, () -> addressBookParser.parseCommand(command));
        }
    }
}
