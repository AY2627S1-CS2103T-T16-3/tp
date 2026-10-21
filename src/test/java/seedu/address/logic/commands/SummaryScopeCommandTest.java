package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
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
    public void execute_filteredSubset_countsOnlyVisibleEmployeesAndPreservesPredicate() throws CommandException {
        Model model = new ModelManager();
        Employee alice = new EmployeeBuilder().withName("Alice").withTags("engineering").build();
        Employee bob = new EmployeeBuilder().withName("Bob").withTags("remote").build();
        model.addEmployee(alice);
        model.addEmployee(bob);
        Employee storedAlice = model.getAddressBook().getEmployeeList().get(0);
        Employee storedBob = model.getAddressBook().getEmployeeList().get(1);
        model.updateFilteredEmployeeList(employee -> employee.getName().fullName.startsWith("B"));
        AddressBook before = new AddressBook(model.getAddressBook());
        SummaryCommand command = new SummaryCommand(SummaryCommand.Scope.FILTERED);
        assertEquals("Employee summary\nScope: Filtered employees\nTotal employees: 1\n"
                + "Employees without tags: 0\n\nEmployees by tag:\n  remote: 1\n\n"
                + "Employees may have multiple tags; tag counts can overlap." + zeroOverview(1),
                command.execute(model).getFeedbackToUser());
        assertEquals(before, model.getAddressBook());
        assertEquals(List.of(storedBob), model.getFilteredEmployeeList());
        Employee renamed = new EmployeeBuilder(storedAlice).withName("Bella").build();
        model.setEmployee(storedAlice, renamed);
        assertEquals(List.of(renamed, storedBob), model.getFilteredEmployeeList());
        assertTrue(command.execute(model).getFeedbackToUser().contains("Total employees: 2"));
    }

    @Test
    public void execute_noMatches_doesNotFallBackToAllEmployees() throws CommandException {
        Model model = new ModelManager();
        model.addEmployee(new EmployeeBuilder().build());
        model.updateFilteredEmployeeList(employee -> false);
        assertEquals("Employee summary\nScope: Filtered employees\nTotal employees: 0\n"
                + "Employees without tags: 0\n\nNo tags to summarize." + zeroOverview(0),
                new SummaryCommand().execute(model).getFeedbackToUser());
        assertEquals(List.of(), model.getFilteredEmployeeList());
        assertTrue(new SummaryCommand(SummaryCommand.Scope.ALL).execute(model)
                .getFeedbackToUser().contains("Total employees: 1"));
    }

    @Test
    public void execute_noRestriction_allScopesHaveSameCountsButDifferentLabels() throws CommandException {
        Model model = new ModelManager();
        model.addEmployee(new EmployeeBuilder().build());
        String all = new SummaryCommand(SummaryCommand.Scope.ALL).execute(model).getFeedbackToUser();
        assertEquals(all.replace("Scope: All employees", "Scope: Filtered employees"),
                new SummaryCommand().execute(model).getFeedbackToUser());
        assertEquals(all.replace("Scope: All employees", "Scope: Filtered employees"),
                new SummaryCommand(SummaryCommand.Scope.FILTERED).execute(model).getFeedbackToUser());
    }

    @Test
    public void execute_afterParsing_readsCurrentFilterAndLatestData() throws Exception {
        SummaryCommand command = new SummaryCommandParser().parse("");
        Model model = new ModelManager();
        Employee alice = new EmployeeBuilder().withName("Alice").build();
        model.addEmployee(alice);
        model.updateFilteredEmployeeList(employee -> false);
        assertTrue(command.execute(model).getFeedbackToUser().contains("Total employees: 0"));
        model.updateFilteredEmployeeList(Model.PREDICATE_SHOW_ALL_EMPLOYEES);
        assertTrue(command.execute(model).getFeedbackToUser().contains("Total employees: 1"));
        model.deleteEmployee(model.getAddressBook().getEmployeeList().get(0));
        assertTrue(command.execute(model).getFeedbackToUser().contains("Total employees: 0"));
    }

    @Test
    public void equalsAndHashCode_compareScope() {
        SummaryCommand all = new SummaryCommand(SummaryCommand.Scope.ALL);
        assertEquals(all, all);
        assertEquals(all, new SummaryCommand(SummaryCommand.Scope.ALL));
        assertEquals(all.hashCode(), new SummaryCommand(SummaryCommand.Scope.ALL).hashCode());
        assertNotEquals(all, new SummaryCommand());
        assertEquals(new SummaryCommand(SummaryCommand.Scope.FILTERED), new SummaryCommand());
        assertNotEquals(all, null);
        assertNotEquals(all, "summary");
    }

    private static String zeroOverview(int employees) {
        String salary = employees == 0 ? "N/A" : "$0.00";
        return "\n\nTask overview\nTotal assigned tasks: 0\nEmployees with tasks: 0\n"
                + "Employees without tasks: " + employees + "\nMost tasks assigned to one employee: 0"
                + "\n\nSalary overview\nTotal recorded salary: $0.00\nAverage salary: " + salary
                + "\nLowest salary: " + salary + "\nHighest salary: " + salary;
    }
}
