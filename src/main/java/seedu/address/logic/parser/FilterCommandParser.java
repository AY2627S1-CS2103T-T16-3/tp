package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LEAVE_ENTITLEMENT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SALARY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TASK;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.logic.commands.FilterCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.employee.EmployeeMatchesCriteriaPredicate;
import seedu.address.model.employee.EmployeeMatchesCriteriaPredicate.Field;

/**
 * Parses field criteria into a {@code FilterCommand}.
 */
public class FilterCommandParser implements Parser<FilterCommand> {

    private static final Map<Prefix, Field> FIELDS = Map.of(
            PREFIX_NAME, Field.NAME, PREFIX_PHONE, Field.PHONE, PREFIX_EMAIL, Field.EMAIL,
            PREFIX_ADDRESS, Field.ADDRESS, PREFIX_TAG, Field.TAG,
            PREFIX_TASK, Field.TASK, PREFIX_SALARY, Field.SALARY,
            PREFIX_LEAVE_ENTITLEMENT, Field.LEAVE_ENTITLEMENT);
    private static final Pattern PREFIX_PATTERN = Pattern.compile("(?<!\\S)[A-Za-z]+/");

    @Override
    public FilterCommand parse(String args) throws ParseException {
        // The shared tokenizer recognizes prefixes preceded by a space.
        String normalizedArgs = " " + args.replaceAll("\\s", " ");
        Matcher matcher = PREFIX_PATTERN.matcher(normalizedArgs);
        while (matcher.find()) {
            if (!FIELDS.containsKey(new Prefix(matcher.group()))) {
                throw invalidFormat();
            }
        }

        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(normalizedArgs,
                PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS, PREFIX_TAG, PREFIX_TASK, PREFIX_SALARY,
                PREFIX_LEAVE_ENTITLEMENT);
        if (!arguments.getPreamble().isEmpty()) {
            throw invalidFormat();
        }
        arguments.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS, PREFIX_SALARY,
                PREFIX_LEAVE_ENTITLEMENT);

        Map<Field, List<String>> criteria = new EnumMap<>(Field.class);
        for (Map.Entry<Prefix, Field> entry : FIELDS.entrySet()) {
            List<String> values = arguments.getAllValues(entry.getKey());
            if (values.stream().anyMatch(String::isBlank)) {
                throw invalidFormat();
            }
            if (!values.isEmpty()) {
                if (entry.getValue() == Field.SALARY) {
                    values = List.of(parseSalaryCriterion(values.get(0)));
                } else if (entry.getValue() == Field.LEAVE_ENTITLEMENT) {
                    values = List.of(parseLeaveCriterion(values.get(0)));
                } else if (values.stream().anyMatch(this::startsWithComparator)) {
                    throw new ParseException(FilterCommand.MESSAGE_TEXT_COMPARATOR);
                } else {
                    for (String value : values) {
                        validateTextCriterion(value, entry.getValue());
                    }
                }
                criteria.put(entry.getValue(), values);
            }
        }
        if (criteria.isEmpty()) {
            throw invalidFormat();
        }
        return new FilterCommand(new EmployeeMatchesCriteriaPredicate(criteria));
    }

    /**
     * Validates an exact salary or strict comparison without allowing leading zeros.
     */
    private String parseSalaryCriterion(String value) throws ParseException {
        int operatorLength = value.startsWith("<") || value.startsWith(">") ? 1 : 0;
        String amount = value.substring(operatorLength).trim();
        checkNoLeadingZeros(amount);
        return value.substring(0, operatorLength) + ParserUtil.parseSalary(amount).salary;
    }

    /**
     * Validates an exact annual entitlement or strict comparison without allowing leading zeros.
     */
    private String parseLeaveCriterion(String value) throws ParseException {
        int operatorLength = value.startsWith("<") || value.startsWith(">") ? 1 : 0;
        String amount = value.substring(operatorLength).trim();
        checkNoLeadingZeros(amount);
        return value.substring(0, operatorLength) + ParserUtil.parseLeaveEntitlement(amount).value;
    }

    /**
     * Applies the same field constraints used when adding or editing employee details.
     */
    private void validateTextCriterion(String value, Field field) throws ParseException {
        switch (field) {
            case NAME -> ParserUtil.parseName(value);
            case PHONE -> ParserUtil.parsePhone(value);
            case EMAIL -> ParserUtil.parseEmail(value);
            case ADDRESS -> ParserUtil.parseAddress(value);
            case TAG -> ParserUtil.parseTag(value);
            case TASK -> ParserUtil.parseTask(value);
            default -> throw new IllegalArgumentException("Field is not a text field: " + field);
        }
    }

    private boolean startsWithComparator(String value) {
        return value.startsWith("<") || value.startsWith(">") || value.startsWith("=") || value.startsWith("!=");
    }

    private void checkNoLeadingZeros(String amount) throws ParseException {
        if (amount.matches("0[0-9]+")) {
            throw new ParseException(FilterCommand.MESSAGE_LEADING_ZEROS);
        }
    }

    private ParseException invalidFormat() {
        return new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterCommand.MESSAGE_USAGE));
    }
}
