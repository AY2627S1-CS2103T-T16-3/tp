package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.SummaryCommandParser;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.employee.Employee;
import seedu.address.testutil.EmployeeBuilder;

public class SummaryScopeCommandTest {
    @Test
    public void constructor_nullScope_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new SummaryCommand(null));
    }

    @Test
    public void execute_filteredSubset_countsOnlyVisibleEmployeesAndPreservesPredicate() {
        Model model = new ModelManager();
        Employee alice = new EmployeeBuilder().withName("Alice").withTags("engineering").build();
        Employee bob = new EmployeeBuilder().withName("Bob").withTags("remote").build();
        model.addEmployee(alice);
        model.addEmployee(bob);
        model.updateFilteredEmployeeList(employee -> employee.getName().fullName.startsWith("B"));
        AddressBook before = new AddressBook(model.getAddressBook());
        SummaryCommand command = new SummaryCommand(SummaryCommand.Scope.FILTERED);
        assertEquals("Employee summary\nScope: Filtered employees\nTotal employees: 1\n"
                + "Employees without tags: 0\n\nEmployees by tag:\n  remote: 1\n\n"
                + "Employees may have multiple tags; tag counts can overlap.",
                command.execute(model).getFeedbackToUser());
        assertEquals(before, model.getAddressBook());
        assertEquals(List.of(bob), model.getFilteredEmployeeList());
        Employee renamed = new EmployeeBuilder(alice).withName("Bella").build();
        model.setEmployee(alice, renamed);
        assertEquals(List.of(renamed, bob), model.getFilteredEmployeeList());
        assertTrue(command.execute(model).getFeedbackToUser().contains("Total employees: 2"));
    }

    @Test
    public void execute_noMatches_doesNotFallBackToAllEmployees() {
        Model model = new ModelManager();
        model.addEmployee(new EmployeeBuilder().build());
        model.updateFilteredEmployeeList(employee -> false);
        assertEquals("Employee summary\nScope: Filtered employees\nTotal employees: 0\n"
                + "Employees without tags: 0\n\nNo tags to summarize.",
                new SummaryCommand(SummaryCommand.Scope.FILTERED).execute(model).getFeedbackToUser());
        assertEquals(List.of(), model.getFilteredEmployeeList());
        assertTrue(new SummaryCommand().execute(model).getFeedbackToUser().contains("Total employees: 1"));
    }

    @Test
    public void execute_noRestriction_allScopesHaveSameCountsButDifferentLabels() {
        Model model = new ModelManager();
        model.addEmployee(new EmployeeBuilder().build());
        String all = new SummaryCommand().execute(model).getFeedbackToUser();
        assertEquals(all, new SummaryCommand(SummaryCommand.Scope.ALL).execute(model).getFeedbackToUser());
        assertEquals(all.replace("Scope: All employees", "Scope: Filtered employees"),
                new SummaryCommand(SummaryCommand.Scope.FILTERED).execute(model).getFeedbackToUser());
    }

    @Test
    public void execute_afterParsing_readsCurrentFilterAndLatestData() throws Exception {
        SummaryCommand command = new SummaryCommandParser().parse("scope/filtered");
        Model model = new ModelManager();
        Employee alice = new EmployeeBuilder().withName("Alice").build();
        model.addEmployee(alice);
        model.updateFilteredEmployeeList(employee -> false);
        assertTrue(command.execute(model).getFeedbackToUser().contains("Total employees: 0"));
        model.updateFilteredEmployeeList(Model.PREDICATE_SHOW_ALL_EMPLOYEES);
        assertTrue(command.execute(model).getFeedbackToUser().contains("Total employees: 1"));
        model.deleteEmployee(alice);
        assertTrue(command.execute(model).getFeedbackToUser().contains("Total employees: 0"));
    }

    @Test
    public void equalsAndHashCode_compareScope() {
        SummaryCommand all = new SummaryCommand();
        assertEquals(all, all);
        assertEquals(all, new SummaryCommand(SummaryCommand.Scope.ALL));
        assertEquals(all.hashCode(), new SummaryCommand(SummaryCommand.Scope.ALL).hashCode());
        assertNotEquals(all, new SummaryCommand(SummaryCommand.Scope.FILTERED));
        assertNotEquals(all, null);
        assertNotEquals(all, "summary");
    }
}
