package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.employee.Employee;
import seedu.address.testutil.EmployeeBuilder;

public class SummaryCommandTest {
    private static final String EMPTY_SUMMARY = "Employee summary\nTotal employees: 0\n"
            + "Employees without tags: 0\n\nNo tags to summarize.";

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new SummaryCommand().execute(null));
    }

    @Test
    public void execute_emptyBook_returnsZeroCounts() {
        assertEquals(new CommandResult(EMPTY_SUMMARY), new SummaryCommand().execute(new ModelManager()));
    }

    @Test
    public void execute_noTags_returnsHeadcountAndNoTagsMessage() {
        Model model = new ModelManager();
        model.addEmployee(employee("Alice"));
        assertEquals(new CommandResult("Employee summary\nTotal employees: 1\n"
                + "Employees without tags: 1\n\nNo tags to summarize."), new SummaryCommand().execute(model));
    }

    @Test
    public void execute_mixedTags_formatsSortedCountsWithoutChangingModel() {
        Model model = new ModelManager();
        model.addEmployee(employee("Alice", "Remote", "Engineering", "engineering"));
        model.addEmployee(employee("Bob", "engineering", "intern"));
        model.addEmployee(employee("Carol"));
        Model expectedModel = new ModelManager(model.getAddressBook(), model.getUserPrefs());
        String expected = "Employee summary\nTotal employees: 3\nEmployees without tags: 1\n\n"
                + "Employees by tag:\n  engineering: 2\n  intern: 1\n  remote: 1\n\n"
                + "Employees may have multiple tags; tag counts can overlap.";
        assertEquals(new CommandResult(expected), new SummaryCommand().execute(model));
        assertEquals(expectedModel, model);
    }

    @Test
    public void execute_filteredView_summarizesWholeBookAndPreservesPredicate() {
        Model model = new ModelManager();
        Employee alice = employee("Alice", "engineering");
        Employee bob = employee("Bob", "remote");
        model.addEmployee(alice);
        model.addEmployee(bob);
        Predicate<Employee> filter = employee -> employee.getName().fullName.startsWith("A");
        model.updateFilteredEmployeeList(filter);
        AddressBook before = new AddressBook(model.getAddressBook());

        CommandResult filteredResult = new SummaryCommand().execute(model);
        Model unfiltered = new ModelManager(before, model.getUserPrefs());
        assertEquals(new SummaryCommand().execute(unfiltered), filteredResult);
        assertEquals(before, model.getAddressBook());
        assertEquals(List.of(alice), model.getFilteredEmployeeList());

        // Verify the predicate remains active, not just the previously displayed contents.
        Employee renamedBob = new EmployeeBuilder(bob).withName("Andrew").build();
        model.setEmployee(bob, renamedBob);
        assertEquals(List.of(alice, renamedBob), model.getFilteredEmployeeList());
    }

    @Test
    public void execute_filterMatchesNothing_stillCountsStoredEmployees() {
        Model model = new ModelManager();
        model.addEmployee(employee("Alice"));
        CommandResult expected = new SummaryCommand().execute(model);
        model.updateFilteredEmployeeList(employee -> false);
        assertEquals(expected, new SummaryCommand().execute(model));
        assertEquals(List.of(), model.getFilteredEmployeeList());
    }

    @Test
    public void execute_afterAddEditDelete_recomputesFromCurrentData() throws Exception {
        Model model = new ModelManager();
        SummaryCommand command = new SummaryCommand();
        new AddCommand(employee("Alice")).execute(model);
        assertEquals(new CommandResult("Employee summary\nTotal employees: 1\n"
                + "Employees without tags: 1\n\nNo tags to summarize."), command.execute(model));

        EditCommand.EditEmployeeDescriptor edit = new EditCommand.EditEmployeeDescriptor();
        edit.setTags(employee("Alice", "remote").getTags());
        new EditCommand(Index.fromOneBased(1), edit).execute(model);
        assertEquals(new CommandResult("Employee summary\nTotal employees: 1\nEmployees without tags: 0\n\n"
                + "Employees by tag:\n  remote: 1\n\n"
                + "Employees may have multiple tags; tag counts can overlap."), command.execute(model));

        new DeleteCommand(Index.fromOneBased(1)).execute(model);
        assertEquals(new CommandResult(EMPTY_SUMMARY), command.execute(model));
    }

    private Employee employee(String name, String... tags) {
        return new EmployeeBuilder().withName(name).withTags(tags).build();
    }
}
