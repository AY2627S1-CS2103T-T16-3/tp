package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.employee.Employee;
import seedu.address.model.employee.EmployeeId;
import seedu.address.model.employee.exceptions.DuplicateEmployeeException;
import seedu.address.testutil.EmployeeBuilder;

public class EmployeeIdAssignmentTest {

    @Test
    public void addEmployee_assignsDistinctIds_withoutMutatingDraft() {
        AddressBook book = new AddressBook();
        Employee draft = draft("Alice");
        Employee alice = book.addEmployee(draft);
        Employee bob = book.addEmployee(draft("Bob"));
        assertTrue(draft.getEmployeeId().isEmpty());
        assertId("1", alice);
        assertId("2", bob);
        assertEquals(List.of(alice, bob), book.getEmployeeList());
    }

    @Test
    public void addEmployee_ignoresFilteredView() {
        Model model = new ModelManager();
        model.addEmployee(draft("Alice"));
        model.updateFilteredEmployeeList(employee -> false);
        assertTrue(model.getFilteredEmployeeList().isEmpty());
        assertId("2", model.addEmployee(draft("Bob")));
    }

    @Test
    public void addEmployee_rejectsDuplicateId_withoutChangingData() {
        AddressBook book = new AddressBook();
        Employee alice = book.addEmployee(draft("Alice"));
        Employee conflicting = draft("Bob").withEmployeeId(new EmployeeId("1"));
        assertThrows(DuplicateEmployeeException.class, () -> book.addEmployee(conflicting));
        assertEquals(List.of(alice), book.getEmployeeList());
        assertId("2", book.addEmployee(draft("Bob")));
    }

    @Test
    public void addEmployee_failedDuplicateName_doesNotConsumeId() {
        AddressBook book = new AddressBook();
        book.addEmployee(draft("Alice"));
        assertThrows(DuplicateEmployeeException.class, () -> book.addEmployee(draft("Alice")));
        assertId("2", book.addEmployee(draft("Bob")));
    }

    @Test
    public void deleteThenAdd_reusesGap_withoutChangingSurvivingId() {
        AddressBook book = new AddressBook();
        Employee alice = book.addEmployee(draft("Alice"));
        Employee bob = book.addEmployee(draft("Bob"));
        book.removeEmployee(alice);
        assertId("1", book.addEmployee(draft("Carol")));
        assertEquals(bob, book.getEmployeeList().get(0));
        assertId("2", bob);
    }

    @Test
    public void setEmployees_reservesExistingIds_beforeAssigningLegacyRecords() {
        AddressBook book = new AddressBook();
        Employee existing = draft("Bob").withEmployeeId(new EmployeeId("1"));
        book.setEmployees(List.of(draft("Alice"), existing));
        assertId("2", book.getEmployeeList().get(0));
        assertEquals(existing, book.getEmployeeList().get(1));
        AddressBook copy = new AddressBook(book);
        assertEquals(book, copy);
        assertId("3", copy.addEmployee(draft("Carol")));
    }

    @Test
    public void setEmployees_duplicateIds_failsAtomically() {
        AddressBook book = new AddressBook();
        Employee original = book.addEmployee(draft("Original"));
        Employee alice = draft("Alice").withEmployeeId(new EmployeeId("2"));
        Employee bob = draft("Bob").withEmployeeId(new EmployeeId("2"));
        assertThrows(DuplicateEmployeeException.class, () -> book.setEmployees(List.of(alice, bob)));
        assertEquals(List.of(original), book.getEmployeeList());
    }

    @Test
    public void setEmployee_preservesId_andRejectsChangingIt() {
        AddressBook book = new AddressBook();
        Employee alice = book.addEmployee(draft("Alice"));
        Employee edit = draft("Renamed Alice");
        book.setEmployee(alice, edit);
        Employee updated = book.getEmployeeList().get(0);
        assertId("1", updated);
        assertEquals(edit.getName(), updated.getName());
        assertThrows(IllegalArgumentException.class, () ->
                book.setEmployee(updated, updated.withEmployeeId(new EmployeeId("2"))));
        assertEquals(List.of(updated), book.getEmployeeList());
    }

    @Test
    public void setEmployee_sameId_doesNotAllowDuplicateName() {
        AddressBook book = new AddressBook();
        Employee alice = book.addEmployee(draft("Alice"));
        Employee bob = book.addEmployee(draft("Bob"));
        Employee edit = new EmployeeBuilder(alice).withName("Bob").build();
        assertThrows(DuplicateEmployeeException.class, () -> book.setEmployee(alice, edit));
        assertEquals(List.of(alice, bob), book.getEmployeeList());
    }

    private Employee draft(String name) {
        return new EmployeeBuilder().withName(name).build();
    }

    private void assertId(String expected, Employee employee) {
        assertEquals(new EmployeeId(expected), employee.getEmployeeId().orElseThrow());
    }
}
