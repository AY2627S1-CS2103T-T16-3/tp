package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.SummaryTestUtil.assertSummaryValue;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.AddressBookParser;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.employee.Employee;
import seedu.address.model.employee.Task;
import seedu.address.testutil.EmployeeBuilder;

public class SummarySortCommandTest {
    private static final Employee ALICE = new EmployeeBuilder().withName("Alice").withEmployeeId(1)
            .withTags("engineering").withSalary("1000").withTasks(new Task("Report")).build();
    private static final Employee BOB = new EmployeeBuilder().withName("Bob").withEmployeeId(2)
            .withTags("remote").withSalary("2000").build();
    private static final Employee CAROL = new EmployeeBuilder().withName("Carol").withEmployeeId(3)
            .withTags("engineering").withSalary("3000").withTasks(new Task("Review"), new Task("Plan")).build();

    private final AddressBookParser parser = new AddressBookParser();

    @Test
    public void execute_allScopeInclude_afterNameSortMatchesDisplayedSelection() throws Exception {
        Model model = modelWithEmployees(CAROL, BOB, ALICE);
        parser.parseCommand("sort n/asc").execute(model);
        parser.parseCommand("list").execute(model);

        String filtered = parser.parseCommand("summary i/1").execute(model).getFeedbackToUser();
        String all = parser.parseCommand("summary scope/all i/1").execute(model).getFeedbackToUser();

        assertEquals(filtered.replace("Scope: Filtered employees", "Scope: All employees"), all);
        assertSummaryValue(all, "Total salary", "$1000.00");
        assertSummaryValue(all, "Assigned tasks", "1");
        assertSummaryValue(all, "engineering", "1");
        assertEquals(List.of(CAROL, BOB, ALICE), model.getAddressBook().getEmployeeList());
    }

    @Test
    public void execute_allScopeExclude_afterSalarySortPreservesFilterAndStoredOrder() throws Exception {
        Model model = modelWithEmployees(ALICE, BOB, CAROL);
        model.updateFilteredEmployeeList(employee -> !employee.equals(BOB));
        parser.parseCommand("sort s/des").execute(model);
        AddressBook before = new AddressBook(model.getAddressBook());

        String result = parser.parseCommand("summary scope/all e/1").execute(model).getFeedbackToUser();

        assertSummaryValue(result, "Employees", "2");
        assertSummaryValue(result, "Total salary", "$3000.00");
        assertSummaryValue(result, "Assigned tasks", "1");
        assertSummaryValue(result, "remote", "1");
        assertEquals(before, model.getAddressBook());
        assertEquals(List.of(CAROL, ALICE), model.getFilteredEmployeeList());
        parser.parseCommand("list").execute(model);
        assertEquals(List.of(CAROL, BOB, ALICE), model.getFilteredEmployeeList());
    }

    @Test
    public void execute_allScopeWithEmptyFilter_selectsFromFullSortedList() throws Exception {
        Model model = modelWithEmployees(ALICE, BOB, CAROL);
        model.updateFilteredEmployeeList(employee -> false);
        parser.parseCommand("sort n/des").execute(model);

        String result = parser.parseCommand("summary scope/all i/1 3 e/3")
                .execute(model).getFeedbackToUser();

        assertSummaryValue(result, "Employees", "1");
        assertSummaryValue(result, "Total salary", "$3000.00");
        assertEquals(List.of(), model.getFilteredEmployeeList());
        assertEquals(List.of(ALICE, BOB, CAROL), model.getAddressBook().getEmployeeList());
    }

    @Test
    public void execute_allScopeWithEqualSortValues_preservesStoredOrderForTies() throws Exception {
        Employee bobWithSameSalary = new EmployeeBuilder(BOB).withSalary("1000").build();
        Model model = modelWithEmployees(bobWithSameSalary, ALICE, CAROL);
        parser.parseCommand("sort s/des").execute(model);

        String result = parser.parseCommand("summary scope/all i/2").execute(model).getFeedbackToUser();

        assertSummaryValue(result, "remote", "1");
        assertSummaryValue(result, "Assigned tasks", "0");
        assertEquals(List.of(CAROL, bobWithSameSalary, ALICE), model.getFilteredEmployeeList());
    }

    @Test
    public void execute_repeatedSortsWithTiedValues_scopesSelectSameEmployees() throws Exception {
        Model model = modelWithEmployees(new EmployeeBuilder(CAROL).withSalary("1000").build(),
                new EmployeeBuilder(BOB).withSalary("1000").build(), ALICE);
        parser.parseCommand("sort n/asc").execute(model);
        parser.parseCommand("sort s/asc").execute(model);
        parser.parseCommand("list").execute(model);
        assertEquals(List.of("Alice", "Bob", "Carol"), model.getFilteredEmployeeList().stream()
                .map(employee -> employee.getName().fullName).toList());

        for (String alias : List.of("summary", "/summary")) {
            for (String selection : List.of("i/1", "e/1", "i/1 2 3 e/2")) {
                String filtered = parser.parseCommand(alias + " " + selection).execute(model).getFeedbackToUser();
                String all = parser.parseCommand(alias + " scope/all " + selection).execute(model).getFeedbackToUser();
                assertEquals(filtered.replace("Scope: Filtered employees", "Scope: All employees"), all);
            }
        }
    }

    @Test
    public void execute_sortWithHiddenEmployeesAndTies_fullScopeMatchesListAfterFilterReset() throws Exception {
        Model model = modelWithEmployees(new EmployeeBuilder(CAROL).withSalary("1000").build(),
                new EmployeeBuilder(BOB).withSalary("1000").build(), ALICE);
        parser.parseCommand("sort n/asc").execute(model);
        parser.parseCommand("find Alice Carol").execute(model);
        parser.parseCommand("sort s/asc").execute(model);
        AddressBook before = new AddressBook(model.getAddressBook());
        List<Employee> displayed = List.copyOf(model.getFilteredEmployeeList());

        String all = parser.parseCommand("summary scope/all i/2").execute(model).getFeedbackToUser();
        assertSummaryValue(all, "remote", "1");
        assertSummaryValue(all, "Assigned tasks", "0");
        assertEquals(displayed, model.getFilteredEmployeeList());
        assertEquals(before, model.getAddressBook());

        parser.parseCommand("list").execute(model);
        String filtered = parser.parseCommand("summary i/2").execute(model).getFeedbackToUser();
        assertEquals(filtered.replace("Scope: Filtered employees", "Scope: All employees"), all);
    }

    @Test
    public void execute_afterParsing_usesCurrentSortAndEmployeeValues() throws Exception {
        Model model = modelWithEmployees(ALICE, BOB, CAROL);
        Command summary = parser.parseCommand("summary scope/all i/1");
        parser.parseCommand("sort n/des").execute(model);
        assertSummaryValue(summary.execute(model).getFeedbackToUser(), "Total salary", "$3000.00");

        parser.parseCommand("sort s/asc").execute(model);
        assertSummaryValue(summary.execute(model).getFeedbackToUser(), "Total salary", "$1000.00");
        model.setEmployee(ALICE, new EmployeeBuilder(ALICE).withSalary("5000").build());
        assertSummaryValue(summary.execute(model).getFeedbackToUser(), "Total salary", "$2000.00");
    }

    private static Model modelWithEmployees(Employee... employees) {
        Model model = new ModelManager();
        for (Employee employee : employees) {
            model.addEmployee(employee);
        }
        return model;
    }
}
