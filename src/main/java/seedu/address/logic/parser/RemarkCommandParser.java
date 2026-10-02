package seedu.address.logic.parser;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REMARK;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.RemarkCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Remark;
public class RemarkCommandParser implements Parser<RemarkCommand> {
    @Override public RemarkCommand parse(String args) throws ParseException {
        ArgumentMultimap map = ArgumentTokenizer.tokenize(args, PREFIX_REMARK);
        try { return new RemarkCommand(ParserUtil.parseIndex(map.getPreamble()), new Remark(map.getValue(PREFIX_REMARK).orElse(""))); }
        catch (ParseException e) { throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemarkCommand.MESSAGE_USAGE), e); }
    }
}
