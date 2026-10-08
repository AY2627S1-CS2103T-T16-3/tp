package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_EMPLOYEES_LISTED_OVERVIEW;
import static seedu.address.testutil.TypicalEmployees.ALICE;
import static seedu.address.testutil.TypicalEmployees.BENSON;
import static seedu.address.testutil.TypicalEmployees.DANIEL;
import static seedu.address.testutil.TypicalEmployees.getTypicalAddressBook;
import static seedu.address.testutil.TypicalEmployees.getTypicalEmployees;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.AddressBookParser;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.employee.Employee;

/**
 * Tests filtering from command input through the model's displayed list.
 */
public class FilterCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private final AddressBookParser parser = new AddressBookParser();

    @Test
    public void execute_eachField_matchesEmployees() throws Exception {
        assertFilter("n/ALICE", List.of(ALICE));
        assertFilter("p/94351253", List.of(ALICE));
        assertFilter("p/987", List.of(BENSON));
        assertFilter("e/ALICE@", List.of(ALICE));
        assertFilter("a/JURONG WEST", List.of(ALICE));
        assertFilter("t/FRIENDS", List.of(ALICE, BENSON, DANIEL));
        assertFilter("t/friend", List.of());
    }

    @Test
    public void execute_multipleFields_requiresEveryCriterion() throws Exception {
        assertFilter("n/Meier t/friends", List.of(BENSON, DANIEL));
        assertFilter("n/Meier p/987", List.of(BENSON));
        assertFilter("n/Alice p/987", List.of());
        assertFilter("n/Alice p/943 e/example.com a/Jurong t/friends", List.of(ALICE));
    }

    @Test
    public void execute_multipleTags_requiresEveryTag() throws Exception {
        assertFilter("t/friends t/owesMoney", List.of(BENSON));
        assertFilter("t/friends t/missing", List.of());
    }

    @Test
    public void execute_newFilter_searchesEntireAddressBook() throws Exception {
        assertFilter("n/Alice", List.of(ALICE));
        assertFilter("n/Benson", List.of(BENSON));
        assertFilter("p/00000000", List.of());
        assertFilter("n/Alice", List.of(ALICE));
        assertEquals(getTypicalAddressBook(), model.getAddressBook());
    }

    @Test
    public void execute_list_restoresAllEmployees() throws Exception {
        assertFilter("n/Alice", List.of(ALICE));
        new ListCommand().execute(model);
        assertEquals(getTypicalEmployees(), model.getFilteredEmployeeList());
    }

    @Test
    public void equals_comparesCriteria() throws Exception {
        Command command = parser.parseCommand("filter n/Alice p/943");
        assertTrue(command.equals(command));
        assertEquals(command, parser.parseCommand("filter p/943 n/Alice"));
        assertFalse(command.equals(parser.parseCommand("filter n/Benson")));
        assertFalse(command.equals(new ListCommand()));
        assertFalse(command.equals(null));
    }

    private void assertFilter(String arguments, List<Employee> expectedEmployees) throws Exception {
        Command command = parser.parseCommand("filter " + arguments);
        assertTrue(command instanceof FilterCommand);
        CommandResult result = command.execute(model);
        assertEquals(String.format(MESSAGE_EMPLOYEES_LISTED_OVERVIEW, expectedEmployees.size()),
                result.getFeedbackToUser());
        assertEquals(expectedEmployees, model.getFilteredEmployeeList());
    }
}
