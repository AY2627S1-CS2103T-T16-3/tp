package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalEmployees.ALICE;
import static seedu.address.testutil.TypicalEmployees.BENSON;
import static seedu.address.testutil.TypicalEmployees.BOB;
import static seedu.address.testutil.TypicalEmployees.getTypicalAddressBook;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.employee.Employee;
import seedu.address.model.employee.EmployeeId;
import seedu.address.model.employee.exceptions.DuplicateEmployeeException;
import seedu.address.model.employee.exceptions.EmployeeNotFoundException;
import seedu.address.model.leave.Leave;
import seedu.address.model.leave.LeaveId;
import seedu.address.model.leave.LeavePeriod;
import seedu.address.model.leave.exceptions.InsufficientLeaveException;
import seedu.address.model.leave.exceptions.LeaveIdExhaustedException;
import seedu.address.model.leave.exceptions.OverlappingLeaveException;
import seedu.address.testutil.EmployeeBuilder;

public class AddressBookTest {

    private static final LeavePeriod MONDAY_TO_WEDNESDAY = new LeavePeriod(
            LocalDate.of(2026, 10, 5), LocalDate.of(2026, 10, 7));
    private static final LeavePeriod THURSDAY_TO_FRIDAY = new LeavePeriod(
            LocalDate.of(2026, 10, 8), LocalDate.of(2026, 10, 9));

    private final AddressBook addressBook = new AddressBook();

    @Test
    public void constructor() {
        assertEquals(List.of(), addressBook.getEmployeeList());
        assertEquals(List.of(), addressBook.getLeaveList());
        assertEquals(1, addressBook.getNextLeaveId());
    }

    @Test
    public void constructor_invalidNextEmployeeId_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new AddressBook(List.of(), 0));
        assertThrows(IllegalArgumentException.class, () ->
                new AddressBook(List.of(), (long) Integer.MAX_VALUE + 2));
    }

    @Test
    public void constructor_unassignedEmployeeAfterMaximumId_throwsIllegalStateException() {
        Employee unassignedEmployee = new EmployeeBuilder().build();

        assertThrows(IllegalStateException.class, () ->
                new AddressBook(List.of(unassignedEmployee), (long) Integer.MAX_VALUE + 1));
    }

    @Test
    public void constructor_invalidNextLeaveId_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                new AddressBook(List.of(), 1, List.of(), 0));
        assertThrows(IllegalArgumentException.class, () ->
                new AddressBook(List.of(), 1, List.of(), (long) Integer.MAX_VALUE + 2));
    }

    @Test
    public void setEmployees_unassignedEmployee_assignsNextId() {
        addressBook.addEmployee(ALICE);
        Employee unassignedBob = new EmployeeBuilder(BOB).withoutEmployeeId().build();

        addressBook.setEmployees(List.of(unassignedBob));

        assertEquals(new EmployeeId(2), addressBook.getEmployeeList().get(0).getEmployeeId().orElseThrow());
    }

    @Test
    public void setEmployees_employeeRemoved_removesOnlyOrphanedLeaves() {
        addressBook.addEmployee(ALICE);
        addressBook.addEmployee(BENSON);
        Leave aliceLeave = addressBook.addLeave(new EmployeeId(1), MONDAY_TO_WEDNESDAY);
        Leave bensonLeave = addressBook.addLeave(new EmployeeId(2), MONDAY_TO_WEDNESDAY);

        addressBook.setEmployees(List.of(BENSON));

        assertEquals(List.of(bensonLeave), addressBook.getLeaveList());
        assertFalse(addressBook.getLeaveList().contains(aliceLeave));
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
    public void addEmployee_noAvailableId_throwsIllegalStateException() {
        AddressBook exhaustedAddressBook = new AddressBook(List.of(), (long) Integer.MAX_VALUE + 1);

        assertThrows(IllegalStateException.class, () ->
                exhaustedAddressBook.addEmployee(new EmployeeBuilder().build()));
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
    public void setLeaves_validLeave_replacesListAndAdvancesNextId() {
        addressBook.addEmployee(ALICE);
        Leave leave = new Leave(new LeaveId(5), new EmployeeId(1), MONDAY_TO_WEDNESDAY);

        addressBook.setLeaves(List.of(leave));

        assertEquals(List.of(leave), addressBook.getLeaveList());
        assertEquals(6, addressBook.getNextLeaveId());
    }

    @Test
    public void setLeaves_unknownEmployee_throwsEmployeeNotFoundException() {
        Leave leave = new Leave(new LeaveId(1), new EmployeeId(1), MONDAY_TO_WEDNESDAY);

        assertThrows(EmployeeNotFoundException.class, () -> addressBook.setLeaves(List.of(leave)));
    }

    @Test
    public void setLeaves_overlappingPeriods_throwsOverlappingLeaveExceptionWithoutChangingData() {
        addressBook.addEmployee(ALICE);
        Leave existingLeave = new Leave(new LeaveId(1), new EmployeeId(1), MONDAY_TO_WEDNESDAY);
        addressBook.setLeaves(List.of(existingLeave));
        Leave overlappingLeave = new Leave(new LeaveId(2), new EmployeeId(1), new LeavePeriod(
                LocalDate.of(2026, 10, 7), LocalDate.of(2026, 10, 8)));

        assertThrows(OverlappingLeaveException.class, () ->
                addressBook.setLeaves(List.of(existingLeave, overlappingLeave)));
        assertEquals(List.of(existingLeave), addressBook.getLeaveList());
    }

    @Test
    public void removeEmployee_employeeHasLeave_removesAssociatedLeaveWithoutReusingId() {
        addressBook.addEmployee(ALICE);
        Leave leave = new Leave(new LeaveId(1), new EmployeeId(1), MONDAY_TO_WEDNESDAY);
        addressBook.setLeaves(List.of(leave));

        addressBook.removeEmployee(ALICE);

        assertTrue(addressBook.getLeaveList().isEmpty());
        assertEquals(2, addressBook.getNextLeaveId());
    }

    @Test
    public void copyConstructor_preservesLeavesAndNextLeaveId() {
        addressBook.addEmployee(ALICE);
        Leave leave = new Leave(new LeaveId(5), new EmployeeId(1), MONDAY_TO_WEDNESDAY);
        addressBook.setLeaves(List.of(leave));

        AddressBook copy = new AddressBook(addressBook);

        assertEquals(List.of(leave), copy.getLeaveList());
        assertEquals(6, copy.getNextLeaveId());
    }

    @Test
    public void addLeave_validPeriod_assignsIdAndDeductsWorkingDays() {
        addressBook.addEmployee(ALICE);

        Leave addedLeave = addressBook.addLeave(new EmployeeId(1), MONDAY_TO_WEDNESDAY);

        assertEquals(new LeaveId(1), addedLeave.getLeaveId());
        assertEquals(List.of(addedLeave), addressBook.getLeaveList());
        assertEquals(2, addressBook.getNextLeaveId());
        assertEquals(ALICE.getLeaveEntitlement().value - 3,
                addressBook.getRemainingLeave(new EmployeeId(1), 2026));
    }

    @Test
    public void addLeave_unknownEmployee_throwsEmployeeNotFoundException() {
        assertThrows(EmployeeNotFoundException.class, () ->
                addressBook.addLeave(new EmployeeId(1), MONDAY_TO_WEDNESDAY));
        assertEquals(1, addressBook.getNextLeaveId());
    }

    @Test
    public void addLeave_overlappingPeriod_throwsOverlappingLeaveException() {
        addressBook.addEmployee(ALICE);
        addressBook.addLeave(new EmployeeId(1), MONDAY_TO_WEDNESDAY);
        LeavePeriod overlappingPeriod = new LeavePeriod(
                LocalDate.of(2026, 10, 7), LocalDate.of(2026, 10, 8));

        assertThrows(OverlappingLeaveException.class, () ->
                addressBook.addLeave(new EmployeeId(1), overlappingPeriod));
        assertEquals(2, addressBook.getNextLeaveId());
    }

    @Test
    public void addLeave_requestExceedsEntitlement_throwsInsufficientLeaveException() {
        addressBook.addEmployee(new EmployeeBuilder().withLeaveEntitlement(2).build());
        EmployeeId employeeId = addressBook.getEmployeeList().get(0).getEmployeeId().orElseThrow();

        InsufficientLeaveException exception = org.junit.jupiter.api.Assertions.assertThrows(
                InsufficientLeaveException.class, () -> addressBook.addLeave(employeeId, MONDAY_TO_WEDNESDAY));

        assertEquals(3, exception.getRequestedDays());
        assertEquals(2, exception.getRemainingDays());
        assertTrue(addressBook.getLeaveList().isEmpty());
    }

    @Test
    public void addLeave_cumulativeRequestsExceedEntitlement_throwsInsufficientLeaveException() {
        addressBook.addEmployee(new EmployeeBuilder().withLeaveEntitlement(4).build());
        EmployeeId employeeId = addressBook.getEmployeeList().get(0).getEmployeeId().orElseThrow();
        addressBook.addLeave(employeeId, MONDAY_TO_WEDNESDAY);

        InsufficientLeaveException exception = org.junit.jupiter.api.Assertions.assertThrows(
                InsufficientLeaveException.class, () -> addressBook.addLeave(employeeId, THURSDAY_TO_FRIDAY));

        assertEquals(2, exception.getRequestedDays());
        assertEquals(1, exception.getRemainingDays());
        assertEquals(1, addressBook.getLeaveList().size());
    }

    @Test
    public void addLeave_newCalendarYear_usesAnnualEntitlementAgain() {
        addressBook.addEmployee(new EmployeeBuilder().withLeaveEntitlement(3).build());
        EmployeeId employeeId = addressBook.getEmployeeList().get(0).getEmployeeId().orElseThrow();
        addressBook.addLeave(employeeId, MONDAY_TO_WEDNESDAY);
        LeavePeriod followingYear = new LeavePeriod(
                LocalDate.of(2027, 10, 4), LocalDate.of(2027, 10, 6));

        Leave addedLeave = addressBook.addLeave(employeeId, followingYear);

        assertEquals(new LeaveId(2), addedLeave.getLeaveId());
    }

    @Test
    public void addLeave_noAvailableId_throwsLeaveIdExhaustedException() {
        Leave maximumIdLeave = new Leave(new LeaveId(Integer.MAX_VALUE), new EmployeeId(1), MONDAY_TO_WEDNESDAY);
        AddressBook exhaustedAddressBook = new AddressBook(List.of(ALICE), 2,
                List.of(maximumIdLeave), (long) Integer.MAX_VALUE + 1);

        assertThrows(LeaveIdExhaustedException.class, () ->
                exhaustedAddressBook.addLeave(new EmployeeId(1), THURSDAY_TO_FRIDAY));
    }

    @Test
    public void getRemainingLeave_entitlementReducedBelowUsed_returnsZero() {
        addressBook.addEmployee(ALICE);
        addressBook.addLeave(new EmployeeId(1), MONDAY_TO_WEDNESDAY);
        Employee reducedEntitlement = new EmployeeBuilder(ALICE).withLeaveEntitlement(2).build();
        addressBook.setEmployee(ALICE, reducedEntitlement);

        assertEquals(0, addressBook.getRemainingLeave(new EmployeeId(1), 2026));
    }

    @Test
    public void getEmployeeList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> addressBook.getEmployeeList().remove(0));
    }

    @Test
    public void getLeaveList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> addressBook.getLeaveList().remove(0));
    }

    @Test
    public void toStringMethod() {
        String expected = AddressBook.class.getCanonicalName() + "{employees=" + addressBook.getEmployeeList()
                + ", leaves=" + addressBook.getLeaveList() + "}";
        assertEquals(expected, addressBook.toString());
    }

    @Test
    public void equalsAndHashCode() {
        AddressBook addressBookCopy = new AddressBook(addressBook);
        AddressBook addressBookWithDifferentNextId = new AddressBook(List.of(), 2);

        assertTrue(addressBook.equals(addressBook));
        assertEquals(addressBook, addressBookCopy);
        assertEquals(addressBook.hashCode(), addressBookCopy.hashCode());
        assertNotEquals(addressBook, addressBookWithDifferentNextId);
        assertNotEquals(addressBook, null);
        assertNotEquals(addressBook, "address book");
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

        @Override
        public long getNextLeaveId() {
            return 1;
        }

        @Override
        public ObservableList<Leave> getLeaveList() {
            return FXCollections.emptyObservableList();
        }
    }

}
