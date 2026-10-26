package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddTaskCommand;
import seedu.address.logic.commands.DeleteTaskCommand;
import seedu.address.logic.commands.ListTasksCommand;
import seedu.address.logic.commands.MarkTaskCommand;
import seedu.address.logic.commands.UnmarkTaskCommand;
import seedu.address.logic.parser.exceptions.ParseException;

public class TaskCommandParserTest {

    private final TaskCommandParser parser = new TaskCommandParser();

    @Test
    public void messageUsage_containsAllTaskCommands() {
        assertEquals("Available task commands:\n\n"
                + AddTaskCommand.MESSAGE_USAGE + "\n\n"
                + DeleteTaskCommand.MESSAGE_USAGE + "\n\n"
                + ListTasksCommand.MESSAGE_USAGE + "\n\n"
                + MarkTaskCommand.MESSAGE_USAGE + "\n\n"
                + UnmarkTaskCommand.MESSAGE_USAGE, TaskCommandParser.MESSAGE_USAGE);
    }

    @Test
    public void parse_missingSubcommand_throwsUsageMessage() {
        assertThrows(ParseException.class,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, TaskCommandParser.MESSAGE_USAGE), () ->
                    parser.parse(""));
    }

    @Test
    public void parse_unknownSubcommand_throwsUsageMessage() {
        assertThrows(ParseException.class,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, TaskCommandParser.MESSAGE_USAGE), () ->
                    parser.parse("remove 1"));
    }

    @Test
    public void parse_validSubcommands_returnsExpectedCommands() throws Exception {
        assertInstanceOf(AddTaskCommand.class, parser.parse("add 1 d/Prepare report"));
        assertInstanceOf(DeleteTaskCommand.class, parser.parse("delete 1 i/1"));
        assertInstanceOf(ListTasksCommand.class, parser.parse("list 1"));
        assertInstanceOf(MarkTaskCommand.class, parser.parse("mark 1 i/1"));
        assertInstanceOf(UnmarkTaskCommand.class, parser.parse("unmark 1 i/1"));
    }
}
