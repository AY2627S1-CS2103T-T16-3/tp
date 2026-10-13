package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.getErrorMessageForDuplicatePrefixes;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TASK_DETAILS;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.AddTaskCommand;
import seedu.address.model.employee.Task;

public class AddTaskCommandParserTest {

    private final AddTaskCommandParser parser = new AddTaskCommandParser();

    @Test
    public void parse_validArgs_returnsCommand() {
        assertParseSuccess(parser, "1 d/ Prepare  report ",
                new AddTaskCommand(Index.fromOneBased(1), new Task("Prepare  report")));
    }

    @Test
    public void parse_duplicateDetailsPrefix_throwsParseException() {
        assertParseFailure(parser, "1 d/Prepare d/report",
                getErrorMessageForDuplicatePrefixes(PREFIX_TASK_DETAILS));
    }

    @Test
    public void parse_missingDetails_throwsParseException() {
        assertParseFailure(parser, "1", "Invalid command format!\n"
                + AddTaskCommand.MESSAGE_USAGE);
    }

    @Test
    public void parse_invalidEmployeeIndex_throwsParseException() {
        assertParseFailure(parser, "zero d/Prepare report",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddTaskCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_taskTextWithTaskPrefix_keepsItAsText() {
        assertParseSuccess(parser, "1 d/Prepare t/report",
                new AddTaskCommand(Index.fromOneBased(1), new Task("Prepare t/report")));
    }
}
