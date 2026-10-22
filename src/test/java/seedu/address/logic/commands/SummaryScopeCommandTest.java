package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.SummaryTestUtil.assertSummaryValue;

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
        Employee storedAlice = model.getAddressBook().getEmployeeList().get(0);
        Employee storedBob = model.getAddressBook().getEmployeeList().get(1);
        model.updateFilteredEmployeeList(employee -> employee.getName().fullName.startsWith("B"));
        AddressBook before = new AddressBook(model.getAddressBook());
        SummaryCommand command = new SummaryCommand(SummaryCommand.Scope.FILTERED);
        String result = command.execute(model).getFeedbackToUser();
        assertTrue(result.startsWith("Employee summary\nScope: Filtered employees\n\n"));
        assertSummaryValue(result, "Employees", "1");
        assertSummaryValue(result, "remote", "1");
        assertEquals(before, model.getAddressBook());
        assertEquals(List.of(storedBob), model.getFilteredEmployeeList());
        Employee renamed = new EmployeeBuilder(storedAlice).withName("Bella").build();
        model.setEmployee(storedAlice, renamed);
        assertEquals(List.of(renamed, storedBob), model.getFilteredEmployeeList());
        assertSummaryValue(command.execute(model).getFeedbackToUser(), "Employees", "2");
    }

    @Test
    public void execute_noMatches_doesNotFallBackToAllEmployees() {
        Model model = new ModelManager();
        model.addEmployee(new EmployeeBuilder().build());
        model.updateFilteredEmployeeList(employee -> false);
        assertEquals(new SummaryCommand().execute(new ModelManager()), new SummaryCommand().execute(model));
        assertEquals(List.of(), model.getFilteredEmployeeList());
        assertSummaryValue(new SummaryCommand(SummaryCommand.Scope.ALL).execute(model)
                .getFeedbackToUser(), "Employees", "1");
    }

    @Test
    public void execute_noRestriction_allScopesHaveSameCountsButDifferentLabels() {
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
        assertSummaryValue(command.execute(model).getFeedbackToUser(), "Employees", "0");
        model.updateFilteredEmployeeList(Model.PREDICATE_SHOW_ALL_EMPLOYEES);
        assertSummaryValue(command.execute(model).getFeedbackToUser(), "Employees", "1");
        model.deleteEmployee(model.getAddressBook().getEmployeeList().get(0));
        assertSummaryValue(command.execute(model).getFeedbackToUser(), "Employees", "0");
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

}
