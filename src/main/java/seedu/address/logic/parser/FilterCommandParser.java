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

    private static final Pattern RANGE_PATTERN = Pattern.compile("(\\d+)\\s*-\\s*(\\d+)");

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
                if (entry.getValue() == Field.SALARY || entry.getValue() == Field.LEAVE_ENTITLEMENT) {
                    values = List.of(parseNumericCriterion(values.get(0), entry.getValue()));
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
     * Normalizes a comparison or inclusive range using the constraints of its numeric field.
     */
    private String parseNumericCriterion(String value, Field field) throws ParseException {
        Matcher range = RANGE_PATTERN.matcher(value);
        if (range.matches()) {
            String lower = parseAmount(range.group(1), field);
            String upper = parseAmount(range.group(2), field);
            if (Integer.parseInt(lower) > Integer.parseInt(upper)) {
                throw new ParseException("Range minimum must not exceed its maximum.\n" + FilterCommand.MESSAGE_USAGE);
            }
            return lower + "-" + upper;
        }
        if (value.startsWith("<") || value.startsWith(">")) {
            int operatorLength = value.startsWith("<=") || value.startsWith(">=") ? 2 : 1;
            return value.substring(0, operatorLength) + parseAmount(value.substring(operatorLength).trim(), field);
        }
        return parseAmount(value, field);
    }

    private String parseAmount(String value, Field field) throws ParseException {
        return switch (field) {
            case SALARY -> ParserUtil.parseSalary(value).salary;
            case LEAVE_ENTITLEMENT -> Integer.toString(ParserUtil.parseLeaveEntitlement(value).value);
            default -> throw new IllegalArgumentException("Field is not numeric: " + field);
        };
    }

    private ParseException invalidFormat() {
        return new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterCommand.MESSAGE_USAGE));
    }
}
