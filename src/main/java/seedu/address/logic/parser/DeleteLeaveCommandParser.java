package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.logic.commands.DeleteLeaveCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.leave.LeaveId;

/**
 * Parses arguments for the delete-leave command.
 */
public class DeleteLeaveCommandParser implements Parser<DeleteLeaveCommand> {

    @Override
    public DeleteLeaveCommand parse(String args) throws ParseException {
        try {
            LeaveId leaveId = ParserUtil.parseLeaveId(args);
            return new DeleteLeaveCommand(leaveId);
        } catch (ParseException e) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteLeaveCommand.MESSAGE_USAGE), e);
        }
    }
}
