package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LEAVE_ENTITLEMENT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SALARY;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.FilterCommand;
import seedu.address.model.employee.Email;
import seedu.address.model.employee.EmployeeMatchesCriteriaPredicate;
import seedu.address.model.employee.EmployeeMatchesCriteriaPredicate.Field;
import seedu.address.model.employee.LeaveEntitlement;
import seedu.address.model.employee.Name;
import seedu.address.model.employee.Phone;
import seedu.address.model.employee.Salary;
import seedu.address.model.employee.Task;
import seedu.address.model.tag.Tag;

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
        for (String input : List.of("n/", "p/ ", "e/", "a/", "t/", "n/Alice p/", "t/friends t/",
                "task/", "s/", "task/report s/", "task/report task/", "le/", "n/Alice le/")) {
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
        for (Prefix prefix : List.of(PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS, PREFIX_SALARY,
                PREFIX_LEAVE_ENTITLEMENT)) {
            assertParseFailure(parser, " " + prefix.getPrefix() + "first " + prefix.getPrefix() + "second",
                    Messages.getErrorMessageForDuplicatePrefixes(prefix));
        }
    }

    @Test
    public void parse_allFields_returnsFilterCommand() {
        FilterCommand expected = new FilterCommand(new EmployeeMatchesCriteriaPredicate(Map.of(
                Field.NAME, List.of("Alice Pauline"), Field.PHONE, List.of("91235321"),
                Field.EMAIL, List.of("alice@example.com"), Field.ADDRESS, List.of("Jurong West"),
                Field.TAG, List.of("friends", "colleagues"), Field.TASK, List.of("report", "review"),
                Field.SALARY, List.of("3000"), Field.LEAVE_ENTITLEMENT, List.of(">10"))));
        assertParseSuccess(parser,
                "n/Alice Pauline p/91235321 e/alice@example.com a/Jurong West t/friends t/colleagues "
                        + "task/report task/review s/3000 le/>10", expected);
    }

    @Test
    public void parse_validPartialValues_returnsFilterCommand() {
        assertParseSuccess(parser, "n/Ali p/912 a/Jurong", new FilterCommand(
                new EmployeeMatchesCriteriaPredicate(Map.of(Field.NAME, List.of("Ali"),
                        Field.PHONE, List.of("912"), Field.ADDRESS, List.of("Jurong")))));
    }

    @Test
    public void parse_invalidTextFields_throwsFieldConstraintMessage() {
        for (String value : List.of("Alice!", "John-Doe", "Alice@Example", "Alice > Bob", "A_B")) {
            assertParseFailure(parser, "n/" + value, Name.MESSAGE_CONSTRAINTS);
        }
        for (String value : List.of("9", "91", "abc", "912a", "912 345", "+6591234567", "912-345")) {
            assertParseFailure(parser, "p/" + value, Phone.MESSAGE_CONSTRAINTS);
        }
        for (String value : List.of("@example.com", "example.com", "alice@", "alice@@example.com",
                "alice!@example.com")) {
            assertParseFailure(parser, "e/" + value, Email.MESSAGE_CONSTRAINTS);
        }
        for (String value : List.of("friends!", "close friends", "co-workers", "a_b")) {
            assertParseFailure(parser, "t/" + value, Tag.MESSAGE_CONSTRAINTS);
        }
        assertParseFailure(parser, "task/" + "a".repeat(Task.MAX_DETAILS_LENGTH + 1), Task.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "t/friends t/invalid!", Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "task/report task/" + "a".repeat(Task.MAX_DETAILS_LENGTH + 1),
                Task.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_validFieldBoundaries_returnsFilterCommand() {
        assertParseSuccess(parser, "n/A1 p/000 t/Team1 task/" + "a".repeat(Task.MAX_DETAILS_LENGTH),
                new FilterCommand(new EmployeeMatchesCriteriaPredicate(Map.of(Field.NAME, List.of("A1"),
                        Field.PHONE, List.of("000"), Field.TAG, List.of("Team1"),
                        Field.TASK, List.of("a".repeat(Task.MAX_DETAILS_LENGTH))))));
    }

    @Test
    public void parse_whitespaceAndPrefixOrder_returnsSameCommand() throws Exception {
        assertEquals(parser.parse("n/Alice p/912"), parser.parse("\t p/912\n n/Alice   "));
    }

    @Test
    public void parse_salary_acceptsBounds() {
        for (String input : List.of("0", "1", "49999", "50000")) {
            assertParseSuccess(parser, "s/" + input, new FilterCommand(new EmployeeMatchesCriteriaPredicate(
                    Map.of(Field.SALARY, List.of(new Salary(input).salary)))));
        }
    }

    @Test
    public void parse_salaryComparators_acceptsBounds() {
        for (String comparator : List.of("<", "<=", ">", ">=")) {
            for (String amount : List.of("0", "1", "49999", "50000")) {
                assertParseSuccess(parser, "s/" + comparator + amount,
                        new FilterCommand(new EmployeeMatchesCriteriaPredicate(
                                Map.of(Field.SALARY, List.of(comparator + new Salary(amount).salary)))));
            }
        }
    }

    @Test
    public void parse_salaryOutsideBounds_throwsParseException() {
        for (String amount : List.of("-1", "50001")) {
            for (String operator : List.of("", "<", "<=", ">", ">=")) {
                assertParseFailure(parser, "s/" + operator + amount, Salary.MESSAGE_CONSTRAINTS);
            }
        }
    }

    @Test
    public void parse_invalidSalaryComparison_throwsParseException() {
        for (String input : List.of("<", ">", "<=", ">=", "<==4000", ">==4000", "=>4000", "=<4000",
                "<<4000", "><4000", "=4000", "<-1", ">50001", "<=-1", ">=50001", "<=3000.50", ">=abc",
                "<3000.50", ">abc", ">9999999999999999999999")) {
            assertParseFailure(parser, "s/" + input, Salary.MESSAGE_CONSTRAINTS);
        }
    }

    @Test
    public void parse_invalidSalary_throwsParseException() {
        for (String input : List.of("-1", "50001", "3000.50", "abc", "9999999999999999999999")) {
            assertParseFailure(parser, "s/" + input, Salary.MESSAGE_CONSTRAINTS);
        }
    }

    @Test
    public void parse_taskOnly_returnsFilterCommand() {
        assertParseSuccess(parser, "task/Quarterly report", new FilterCommand(new EmployeeMatchesCriteriaPredicate(
                Map.of(Field.TASK, List.of("Quarterly report")))));
    }

    @Test
    public void parse_taskWithEscapedPrefix_returnsCommandWithLiteralSlash() {
        assertParseSuccess(parser, "task/Coordinate w\\/Finance",
                new FilterCommand(new EmployeeMatchesCriteriaPredicate(
                        Map.of(Field.TASK, List.of("Coordinate w/Finance")))));
        assertParseSuccess(parser, "task/Discuss n\\/Alice and task\\/review",
                new FilterCommand(new EmployeeMatchesCriteriaPredicate(
                        Map.of(Field.TASK, List.of("Discuss n/Alice and task/review")))));
    }

    @Test
    public void parse_leaveEntitlement_acceptsExactAmountsAndComparators() {
        for (String operator : List.of("", "<", "<=", ">", ">=")) {
            for (String amount : List.of("0", "14", "365")) {
                assertParseSuccess(parser, "le/" + operator + amount,
                        new FilterCommand(new EmployeeMatchesCriteriaPredicate(
                                Map.of(Field.LEAVE_ENTITLEMENT, List.of(operator + amount)))));
            }
        }
    }

    @Test
    public void parse_numericLeadingZeros_throwsSpecificParseException() {
        for (String prefix : List.of("s/", "le/")) {
            for (String operator : List.of("", "<", "<=", ">", ">=")) {
                for (String amount : List.of("00", "000", "014", "003000", "050001", "000000000000000000001")) {
                    assertParseFailure(parser, prefix + operator + amount, FilterCommand.MESSAGE_LEADING_ZEROS);
                    assertParseFailure(parser, prefix + operator + " " + amount,
                            FilterCommand.MESSAGE_LEADING_ZEROS);
                }
            }
        }
    }

    @Test
    public void parse_phoneLeadingZeros_remainsPartialMatch() {
        assertParseSuccess(parser, "p/014", new FilterCommand(new EmployeeMatchesCriteriaPredicate(
                Map.of(Field.PHONE, List.of("014")))));
    }

    @Test
    public void parse_invalidLeaveEntitlement_throwsParseException() {
        for (String amount : List.of("-1", "366", "14.5", "abc", "999999999999999999999")) {
            for (String operator : List.of("", "<", "<=", ">", ">=")) {
                assertParseFailure(parser, "le/" + operator + amount, LeaveEntitlement.MESSAGE_CONSTRAINTS);
            }
        }
        for (String input : List.of("<", ">", "<=", ">=", "<==14", ">==14", "=>14", "=<14", "<<14", "><14", "=14")) {
            assertParseFailure(parser, "le/" + input, LeaveEntitlement.MESSAGE_CONSTRAINTS);
        }
    }

    @Test
    public void parse_leaveEntitlementPrefixOrder_returnsSameCommand() throws Exception {
        assertEquals(parser.parse("n/Alice s/<4000 le/>10"), parser.parse("le/>10 s/<4000 n/Alice"));
        assertParseFailure(parser, "le/>10 le/<20",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_LEAVE_ENTITLEMENT));
    }

    @Test
    public void parse_textComparators_throwsSpecificParseException() {
        for (String prefix : List.of("n/", "p/", "e/", "a/", "t/", "task/")) {
            for (String operator : List.of("<", ">", "<=", ">=", "=", "==", "!=", "=>", "=<")) {
                for (String value : List.of("Alice", " Alice", "")) {
                    assertParseFailure(parser, prefix + operator + value, FilterCommand.MESSAGE_TEXT_COMPARATOR);
                }
            }
        }
        assertParseFailure(parser, "t/friends t/>colleagues", FilterCommand.MESSAGE_TEXT_COMPARATOR);
        assertParseFailure(parser, "task/report task/<=review", FilterCommand.MESSAGE_TEXT_COMPARATOR);
        assertParseFailure(parser, "s/3000 n/>Alice", FilterCommand.MESSAGE_TEXT_COMPARATOR);
    }

    @Test
    public void parse_comparatorWithinText_remainsLiteralText() {
        assertParseSuccess(parser, "task/Check salary > 3000", new FilterCommand(
                new EmployeeMatchesCriteriaPredicate(Map.of(Field.TASK, List.of("Check salary > 3000")))));
    }

    @Test
    public void parse_numericRanges_acceptsWhitespaceAndEqualEndpoints() throws Exception {
        for (String prefix : List.of("s/", "le/")) {
            assertEquals(parser.parse(prefix + "14-21"), parser.parse(prefix + "14 - 21"));
            assertEquals(parser.parse(prefix + "14 - 14"), parser.parse(prefix + "14-14"));
            for (String operator : List.of("<", "<=", ">", ">=")) {
                assertEquals(parser.parse(prefix + operator + "14"),
                        parser.parse(prefix + operator + " 14"));
            }
        }
    }

    @Test
    public void parse_invalidNumericRanges_throwsParseException() {
        for (String prefix : List.of("s/", "le/")) {
            for (String value : List.of("21-14")) {
                assertParseFailure(parser, prefix + value,
                        "Range minimum must not exceed its maximum.\n" + FilterCommand.MESSAGE_USAGE);
            }
        }
        for (String value : List.of("0-50001", "0..50001", "1-", "1..", "..2", "1-2-3", "1.5-2", "-1-2")) {
            assertParseFailure(parser, "s/" + value, Salary.MESSAGE_CONSTRAINTS);
        }
        assertParseFailure(parser, "le/0-366", LeaveEntitlement.MESSAGE_CONSTRAINTS);

    }

    @Test
    public void parse_dotRanges_throwsParseException() {
        for (String range : List.of("14..21", "14 .. 21", "14..14", "21..14")) {
            assertParseFailure(parser, "s/" + range, Salary.MESSAGE_CONSTRAINTS);
            assertParseFailure(parser, "le/" + range, LeaveEntitlement.MESSAGE_CONSTRAINTS);
        }
    }

    @Test
    public void parse_numericRangesWithLeadingZeros_throwsSpecificParseException() {
        for (String prefix : List.of("s/", "le/")) {
            for (String range : List.of("014-21", "14-021", "00-0", "0-00", "0014 - 0021")) {
                assertParseFailure(parser, prefix + range, FilterCommand.MESSAGE_LEADING_ZEROS);
            }
        }
    }

    private void assertInvalidFormat(String input) {
        assertParseFailure(parser, input, String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterCommand.MESSAGE_USAGE));
    }
}
