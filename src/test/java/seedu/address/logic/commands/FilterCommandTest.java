package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_EMPLOYEES_LISTED_OVERVIEW;
import static seedu.address.testutil.TypicalEmployees.ALICE;
import static seedu.address.testutil.TypicalEmployees.BENSON;
import static seedu.address.testutil.TypicalEmployees.CARL;
import static seedu.address.testutil.TypicalEmployees.DANIEL;
import static seedu.address.testutil.TypicalEmployees.getTypicalAddressBook;
import static seedu.address.testutil.TypicalEmployees.getTypicalEmployees;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.AddressBookParser;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.employee.Employee;
import seedu.address.model.employee.Task;
import seedu.address.model.leave.LeavePeriod;
import seedu.address.testutil.EmployeeBuilder;

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
        assertFilter("e/ALICE@EXAMPLE.COM", List.of(ALICE));
        assertFilter("a/JURONG WEST", List.of(ALICE));
        assertFilter("t/FRIENDS", List.of(ALICE, BENSON, DANIEL));
        assertFilter("t/friend", List.of());
    }

    @Test
    public void execute_multipleFields_requiresEveryCriterion() throws Exception {
        assertFilter("n/Meier t/friends", List.of(BENSON, DANIEL));
        assertFilter("n/Meier p/987", List.of(BENSON));
        assertFilter("n/Alice p/987", List.of());
        assertFilter("n/Alice p/943 e/alice@example.com a/Jurong t/friends", List.of(ALICE));
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

    @Test
    public void execute_tasksAndSalary_requiresAllCriteria() throws Exception {
        Employee alice = new EmployeeBuilder(ALICE).withSalary("3000")
                .withTasks(new Task("Quarterly report"), new Task("Review invoices")).build();
        Employee benson = new EmployeeBuilder(BENSON).withSalary("30000")
                .withTasks(new Task("Quarterly report")).build();
        Employee daniel = new EmployeeBuilder(DANIEL).withSalary("3000").build();
        AddressBook addressBook = new AddressBook();
        addressBook.addEmployee(alice);
        addressBook.addEmployee(benson);
        addressBook.addEmployee(daniel);
        model.setAddressBook(addressBook);

        assertFilter("task/REPORT", List.of(alice, benson));
        assertFilter("task/report task/INVOICES", List.of(alice));
        assertFilter("task/missing", List.of());
        assertFilter("s/3000", List.of(alice, daniel));
        assertFilter("s/300", List.of());
        assertFilter("task/report s/3000", List.of(alice));
        assertFilter("n/Alice t/friends task/invoices s/3000", List.of(alice));
        assertFilter("task/report s/0", List.of());
        assertEquals(addressBook, model.getAddressBook());
    }

    @Test
    public void execute_salaryComparators_matchesStrictNumericAmounts() throws Exception {
        Employee alice = new EmployeeBuilder(ALICE).withSalary("900").build();
        Employee benson = new EmployeeBuilder(BENSON).withSalary("4000").build();
        Employee daniel = new EmployeeBuilder(DANIEL).withSalary("10000").build();
        AddressBook addressBook = new AddressBook();
        addressBook.addEmployee(alice);
        addressBook.addEmployee(benson);
        addressBook.addEmployee(daniel);
        model.setAddressBook(addressBook);

        assertFilter("s/<4000", List.of(alice));
        assertFilter("s/>4000", List.of(daniel));
        assertFilter("s/4000", List.of(benson));
        assertFilter("s/<0", List.of());
        assertFilter("s/>50000", List.of());
        assertFilter("s/>0", List.of(alice, benson, daniel));
        assertFilter("s/<50000", List.of(alice, benson, daniel));
        assertEquals(addressBook, model.getAddressBook());
    }

    @Test
    public void execute_salaryBoundaries_matchesExactAmountsAndStrictComparisons() throws Exception {
        Employee alice = new EmployeeBuilder(ALICE).withSalary("0").build();
        Employee benson = new EmployeeBuilder(BENSON).withSalary("1").build();
        Employee carl = new EmployeeBuilder(CARL).withSalary("49999").build();
        Employee daniel = new EmployeeBuilder(DANIEL).withSalary("50000").build();
        AddressBook addressBook = new AddressBook();
        addressBook.addEmployee(alice);
        addressBook.addEmployee(benson);
        addressBook.addEmployee(carl);
        addressBook.addEmployee(daniel);
        model.setAddressBook(addressBook);

        // Exact matches at both limits and immediately inside them.
        assertFilter("s/0", List.of(alice));
        assertFilter("s/1", List.of(benson));
        assertFilter("s/49999", List.of(carl));
        assertFilter("s/50000", List.of(daniel));

        // Strict comparisons must exclude salaries equal to the threshold.
        assertFilter("s/<0", List.of());
        assertFilter("s/>0", List.of(benson, carl, daniel));
        assertFilter("s/<1", List.of(alice));
        assertFilter("s/>1", List.of(carl, daniel));
        assertFilter("s/<49999", List.of(alice, benson));
        assertFilter("s/>49999", List.of(daniel));
        assertFilter("s/<50000", List.of(alice, benson, carl));
        assertFilter("s/>50000", List.of());
        assertEquals(addressBook, model.getAddressBook());
    }

    @Test
    public void execute_salaryComparatorsWithOtherFields_requiresEveryCriterion() throws Exception {
        Employee alice = new EmployeeBuilder(ALICE).withSalary("3000")
                .withTasks(new Task("Quarterly report")).build();
        Employee benson = new EmployeeBuilder(BENSON).withSalary("5000").build();
        AddressBook addressBook = new AddressBook();
        addressBook.addEmployee(alice);
        addressBook.addEmployee(benson);
        model.setAddressBook(addressBook);

        assertFilter("n/Alice p/943 s/<4000", List.of(alice));
        assertFilter("n/Alice p/987 s/<4000", List.of());
        assertFilter("n/Alice p/943 s/>4000", List.of());
        assertFilter("n/Meier p/987 s/>4000", List.of(benson));
        assertFilter("n/Meier p/987 s/<4000", List.of());
        assertFilter("e/alice@example.com a/Jurong t/friends task/report s/<4000", List.of(alice));
        assertFilter("task/missing s/<4000", List.of());
    }

    @Test
    public void execute_leaveEntitlement_matchesExactAmountsAndStrictComparisons() throws Exception {
        Employee alice = new EmployeeBuilder(ALICE).withLeaveEntitlement(0).build();
        Employee benson = new EmployeeBuilder(BENSON).withLeaveEntitlement(14).build();
        Employee daniel = new EmployeeBuilder(DANIEL).withLeaveEntitlement(365).build();
        AddressBook addressBook = new AddressBook();
        addressBook.addEmployee(alice);
        addressBook.addEmployee(benson);
        addressBook.addEmployee(daniel);
        model.setAddressBook(addressBook);

        assertFilter("le/14", List.of(benson));
        assertFilter("le/<14", List.of(alice));
        assertFilter("le/>14", List.of(daniel));
        assertFilter("le/>9", List.of(benson, daniel));
        assertFilter("le/0", List.of(alice));
        assertFilter("le/365", List.of(daniel));
        assertFilter("le/<0", List.of());
        assertFilter("le/>365", List.of());
        assertFilter("le/>0", List.of(benson, daniel));
        assertFilter("le/<365", List.of(alice, benson));
        new ListCommand().execute(model);
        assertEquals(addressBook.getEmployeeList(), model.getFilteredEmployeeList());
        assertEquals(addressBook, model.getAddressBook());
    }

    @Test
    public void execute_leaveEntitlementWithOtherFields_requiresEveryCriterion() throws Exception {
        Employee alice = new EmployeeBuilder(ALICE).withLeaveEntitlement(14).withSalary("3000")
                .withTasks(new Task("Quarterly report")).build();
        Employee benson = new EmployeeBuilder(BENSON).withLeaveEntitlement(7).withSalary("5000").build();
        AddressBook addressBook = new AddressBook();
        addressBook.addEmployee(alice);
        addressBook.addEmployee(benson);
        model.setAddressBook(addressBook);

        assertFilter("n/Alice p/943 s/<4000 le/>10", List.of(alice));
        assertFilter("n/Alice p/943 s/>4000 le/>10", List.of());
        assertFilter("n/Alice p/943 s/<4000 le/<10", List.of());
        assertFilter("n/Alice p/987 le/>10", List.of());
        assertFilter("n/Benson le/>10", List.of());
        assertFilter("n/Meier le/<10", List.of(benson));
        assertFilter("e/alice@example.com a/Jurong t/friends task/report le/14", List.of(alice));
        assertFilter("task/missing le/14", List.of());

        model.addLeave(model.getAddressBook().getEmployeeList().get(0).getEmployeeId().orElseThrow(),
                new LeavePeriod(LocalDate.of(2026, 11, 2), LocalDate.of(2026, 11, 6)));
        assertFilter("n/Alice le/14", List.of(alice));
        assertFilter("n/Alice le/9", List.of());
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
