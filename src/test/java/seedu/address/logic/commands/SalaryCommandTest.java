package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_EMPLOYEE;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.employee.Address;
import seedu.address.model.employee.Email;
import seedu.address.model.employee.Employee;
import seedu.address.model.employee.LeaveEntitlement;
import seedu.address.model.employee.Name;
import seedu.address.model.employee.Phone;
import seedu.address.model.employee.Salary;
import seedu.address.model.employee.Task;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.EmployeeBuilder;

public class SalaryCommandTest {

    @Test
    public void execute_validIndex_updatesSalaryAndPreservesDetails() throws Exception {
        Model model = new ModelManager();
        Employee original = Employee.createUnassigned(new Name("Alice"), new Phone("91234567"),
                new Email("alice@example.com"), new Address("123 Main Street"), new Salary("1000"),
                new LeaveEntitlement(14), Set.of(new Tag("team")),
                List.of(new Task("Prepare report"), new Task("Meet partners")));
        model.addEmployee(original);
        Salary updatedSalary = new Salary("5000");

        CommandResult result = new SalaryCommand(INDEX_FIRST_EMPLOYEE, updatedSalary).execute(model);
        Employee updated = model.getFilteredEmployeeList().get(0);

        assertEquals(updatedSalary, updated.getSalary());
        assertEquals(original.getName(), updated.getName());
        assertEquals(original.getPhone(), updated.getPhone());
        assertEquals(original.getEmail(), updated.getEmail());
        assertEquals(original.getAddress(), updated.getAddress());
        assertEquals(original.getTags(), updated.getTags());
        assertEquals(original.getLeaveEntitlement(), updated.getLeaveEntitlement());
        assertEquals("Updated Alice's salary from $1000 to $5000.", result.getFeedbackToUser());
        assertEquals(original.getTasks(), updated.getTasks());
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        Model model = new ModelManager();
        SalaryCommand command = new SalaryCommand(INDEX_FIRST_EMPLOYEE, new Salary("5000"));

        assertCommandFailure(command, model, Messages.MESSAGE_INVALID_EMPLOYEE_DISPLAYED_INDEX);
    }
    @Test
    public void execute_filteredList_updatesDisplayedEmployee() throws Exception {
        Model model = new ModelManager();
        Employee alice = new EmployeeBuilder().withName("Alice").withSalary("1000").build();
        Employee bob = new EmployeeBuilder().withName("Bob").withSalary("2000").build();
        model.addEmployee(alice);
        model.addEmployee(bob);
        model.updateFilteredEmployeeList(employee -> employee.getName().equals(bob.getName()));

        new SalaryCommand(INDEX_FIRST_EMPLOYEE, new Salary("5000")).execute(model);

        assertEquals(2, model.getFilteredEmployeeList().size());
        assertEquals(alice.getSalary(), model.getAddressBook().getEmployeeList().get(0).getSalary());
        assertEquals(new Salary("5000"), model.getAddressBook().getEmployeeList().get(1).getSalary());
    }

    @Test
    public void constructor_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new SalaryCommand(null, new Salary("5000")));
        assertThrows(NullPointerException.class, () -> new SalaryCommand(INDEX_FIRST_EMPLOYEE, null));
    }

    @Test
    public void execute_invalidIndexPopulatedList_preservesModel() {
        Model model = new ModelManager();
        model.addEmployee(new EmployeeBuilder().build());
        assertCommandFailure(new SalaryCommand(Index.fromOneBased(2), new Salary("5000")),
                model, Messages.MESSAGE_INVALID_EMPLOYEE_DISPLAYED_INDEX);
    }

    @Test
    public void execute_invalidFilteredIndex_preservesModelAndFilter() {
        Model model = new ModelManager();
        model.addEmployee(new EmployeeBuilder().withName("Alice").build());
        model.addEmployee(new EmployeeBuilder().withName("Bob").build());
        model.updateFilteredEmployeeList(employee -> employee.getName().fullName.equals("Alice"));
        assertCommandFailure(new SalaryCommand(Index.fromOneBased(2), new Salary("5000")),
                model, Messages.MESSAGE_INVALID_EMPLOYEE_DISPLAYED_INDEX);
    }

    @Test
    public void execute_lastIndex_updatesOnlyTarget() throws Exception {
        Model model = new ModelManager();
        Employee alice = new EmployeeBuilder().withName("Alice").withSalary("1000").build();
        Employee bob = new EmployeeBuilder().withName("Bob").withSalary("2000").build();
        model.addEmployee(alice);
        model.addEmployee(bob);
        Employee storedAlice = model.getAddressBook().getEmployeeList().get(0);

        CommandResult result = new SalaryCommand(Index.fromOneBased(2),
                new Salary("50000")).execute(model);

        assertEquals(2, model.getAddressBook().getEmployeeList().size());
        assertEquals(storedAlice, model.getAddressBook().getEmployeeList().get(0));
        assertEquals(new Salary("50000"), model.getAddressBook().getEmployeeList().get(1).getSalary());
        assertTrue(result.getFeedbackToUser().contains("$50000"));
        assertFalse(result.isExit());
        assertFalse(result.isShowHelp());
    }

    @Test
    public void execute_sameSalary_preservesEmployeeAndListSize() throws Exception {
        Model model = new ModelManager() {
            @Override
            public void setEmployee(Employee target, Employee editedEmployee) {
                throw new AssertionError("An unchanged salary must not update the employee.");
            }
        };
        Employee original = new EmployeeBuilder().withName("Alice").withSalary("5000").build();
        model.addEmployee(original);
        Employee storedOriginal = model.getAddressBook().getEmployeeList().get(0);

        for (String salary : List.of("5000", "005000")) {
            CommandResult result = new SalaryCommand(INDEX_FIRST_EMPLOYEE, new Salary(salary)).execute(model);
            assertEquals("Alice's salary is already $5000. No changes made.", result.getFeedbackToUser());
        }

        assertEquals(1, model.getAddressBook().getEmployeeList().size());
        assertEquals(storedOriginal, model.getAddressBook().getEmployeeList().get(0));
    }

    @Test
    public void execute_sameSalaryFilteredList_preservesFilter() throws Exception {
        Model model = new ModelManager();
        Employee alice = new EmployeeBuilder().withName("Alice").withSalary("1000").build();
        Employee bob = new EmployeeBuilder().withName("Bob").withSalary("2000").build();
        model.addEmployee(alice);
        model.addEmployee(bob);
        model.updateFilteredEmployeeList(employee -> employee.getName().equals(bob.getName()));
        List<Employee> originalEmployees = List.copyOf(model.getAddressBook().getEmployeeList());

        CommandResult result = new SalaryCommand(INDEX_FIRST_EMPLOYEE, new Salary("2000")).execute(model);

        assertEquals("Bob's salary is already $2000. No changes made.", result.getFeedbackToUser());
        assertEquals(originalEmployees, model.getAddressBook().getEmployeeList());
        assertEquals(List.of(originalEmployees.get(1)), model.getFilteredEmployeeList());
    }

    @Test
    public void execute_repeatedSalaryCommand_reportsNoChange() throws Exception {
        Model model = new ModelManager();
        model.addEmployee(new EmployeeBuilder().withName("Alice").withSalary("1000").build());
        SalaryCommand command = new SalaryCommand(INDEX_FIRST_EMPLOYEE, new Salary("5000"));

        CommandResult firstResult = command.execute(model);
        CommandResult secondResult = command.execute(model);

        assertEquals("Updated Alice's salary from $1000 to $5000.", firstResult.getFeedbackToUser());
        assertEquals("Alice's salary is already $5000. No changes made.", secondResult.getFeedbackToUser());
        assertEquals(new Salary("5000"), model.getFilteredEmployeeList().get(0).getSalary());
    }

    @Test
    public void execute_boundarySalaries_preservesOtherDetails() throws Exception {
        Model model = new ModelManager();
        model.addEmployee(new EmployeeBuilder().withName("Alice").withSalary("1000").build());

        for (String salary : List.of("0", "50000", "0")) {
            Employee before = model.getFilteredEmployeeList().get(0);
            Employee expected = new EmployeeBuilder(before).withSalary(salary).build();

            CommandResult result = new SalaryCommand(INDEX_FIRST_EMPLOYEE, new Salary(salary)).execute(model);

            assertEquals("Updated Alice's salary from " + before.getSalary() + " to $" + salary + ".",
                    result.getFeedbackToUser());
            assertEquals(List.of(expected), model.getAddressBook().getEmployeeList());
            assertFalse(result.isExit());
            assertFalse(result.isShowHelp());
        }
    }

    @Test
    public void execute_sameSalarySortedList_preservesOrderAndEmployeeInstances() throws Exception {
        Model model = new ModelManager();
        model.addEmployee(new EmployeeBuilder().withName("Alice").withSalary("1000").build());
        model.addEmployee(new EmployeeBuilder().withName("Bob").withSalary("2000").build());
        model.sortFilteredEmployeeList(Comparator.comparing(employee -> employee.getName().fullName,
                Comparator.reverseOrder()));
        List<Employee> originalEmployees = List.copyOf(model.getFilteredEmployeeList());

        CommandResult result = new SalaryCommand(INDEX_FIRST_EMPLOYEE, new Salary("002000")).execute(model);

        assertEquals("Bob's salary is already $2000. No changes made.", result.getFeedbackToUser());
        assertEquals(originalEmployees, model.getFilteredEmployeeList());
        assertSame(originalEmployees.get(0), model.getFilteredEmployeeList().get(0));
        assertSame(originalEmployees.get(1), model.getFilteredEmployeeList().get(1));
        assertFalse(result.isExit());
        assertFalse(result.isShowHelp());
    }

    @Test
    public void equals() {
        SalaryCommand command = new SalaryCommand(INDEX_FIRST_EMPLOYEE, new Salary("5000"));
        assertTrue(command.equals(command));
        assertTrue(command.equals(new SalaryCommand(INDEX_FIRST_EMPLOYEE, new Salary("005000"))));
        assertFalse(command.equals(new SalaryCommand(Index.fromOneBased(2), new Salary("5000"))));
        assertFalse(command.equals(new SalaryCommand(INDEX_FIRST_EMPLOYEE, new Salary("6000"))));
        assertFalse(command.equals(null));
        assertFalse(command.equals(new ClearCommand()));
    }
}
