package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EXCLUDE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INCLUDE;

import java.util.HashSet;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.SummaryCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses a summary command with optional scope, inclusion, and exclusion parameters.
 */
public class SummaryCommandParser implements Parser<SummaryCommand> {
    @Override
    public SummaryCommand parse(String args) throws ParseException {
        requireNonNull(args);
        // The shared tokenizer recognizes prefixes preceded by a space.
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(" " + args.replaceAll("\\s", " "),
                PREFIX_INCLUDE, PREFIX_EXCLUDE);
        arguments.verifyNoDuplicatePrefixesFor(PREFIX_INCLUDE, PREFIX_EXCLUDE);
        SummaryCommand.Scope scope = switch (arguments.getPreamble()) {
            case "scope/all" -> SummaryCommand.Scope.ALL;
            case "", "scope/filtered" -> SummaryCommand.Scope.FILTERED;
            default -> throw invalidFormat();
        };
        return new SummaryCommand(scope, parseIndexes(arguments, PREFIX_INCLUDE),
                parseIndexes(arguments, PREFIX_EXCLUDE));
    }

    /** Parses a space-separated index list, treating repeated indexes as a single selection. */
    private Set<Index> parseIndexes(ArgumentMultimap arguments, Prefix prefix) throws ParseException {
        if (arguments.getValue(prefix).isEmpty()) {
            return Set.of();
        }
        Set<Index> indexes = new HashSet<>();
        for (String value : arguments.getValue(prefix).get().split("\\s+")) {
            try {
                indexes.add(ParserUtil.parseIndex(value));
            } catch (ParseException exception) {
                throw invalidFormat();
            }
        }
        return indexes;
    }

    private ParseException invalidFormat() {
        return new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, SummaryCommand.MESSAGE_USAGE));
    }
}
