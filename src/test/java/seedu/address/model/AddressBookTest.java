package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalEmployees.ALICE;
import static seedu.address.testutil.TypicalEmployees.BENSON;
import static seedu.address.testutil.TypicalEmployees.BOB;
import static seedu.address.testutil.TypicalEmployees.getTypicalAddressBook;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.employee.Employee;
import seedu.address.model.employee.EmployeeId;
import seedu.address.model.employee.exceptions.DuplicateEmployeeException;
import seedu.address.testutil.EmployeeBuilder;

public class AddressBookTest {

    private final AddressBook addressBook = new AddressBook();

    @Test
    public void constructor() {
        assertEquals(List.of(), addressBook.getEmployeeList());
    }

    @Test
    public void resetData_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> addressBook.resetData(null));
    }

    @Test
    public void resetData_withValidReadOnlyAddressBook_replacesData() {
        AddressBook newData = getTypicalAddressBook();
        addressBook.resetData(newData);
        assertEquals(newData, addressBook);
    }

    @Test
    public void resetData_withDuplicateEmployees_throwsDuplicateEmployeeException() {
        // Two employees with the same identity fields
        Employee editedAlice = new EmployeeBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).withTags(VALID_TAG_HUSBAND)
                .withoutEmployeeId().build();
        List<Employee> newEmployees = List.of(ALICE, editedAlice);
        AddressBookStub newData = new AddressBookStub(newEmployees);

        assertThrows(DuplicateEmployeeException.class, () -> addressBook.resetData(newData));
    }

    @Test
    public void resetData_withAssignedAndUnassignedEmployees_assignsAfterHighestId() {
        Employee assignedBob = new EmployeeBuilder(BOB).withEmployeeId(5).build();
        Employee unassignedAlice = new EmployeeBuilder(ALICE).withoutEmployeeId().build();
        AddressBookStub newData = new AddressBookStub(List.of(unassignedAlice, assignedBob));

        addressBook.resetData(newData);

        assertEquals(new EmployeeId(6), addressBook.getEmployeeList().get(0).getEmployeeId().orElseThrow());
        assertEquals(new EmployeeId(5), addressBook.getEmployeeList().get(1).getEmployeeId().orElseThrow());
    }

    @Test
    public void resetData_withDuplicateEmployeeIds_throwsIllegalArgumentException() {
        Employee assignedAlice = new EmployeeBuilder(ALICE).withEmployeeId(1).build();
        Employee assignedBob = new EmployeeBuilder(BOB).withEmployeeId(1).build();
        AddressBookStub newData = new AddressBookStub(List.of(assignedAlice, assignedBob));

        assertThrows(IllegalArgumentException.class, () -> addressBook.resetData(newData));
    }

    @Test
    public void hasEmployee_nullEmployee_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> addressBook.hasEmployee(null));
    }

    @Test
    public void hasEmployee_employeeNotInAddressBook_returnsFalse() {
        assertFalse(addressBook.hasEmployee(ALICE));
    }

    @Test
    public void hasEmployee_employeeInAddressBook_returnsTrue() {
        addressBook.addEmployee(ALICE);
        assertTrue(addressBook.hasEmployee(ALICE));
    }

    @Test
    public void hasEmployee_employeeWithSameIdentityFieldsInAddressBook_returnsTrue() {
        addressBook.addEmployee(ALICE);
        Employee editedAlice = new EmployeeBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).withTags(VALID_TAG_HUSBAND)
                .build();
        assertTrue(addressBook.hasEmployee(editedAlice));
    }

    @Test
    public void addEmployee_unassignedEmployees_assignsSequentialIds() {
        Employee firstEmployee = new EmployeeBuilder(ALICE).withoutEmployeeId().build();
        Employee secondEmployee = new EmployeeBuilder(BENSON).withoutEmployeeId().build();
        addressBook.addEmployee(firstEmployee);
        addressBook.addEmployee(secondEmployee);

        assertEquals(new EmployeeId(1), addressBook.getEmployeeList().get(0).getEmployeeId().orElseThrow());
        assertEquals(new EmployeeId(2), addressBook.getEmployeeList().get(1).getEmployeeId().orElseThrow());
        assertTrue(firstEmployee.getEmployeeId().isEmpty());
    }

    @Test
    public void addEmployee_assignedEmployee_preservesIdAndAdvancesAllocator() {
        Employee assignedAlice = new EmployeeBuilder(ALICE).withEmployeeId(5).build();
        Employee unassignedBob = new EmployeeBuilder(BOB).withoutEmployeeId().build();

        addressBook.addEmployee(assignedAlice);
        addressBook.addEmployee(unassignedBob);

        assertEquals(new EmployeeId(5), addressBook.getEmployeeList().get(0).getEmployeeId().orElseThrow());
        assertEquals(new EmployeeId(6), addressBook.getEmployeeList().get(1).getEmployeeId().orElseThrow());
    }

    @Test
    public void addEmployee_duplicateEmployeeId_throwsIllegalArgumentException() {
        Employee assignedAlice = new EmployeeBuilder(ALICE).withEmployeeId(1).build();
        Employee assignedBob = new EmployeeBuilder(BOB).withEmployeeId(1).build();
        addressBook.addEmployee(assignedAlice);

        assertThrows(IllegalArgumentException.class, () -> addressBook.addEmployee(assignedBob));
    }

    @Test
    public void addEmployee_afterDeletion_doesNotReuseId() {
        addressBook.addEmployee(ALICE);
        addressBook.removeEmployee(addressBook.getEmployeeList().get(0));

        addressBook.addEmployee(new EmployeeBuilder(BOB).withoutEmployeeId().build());

        assertEquals(new EmployeeId(2), addressBook.getEmployeeList().get(0).getEmployeeId().orElseThrow());
    }

    @Test
    public void setEmployee_unassignedReplacement_preservesTargetId() {
        addressBook.addEmployee(ALICE);
        Employee target = addressBook.getEmployeeList().get(0);
        Employee editedEmployee = new EmployeeBuilder(ALICE).withoutEmployeeId()
                .withAddress(VALID_ADDRESS_BOB).build();

        addressBook.setEmployee(target, editedEmployee);

        assertEquals(target.getEmployeeId(), addressBook.getEmployeeList().get(0).getEmployeeId());
    }

    @Test
    public void setEmployee_differentEmployeeId_throwsIllegalArgumentException() {
        addressBook.addEmployee(ALICE);
        Employee target = addressBook.getEmployeeList().get(0);
        Employee editedEmployee = new EmployeeBuilder(ALICE).withEmployeeId(2).build();

        assertThrows(IllegalArgumentException.class, () -> addressBook.setEmployee(target, editedEmployee));
    }

    @Test
    public void copyConstructor_afterDeletion_preservesNextId() {
        addressBook.addEmployee(ALICE);
        addressBook.removeEmployee(addressBook.getEmployeeList().get(0));
        AddressBook copy = new AddressBook(addressBook);

        copy.addEmployee(new EmployeeBuilder(BOB).withoutEmployeeId().build());

        assertEquals(new EmployeeId(2), copy.getEmployeeList().get(0).getEmployeeId().orElseThrow());
    }

    @Test
    public void getEmployeeList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> addressBook.getEmployeeList().remove(0));
    }

    @Test
    public void toStringMethod() {
        String expected = AddressBook.class.getCanonicalName() + "{employees=" + addressBook.getEmployeeList() + "}";
        assertEquals(expected, addressBook.toString());
    }

    /**
     * A stub ReadOnlyAddressBook whose employees list can violate interface constraints.
     */
    private static class AddressBookStub implements ReadOnlyAddressBook {
        private final ObservableList<Employee> employees = FXCollections.observableArrayList();

        AddressBookStub(Collection<Employee> employees) {
            this.employees.setAll(employees);
        }

        @Override
        public long getNextEmployeeId() {
            return 1;
        }

        @Override
        public ObservableList<Employee> getEmployeeList() {
            return employees;
        }
    }

}
