package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

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
            PREFIX_ADDRESS, Field.ADDRESS, PREFIX_TAG, Field.TAG);
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
                PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS, PREFIX_TAG);
        if (!arguments.getPreamble().isEmpty()) {
            throw invalidFormat();
        }
        arguments.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS);

        Map<Field, List<String>> criteria = new EnumMap<>(Field.class);
        for (Map.Entry<Prefix, Field> entry : FIELDS.entrySet()) {
            List<String> values = arguments.getAllValues(entry.getKey());
            if (values.stream().anyMatch(String::isBlank)) {
                throw invalidFormat();
            }
            if (!values.isEmpty()) {
                criteria.put(entry.getValue(), values);
            }
        }
        if (criteria.isEmpty()) {
            throw invalidFormat();
        }
        return new FilterCommand(new EmployeeMatchesCriteriaPredicate(criteria));
    }

    private ParseException invalidFormat() {
        return new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterCommand.MESSAGE_USAGE));
    }
}
