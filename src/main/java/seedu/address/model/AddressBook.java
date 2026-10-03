package seedu.address.model;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.collections.ObservableList;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.employee.Employee;
import seedu.address.model.employee.EmployeeId;
import seedu.address.model.employee.UniqueEmployeeList;
import seedu.address.model.employee.exceptions.DuplicateEmployeeException;
import seedu.address.model.employee.exceptions.EmployeeIdExhaustedException;

/**
 * Wraps all data at the address-book level.
 * Duplicates are not allowed (by .isSameEmployee comparison).
 */
public class AddressBook implements ReadOnlyAddressBook {

    private final UniqueEmployeeList employees = new UniqueEmployeeList();

    public AddressBook() {}

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
        requireNonNull(employees);
        Set<EmployeeId> usedIds = new HashSet<>();
        // Reserve all imported IDs before allocating any, even if an ID-less record appears first.
        for (Employee employee : employees) {
            employee.getEmployeeId().ifPresent(id -> {
                if (!usedIds.add(id)) {
                    throw new DuplicateEmployeeException();
                }
            });
        }
        List<Employee> assignedEmployees = new ArrayList<>();
        for (Employee employee : employees) {
            Employee assigned = employee;
            if (employee.getEmployeeId().isEmpty()) {
                EmployeeId id = findAvailableId(usedIds);
                usedIds.add(id);
                assigned = employee.withEmployeeId(id);
            }
            assignedEmployees.add(assigned);
        }
        // UniqueEmployeeList validates the complete replacement before changing the live list.
        this.employees.setEmployees(assignedEmployees);
    }

    /**
     * Resets the existing data of this {@code AddressBook} with {@code newData}.
     */
    public void resetData(ReadOnlyAddressBook newData) {
        requireNonNull(newData);

        setEmployees(newData.getEmployeeList());
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
     * Returns the stored employee with its assigned ID; the input object is unchanged.
     */
    public Employee addEmployee(Employee employee) {
        requireNonNull(employee);
        Employee assigned = employee;
        if (employee.getEmployeeId().isEmpty()) {
            Set<EmployeeId> usedIds = new HashSet<>();
            employees.forEach(existing -> usedIds.add(existing.getEmployeeId().orElseThrow()));
            assigned = employee.withEmployeeId(findAvailableId(usedIds));
        }
        employees.add(assigned);
        return assigned;
    }

    /**
     * Returns the smallest unused ID. Deleted IDs can be reused; list filtering has no effect.
     */
    private static EmployeeId findAvailableId(Set<EmployeeId> usedIds) {
        for (int candidate = 1; candidate <= EmployeeId.MAX_VALUE; candidate++) {
            EmployeeId id = new EmployeeId(Integer.toString(candidate));
            if (!usedIds.contains(id)) {
                return id;
            }
        }
        throw new EmployeeIdExhaustedException();
    }

    /**
     * Replaces the given employee {@code target} in the list with {@code editedEmployee}.
     * {@code target} must exist in the address book.
     * The employee identity of {@code editedEmployee} must not be the same as another existing employee in
     * the address book.
     */
    public void setEmployee(Employee target, Employee editedEmployee) {
        requireNonNull(target);
        requireNonNull(editedEmployee);

        EmployeeId targetId = target.getEmployeeId().orElseThrow();
        if (editedEmployee.getEmployeeId().isPresent()
                && !editedEmployee.getEmployeeId().get().equals(targetId)) {
            throw new IllegalArgumentException("An employee's ID cannot be changed.");
        }
        employees.setEmployee(target, editedEmployee.withEmployeeId(targetId));
    }

    /**
     * Removes {@code key} from this {@code AddressBook}.
     * {@code key} must exist in the address book.
     */
    public void removeEmployee(Employee key) {
        employees.remove(key);
    }

    //// util methods

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("employees", employees)
                .toString();
    }

    @Override
    public ObservableList<Employee> getEmployeeList() {
        return employees.asUnmodifiableObservableList();
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

        return employees.equals(otherAddressBook.employees);
    }

    @Override
    public int hashCode() {
        return employees.hashCode();
    }
}
