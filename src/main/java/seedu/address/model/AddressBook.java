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

/**
 * Wraps all data at the address-book level.
 * Duplicates are not allowed (by .isSameEmployee comparison).
 */
public class AddressBook implements ReadOnlyAddressBook {

    private final UniqueEmployeeList employees = new UniqueEmployeeList();
    private long nextEmployeeId = 1;

    public AddressBook() {}

    /**
     * Creates an address book snapshot with the given employees and next internal employee ID.
     */
    public AddressBook(List<Employee> employees, long nextEmployeeId) {
        replaceEmployees(employees, nextEmployeeId);
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
    }

    /**
     * Resets the existing data of this {@code AddressBook} with {@code newData}.
     */
    public void resetData(ReadOnlyAddressBook newData) {
        requireNonNull(newData);

        replaceEmployees(newData.getEmployeeList(), newData.getNextEmployeeId());
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
    public long getNextEmployeeId() {
        return nextEmployeeId;
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
                && nextEmployeeId == otherAddressBook.nextEmployeeId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(employees, nextEmployeeId);
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
}
