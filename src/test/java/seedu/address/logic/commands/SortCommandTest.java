package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.SortCommand.Field;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.employee.Employee;
import seedu.address.testutil.EmployeeBuilder;

public class SortCommandTest {
    private final AddressBookParser parser = new AddressBookParser();
    private final Employee high = new EmployeeBuilder().withName("zoe").withPhone("10000")
            .withEmail("Zoe@example.com").withAddress("Zebra Road").withSalary("10000")
            .withLeaveEntitlement(100).withEmployeeId(1).build();
    private final Employee low = new EmployeeBuilder().withName("Alice").withPhone("900")
            .withEmail("alice@example.com").withAddress("apple Road").withSalary("900")
            .withLeaveEntitlement(9).withEmployeeId(2).build();
    private final Employee middle = new EmployeeBuilder().withName("Bob").withPhone("4000")
            .withEmail("bob@example.com").withAddress("Banana Road").withSalary("4000")
            .withLeaveEntitlement(14).withEmployeeId(3).build();
    private final Model model = createModel(high, low, middle);

    @Test
    public void execute_eachFieldAndDirection_sortsDisplayedList() throws Exception {
        for (String prefix : List.of("n", "p", "e", "a", "s", "le")) {
            assertSort(prefix + "/asc", List.of(low, middle, high));
            assertSort(prefix + "/des", List.of(high, middle, low));
        }
        assertEquals(List.of(high, low, middle), model.getAddressBook().getEmployeeList());
    }

    @Test
    public void execute_filteredList_preservesFilterAndListReference() throws Exception {
        var displayed = model.getFilteredEmployeeList();
        parser.parseCommand("filter n/o").execute(model);
        assertSort("s/asc", List.of(middle, high));
        assertEquals(List.of(middle, high), displayed);
        parser.parseCommand("list").execute(model);
        assertEquals(List.of(low, middle, high), displayed);
        parser.parseCommand("find Alice").execute(model);
        assertSort("p/des", List.of(low));
        parser.parseCommand("filter n/nobody").execute(model);
        assertSort("s/asc", List.of());
        assertEquals(List.of(high, low, middle), model.getAddressBook().getEmployeeList());
    }

    @Test
    public void execute_equalValues_preservesStoredOrder() throws Exception {
        Employee another = new EmployeeBuilder().withName("Another").withSalary("900")
                .withEmployeeId(4).build();
        Model ties = createModel(low, another, middle);
        parser.parseCommand("sort s/asc").execute(ties);
        assertEquals(List.of(low, another, middle), ties.getFilteredEmployeeList());
        parser.parseCommand("sort s/des").execute(ties);
        assertEquals(List.of(middle, low, another), ties.getFilteredEmployeeList());
    }

    @Test
    public void execute_phoneWithLeadingZerosAndLargeAmounts_sortsWithoutOverflow() throws Exception {
        Employee huge = new EmployeeBuilder().withName("Huge").withPhone("99999999999999999999999")
                .withEmployeeId(5).build();
        Employee padded = new EmployeeBuilder().withName("Padded").withPhone("000900")
                .withEmployeeId(6).build();
        Model phones = createModel(huge, padded, middle);
        parser.parseCommand("sort p/asc").execute(phones);
        assertEquals(List.of(padded, middle, huge), phones.getFilteredEmployeeList());
    }

    @Test
    public void execute_indexCommandsFollowDisplayedOrderAndUpdatesResort() throws Exception {
        assertSort("s/asc", List.of(low, middle, high));
        parser.parseCommand("salary 1 s/20000").execute(model);
        assertEquals(List.of("Bob", "zoe", "Alice"), model.getFilteredEmployeeList().stream()
                .map(employee -> employee.getName().fullName).toList());
        parser.parseCommand("delete 1").execute(model);
        assertFalse(model.hasEmployee(middle));
        assertTrue(model.hasEmployee(high));
        parser.parseCommand("clear").execute(model);
        assertSort("n/des", List.of());
    }

    @Test
    public void equals_comparesFieldAndDirection() {
        SortCommand command = new SortCommand(Field.SALARY, true);
        assertEquals(command, command);
        assertEquals(command, new SortCommand(Field.SALARY, true));
        assertNotEquals(command, new SortCommand(Field.PHONE, true));
        assertNotEquals(command, new SortCommand(Field.SALARY, false));
        assertNotEquals(command, null);
        assertNotEquals(command, new ListCommand());
        assertThrows(NullPointerException.class, () -> new SortCommand(null, true));
        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    private void assertSort(String arguments, List<Employee> expected) throws Exception {
        Command command = parser.parseCommand("sort " + arguments);
        assertTrue(command instanceof SortCommand);
        CommandResult result = command.execute(model);
        assertTrue(result.getFeedbackToUser().startsWith("Sorted displayed employees by "));
        assertEquals(expected, model.getFilteredEmployeeList());
    }

    private static Model createModel(Employee... employees) {
        AddressBook addressBook = new AddressBook();
        for (Employee employee : employees) {
            addressBook.addEmployee(employee);
        }
        return new ModelManager(addressBook, new UserPrefs());
    }
}
