package seedu.address.logic.commands;
import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;
import java.util.List;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.person.Remark;
public class RemarkCommand extends Command {
    public static final String COMMAND_WORD = "remark";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits a person's remark. Parameters: INDEX r/REMARK";
    public static final String MESSAGE_ADD_REMARK_SUCCESS = "Added remark to Person: %1$s";
    public static final String MESSAGE_DELETE_REMARK_SUCCESS = "Removed remark from Person: %1$s";
    private final Index index; private final Remark remark;
    public RemarkCommand(Index index, Remark remark) { this.index = requireNonNull(index); this.remark = requireNonNull(remark); }
    @Override public CommandResult execute(Model model) throws CommandException {
        List<Person> persons = model.getFilteredPersonList();
        if (index.getZeroBased() >= persons.size()) throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        Person oldPerson = persons.get(index.getZeroBased());
        Person editedPerson = new Person(oldPerson.getName(), oldPerson.getPhone(), oldPerson.getEmail(), oldPerson.getAddress(), remark, oldPerson.getTags());
        model.setPerson(oldPerson, editedPerson); model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        String message = remark.value.isEmpty() ? MESSAGE_DELETE_REMARK_SUCCESS : MESSAGE_ADD_REMARK_SUCCESS;
        return new CommandResult(String.format(message, Messages.format(editedPerson)));
    }
    @Override public boolean equals(Object other) { return other instanceof RemarkCommand c && index.equals(c.index) && remark.equals(c.remark); }
}
