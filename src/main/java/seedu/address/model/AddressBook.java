package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javafx.collections.ObservableList;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.employee.Employee;
import seedu.address.model.employee.EmployeeId;
import seedu.address.model.employee.UniqueEmployeeList;
import seedu.address.model.employee.exceptions.EmployeeNotFoundException;
import seedu.address.model.leave.Leave;
import seedu.address.model.leave.LeaveId;
import seedu.address.model.leave.LeavePeriod;
import seedu.address.model.leave.UniqueLeaveList;
import seedu.address.model.leave.exceptions.InsufficientLeaveException;
import seedu.address.model.leave.exceptions.LeaveIdExhaustedException;
import seedu.address.model.leave.exceptions.OverlappingLeaveException;

/**
 * Wraps all data at the address-book level.
 * Duplicates are not allowed (by .isSameEmployee comparison).
 */
public class AddressBook implements ReadOnlyAddressBook {

    private final UniqueEmployeeList employees = new UniqueEmployeeList();
    private final UniqueLeaveList leaves = new UniqueLeaveList();
    private long nextEmployeeId = 1;
    private long nextLeaveId = 1;

    public AddressBook() {}

    /**
     * Creates an address book snapshot with the given employees and next internal employee ID.
     */
    public AddressBook(List<Employee> employees, long nextEmployeeId) {
        replaceEmployees(employees, nextEmployeeId);
    }

    /**
     * Creates an address book snapshot with the given employees, leave records, and next identifiers.
     */
    public AddressBook(List<Employee> employees, long nextEmployeeId, List<Leave> leaves, long nextLeaveId) {
        replaceEmployees(employees, nextEmployeeId);
        this.nextLeaveId = setLeavesAndGetNextId(leaves, nextLeaveId);
    }

    /**
     * Creates an AddressBook using the Employees in the {@code toBeCopied}
     */
    public AddressBook(ReadOnlyAddressBook toBeCopied) {
        this();
        resetData(toBeCopied);
    }

    //// list overwrite operations

    /**
     * Replaces the contents of the employee list with {@code employees}.
     * {@code employees} must not contain duplicate employees.
     */
    public void setEmployees(List<Employee> employees) {
        replaceEmployees(employees, nextEmployeeId);
        removeOrphanedLeaves();
    }

    /**
     * Replaces the contents of the leave list with {@code leaves}.
     */
    public void setLeaves(List<Leave> leaves) {
        nextLeaveId = setLeavesAndGetNextId(leaves, nextLeaveId);
    }

    /**
     * Resets the existing data of this {@code AddressBook} with {@code newData}.
     */
    public void resetData(ReadOnlyAddressBook newData) {
        requireNonNull(newData);

        AddressBook replacement = new AddressBook(newData.getEmployeeList(), newData.getNextEmployeeId(),
                newData.getLeaveList(), newData.getNextLeaveId());
        employees.setEmployees(replacement.employees);
        leaves.setLeaves(replacement.leaves);
        nextEmployeeId = replacement.nextEmployeeId;
        nextLeaveId = replacement.nextLeaveId;
    }

    //// employee-level operations

    /**
     * Returns true if an employee with the same identity as {@code employee} exists in the address book.
     */
    public boolean hasEmployee(Employee employee) {
        requireNonNull(employee);
        return employees.contains(employee);
    }

    /**
     * Adds an employee to the address book.
     * The employee must not already exist in the address book.
     */
    public void addEmployee(Employee employee) {
        requireNonNull(employee);

        Employee employeeToAdd = employee;
        long nextIdAfterAdd = nextEmployeeId;
        if (employee.getEmployeeId().isEmpty()) {
            employeeToAdd = employee.withEmployeeId(createNextEmployeeId());
            nextIdAfterAdd++;
        } else {
            EmployeeId employeeId = employee.getEmployeeId().orElseThrow();
            if (hasEmployeeId(employeeId)) {
                throw new IllegalArgumentException("Employee ID is already in use.");
            }
            nextIdAfterAdd = Math.max(nextIdAfterAdd, (long) employeeId.getValue() + 1);
        }

        employees.add(employeeToAdd);
        nextEmployeeId = nextIdAfterAdd;
    }

    /**
     * Replaces the given employee {@code target} in the list with {@code editedEmployee}.
     * {@code target} must exist in the address book.
     * The employee identity of {@code editedEmployee} must not be the same as another existing employee in
     * the address book.
     */
    public void setEmployee(Employee target, Employee editedEmployee) {
        requireAllNonNull(target, editedEmployee);

        EmployeeId targetId = target.getEmployeeId()
                .orElseThrow(() -> new IllegalStateException("Stored employee has no internal ID."));
        Employee employeeToSet = editedEmployee.getEmployeeId()
                .map(editedId -> {
                    if (!editedId.equals(targetId)) {
                        throw new IllegalArgumentException("An employee's internal ID cannot be changed.");
                    }
                    return editedEmployee;
                })
                .orElseGet(() -> editedEmployee.withEmployeeId(targetId));

        employees.setEmployee(target, employeeToSet);
    }

    /**
     * Removes {@code key} from this {@code AddressBook}.
     * {@code key} must exist in the address book.
     */
    public void removeEmployee(Employee key) {
        EmployeeId employeeId = key.getEmployeeId()
                .orElseThrow(() -> new IllegalStateException("Stored employee has no internal ID."));
        employees.remove(key);
        leaves.removeAllFor(employeeId);
    }

    //// leave-level operations

    /**
     * Records leave for an existing employee and assigns the next leave ID.
     */
    public Leave addLeave(EmployeeId employeeId, LeavePeriod leavePeriod) {
        requireAllNonNull(employeeId, leavePeriod);
        Employee employee = findEmployeeById(employeeId);
        if (leaves.hasOverlappingLeave(employeeId, leavePeriod)) {
            throw new OverlappingLeaveException();
        }

        int requestedDays = leavePeriod.getWorkingDayCount();
        int remainingDays = calculateRemainingLeave(employee, leavePeriod.getYear());
        if (requestedDays > remainingDays) {
            throw new InsufficientLeaveException(requestedDays, remainingDays);
        }
        if (nextLeaveId > Integer.MAX_VALUE) {
            throw new LeaveIdExhaustedException();
        }

        Leave leave = new Leave(new LeaveId((int) nextLeaveId), employeeId, leavePeriod);
        leaves.add(leave);
        nextLeaveId++;
        return leave;
    }

    /** Returns the employee's remaining annual leave entitlement for {@code year}. */
    public int getRemainingLeave(EmployeeId employeeId, int year) {
        requireNonNull(employeeId);
        return calculateRemainingLeave(findEmployeeById(employeeId), year);
    }

    //// util methods

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("employees", employees)
                .add("leaves", leaves)
                .toString();
    }

    @Override
    public ObservableList<Employee> getEmployeeList() {
        return employees.asUnmodifiableObservableList();
    }

    @Override
    public long getNextEmployeeId() {
        return nextEmployeeId;
    }

    @Override
    public long getNextLeaveId() {
        return nextLeaveId;
    }

    @Override
    public ObservableList<Leave> getLeaveList() {
        return leaves.asUnmodifiableObservableList();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddressBook otherAddressBook)) {
            return false;
        }

        return employees.equals(otherAddressBook.employees)
                && nextEmployeeId == otherAddressBook.nextEmployeeId
                && leaves.equals(otherAddressBook.leaves)
                && nextLeaveId == otherAddressBook.nextLeaveId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(employees, nextEmployeeId, leaves, nextLeaveId);
    }

    /**
     * Replaces all employees, preserving existing IDs and assigning IDs after the highest reserved value.
     */
    private void replaceEmployees(List<Employee> employees, long startingEmployeeId) {
        requireAllNonNull(employees);
        if (startingEmployeeId < 1 || startingEmployeeId > (long) Integer.MAX_VALUE + 1) {
            throw new IllegalArgumentException("Invalid next employee ID.");
        }

        Set<EmployeeId> assignedIds = new HashSet<>();
        long nextIdAfterSet = startingEmployeeId;
        for (Employee employee : employees) {
            employee.getEmployeeId().ifPresent(employeeId -> {
                if (!assignedIds.add(employeeId)) {
                    throw new IllegalArgumentException("Employee ID is already in use.");
                }
            });
            if (employee.getEmployeeId().isPresent()) {
                nextIdAfterSet = Math.max(nextIdAfterSet,
                        (long) employee.getEmployeeId().orElseThrow().getValue() + 1);
            }
        }

        List<Employee> employeesWithIds = new ArrayList<>();
        for (Employee employee : employees) {
            if (employee.getEmployeeId().isPresent()) {
                employeesWithIds.add(employee);
                continue;
            }
            if (nextIdAfterSet > Integer.MAX_VALUE) {
                throw new IllegalStateException("No employee IDs are available.");
            }
            employeesWithIds.add(employee.withEmployeeId(new EmployeeId((int) nextIdAfterSet)));
            nextIdAfterSet++;
        }

        this.employees.setEmployees(employeesWithIds);
        nextEmployeeId = nextIdAfterSet;
    }

    private EmployeeId createNextEmployeeId() {
        if (nextEmployeeId > Integer.MAX_VALUE) {
            throw new IllegalStateException("No employee IDs are available.");
        }
        return new EmployeeId((int) nextEmployeeId);
    }

    private boolean hasEmployeeId(EmployeeId employeeId) {
        return employees.asUnmodifiableObservableList().stream()
                .flatMap(employee -> employee.getEmployeeId().stream())
                .anyMatch(employeeId::equals);
    }

    private Employee findEmployeeById(EmployeeId employeeId) {
        return employees.asUnmodifiableObservableList().stream()
                .filter(employee -> employee.getEmployeeId().orElseThrow().equals(employeeId))
                .findFirst()
                .orElseThrow(EmployeeNotFoundException::new);
    }

    private int calculateRemainingLeave(Employee employee, int year) {
        EmployeeId employeeId = employee.getEmployeeId().orElseThrow();
        int usedDays = leaves.getWorkingDaysUsed(employeeId, year);
        return Math.max(0, employee.getLeaveEntitlement().value - usedDays);
    }

    /**
     * Replaces the leave list and returns an identifier greater than every leave identifier in the replacement.
     */
    private long setLeavesAndGetNextId(List<Leave> leaves, long startingLeaveId) {
        requireAllNonNull(leaves);
        if (startingLeaveId < 1 || startingLeaveId > (long) Integer.MAX_VALUE + 1) {
            throw new IllegalArgumentException("Invalid next leave ID.");
        }

        long nextIdAfterSet = startingLeaveId;
        UniqueLeaveList validatedLeaves = new UniqueLeaveList();
        for (Leave leave : leaves) {
            if (!hasEmployeeId(leave.getEmployeeId())) {
                throw new EmployeeNotFoundException();
            }
            if (validatedLeaves.hasOverlappingLeave(leave.getEmployeeId(), leave.getPeriod())) {
                throw new OverlappingLeaveException();
            }
            validatedLeaves.add(leave);
            nextIdAfterSet = Math.max(nextIdAfterSet, (long) leave.getLeaveId().value + 1);
        }

        this.leaves.setLeaves(validatedLeaves);
        return nextIdAfterSet;
    }

    private void removeOrphanedLeaves() {
        List<Leave> retainedLeaves = leaves.asUnmodifiableObservableList().stream()
                .filter(leave -> hasEmployeeId(leave.getEmployeeId()))
                .toList();
        leaves.setLeaves(retainedLeaves);
    }
}
