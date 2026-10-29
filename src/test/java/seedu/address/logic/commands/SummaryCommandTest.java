package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.SummaryTestUtil.assertSummaryValue;

import java.util.List;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.employee.Employee;
import seedu.address.testutil.EmployeeBuilder;

public class SummaryCommandTest {
    private static final String EMPTY_SUMMARY = "Employee summary\nScope: Filtered employees\n\n" + """
            +----------------------+-------+
            | Metric               | Value |
            +----------------------+-------+
            | Employees            |     0 |
            | Without tags         |     0 |
            | Assigned tasks       |     0 |
            | Employees with tasks |     0 |
            | Leave records        |     0 |
            | Total salary         | $0.00 |
            | Average salary       |   N/A |
            +----------------------+-------+""";

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new SummaryCommand().execute(null));
    }

    @Test
    public void execute_emptyBook_returnsZeroCounts() throws CommandException {
        assertEquals(new CommandResult(EMPTY_SUMMARY, CommandResult.DisplayFormat.TABLE),
                new SummaryCommand().execute(new ModelManager()));
    }

    @Test
    public void execute_noTags_returnsEssentialCountsWithoutTagTable() throws CommandException {
        Model model = new ModelManager();
        model.addEmployee(employee("Alice"));
        String result = new SummaryCommand().execute(model).getFeedbackToUser();
        assertSummaryValue(result, "Employees", "1");
        assertSummaryValue(result, "Without tags", "1");
        assertSummaryValue(result, "Assigned tasks", "0");
        assertSummaryValue(result, "Employees with tasks", "0");
        assertSummaryValue(result, "Total salary", "$0.00");
        assertSummaryValue(result, "Average salary", "$0.00");
        assertFalse(result.contains("| Tag"));
    }

    @Test
    public void execute_mixedTags_formatsSortedCountsWithoutChangingModel() throws CommandException {
        Model model = new ModelManager();
        model.addEmployee(employee("Alice", "Remote", "Engineering", "engineering"));
        model.addEmployee(employee("Bob", "engineering", "intern"));
        model.addEmployee(employee("Carol"));
        Model expectedModel = new ModelManager(model.getAddressBook(), model.getUserPrefs());
        String result = new SummaryCommand().execute(model).getFeedbackToUser();
        assertSummaryValue(result, "Employees", "3");
        assertSummaryValue(result, "Without tags", "1");
        assertSummaryValue(result, "engineering", "2");
        assertSummaryValue(result, "intern", "1");
        assertSummaryValue(result, "remote", "1");
        assertFalse(result.contains("Engineering"));
        assertEquals(expectedModel, model);
    }

    @Test
    public void execute_explicitAllScope_summarizesWholeBookAndPreservesPredicate() throws CommandException {
        Model model = new ModelManager();
        Employee alice = employee("Alice", "engineering");
        Employee bob = employee("Bob", "remote");
        model.addEmployee(alice);
        model.addEmployee(bob);
        Employee storedAlice = model.getAddressBook().getEmployeeList().get(0);
        Employee storedBob = model.getAddressBook().getEmployeeList().get(1);
        Predicate<Employee> filter = employee -> employee.getName().fullName.startsWith("A");
        model.updateFilteredEmployeeList(filter);
        AddressBook before = new AddressBook(model.getAddressBook());

        CommandResult filteredResult = new SummaryCommand(SummaryCommand.Scope.ALL).execute(model);
        Model unfiltered = new ModelManager(before, model.getUserPrefs());
        assertEquals(new SummaryCommand(SummaryCommand.Scope.ALL).execute(unfiltered), filteredResult);
        assertEquals(before, model.getAddressBook());
        assertEquals(List.of(storedAlice), model.getFilteredEmployeeList());

        // Verify the predicate remains active, not just the previously displayed contents.
        Employee renamedBob = new EmployeeBuilder(storedBob).withName("Andrew").build();
        model.setEmployee(storedBob, renamedBob);
        assertEquals(List.of(storedAlice, renamedBob), model.getFilteredEmployeeList());
    }

    @Test
    public void execute_explicitAllWithNoMatches_stillCountsStoredEmployees() throws CommandException {
        Model model = new ModelManager();
        model.addEmployee(employee("Alice"));
        CommandResult expected = new SummaryCommand(SummaryCommand.Scope.ALL).execute(model);
        model.updateFilteredEmployeeList(employee -> false);
        assertEquals(expected, new SummaryCommand(SummaryCommand.Scope.ALL).execute(model));
        assertEquals(List.of(), model.getFilteredEmployeeList());
    }

    @Test
    public void execute_afterAddEditDelete_recomputesFromCurrentData() throws Exception {
        Model model = new ModelManager();
        SummaryCommand command = new SummaryCommand();
        new AddCommand(employee("Alice")).execute(model);
        String result = command.execute(model).getFeedbackToUser();
        assertSummaryValue(result, "Employees", "1");
        assertSummaryValue(result, "Without tags", "1");

        EditCommand.EditEmployeeDescriptor edit = new EditCommand.EditEmployeeDescriptor();
        edit.setTags(employee("Alice", "remote").getTags());
        new EditCommand(Index.fromOneBased(1), edit).execute(model);
        result = command.execute(model).getFeedbackToUser();
        assertSummaryValue(result, "Without tags", "0");
        assertSummaryValue(result, "remote", "1");

        new DeleteCommand(Index.fromOneBased(1)).execute(model);
        assertEquals(new CommandResult(EMPTY_SUMMARY, CommandResult.DisplayFormat.TABLE), command.execute(model));
    }

    private Employee employee(String name, String... tags) {
        return new EmployeeBuilder().withName(name).withTags(tags).build();
    }

}
