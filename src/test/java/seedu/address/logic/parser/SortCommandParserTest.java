package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.SortCommand;
import seedu.address.logic.commands.SortCommand.Field;

public class SortCommandParserTest {
    private final SortCommandParser parser = new SortCommandParser();

    @Test
    public void parse_singleFieldAndOrder_success() {
        Map<String, Field> fields = Map.of("n", Field.NAME, "p", Field.PHONE, "e", Field.EMAIL,
                "a", Field.ADDRESS, "s", Field.SALARY, "le", Field.LEAVE_ENTITLEMENT);
        fields.forEach((prefix, field) -> {
            assertParseSuccess(parser, prefix + "/asc", new SortCommand(field, true));
            assertParseSuccess(parser, prefix + "/des", new SortCommand(field, false));
        });
        assertParseSuccess(parser, "  s/ asc  ", new SortCommand(Field.SALARY, true));
    }

    @Test
    public void parse_missingInvalidOrMultipleFields_failure() {
        for (String input : List.of("", " ", "s/", "asc", "s/up", "s/desc", "s/ASC", "s/asc extra",
                "s/asc p/des", "s/asc s/des", "extra s/asc", "x/asc", "t/asc", "task/des")) {
            assertParseFailure(parser, input,
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, SortCommand.MESSAGE_USAGE));
        }
    }
}
