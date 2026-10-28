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
import seedu.address.model.employee.EmployeeMatchesCriteriaPredicate;
import seedu.address.model.employee.EmployeeMatchesCriteriaPredicate.Field;
import seedu.address.model.employee.LeaveEntitlement;
import seedu.address.model.employee.Salary;

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
                Field.EMAIL, List.of("example.com"), Field.ADDRESS, List.of("Jurong West"),
                Field.TAG, List.of("friends", "colleagues"), Field.TASK, List.of("report", "review"),
                Field.SALARY, List.of("3000"), Field.LEAVE_ENTITLEMENT, List.of(">10"))));
        assertParseSuccess(parser,
                "n/Alice Pauline p/91235321 e/example.com a/Jurong West t/friends t/colleagues "
                        + "task/report task/review s/3000 le/>10", expected);
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

    @Test
    public void parse_salary_normalizesLeadingZerosAndAcceptsBounds() {
        for (String input : List.of("0", "1", "49999", "50000", "003000")) {
            assertParseSuccess(parser, "s/" + input, new FilterCommand(new EmployeeMatchesCriteriaPredicate(
                    Map.of(Field.SALARY, List.of(new Salary(input).salary)))));
        }
    }

    @Test
    public void parse_salaryComparators_normalizesAmountAndAcceptsBounds() {
        for (String comparator : List.of("<", "<=", ">", ">=")) {
            for (String amount : List.of("0", "1", "49999", "50000", "004000")) {
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
    public void parse_leaveEntitlementLeadingZeros_returnsNormalizedCommand() throws Exception {
        for (String operator : List.of("", "<", "<=", ">", ">=")) {
            assertEquals(parser.parse("le/" + operator + "14"), parser.parse("le/" + operator + "014"));
        }
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

    private void assertInvalidFormat(String input) {
        assertParseFailure(parser, input, String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterCommand.MESSAGE_USAGE));
    }
}
