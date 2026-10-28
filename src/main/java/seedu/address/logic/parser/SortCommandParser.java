package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.logic.commands.SortCommand;
import seedu.address.logic.commands.SortCommand.Field;
import seedu.address.logic.parser.exceptions.ParseException;

/** Parses a single sort field and its ascending or descending order. */
public class SortCommandParser implements Parser<SortCommand> {
    private static final Map<String, Field> FIELDS = Map.of(
            "n", Field.NAME, "p", Field.PHONE, "e", Field.EMAIL, "a", Field.ADDRESS,
            "s", Field.SALARY, "le", Field.LEAVE_ENTITLEMENT);
    private static final Pattern SORT_FORMAT =
            Pattern.compile("(?<field>n|p|e|a|s|le)/\\s*(?<order>asc|des)");

    @Override
    public SortCommand parse(String args) throws ParseException {
        Matcher matcher = SORT_FORMAT.matcher(args.trim());
        if (!matcher.matches()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, SortCommand.MESSAGE_USAGE));
        }
        return new SortCommand(FIELDS.get(matcher.group("field")), matcher.group("order").equals("asc"));
    }
}
