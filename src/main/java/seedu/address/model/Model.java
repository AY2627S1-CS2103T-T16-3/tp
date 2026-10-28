package seedu.address.model;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.model.employee.Employee;
import seedu.address.model.employee.EmployeeId;
import seedu.address.model.leave.Leave;
import seedu.address.model.leave.LeaveId;
import seedu.address.model.leave.LeavePeriod;

/**
 * The API of the Model component.
 */
public interface Model {
    /** {@code Predicate} that always evaluates to true */
    Predicate<Employee> PREDICATE_SHOW_ALL_EMPLOYEES = unused -> true;

    /**
     * Returns the user prefs.
     */
    ReadOnlyUserPrefs getUserPrefs();

    /**
     * Returns the user prefs' GUI settings.
     */
    GuiSettings getGuiSettings();

    /**
     * Sets the user prefs' GUI settings.
     */
    void setGuiSettings(GuiSettings guiSettings);

    /**
     * Replaces address book data with the data in {@code addressBook}.
     */
    void setAddressBook(ReadOnlyAddressBook addressBook);

    /** Returns the AddressBook */
    ReadOnlyAddressBook getAddressBook();

    /**
     * Returns true if an employee with the same identity as {@code employee} exists in the address book.
     */
    boolean hasEmployee(Employee employee);

    /**
     * Deletes the given employee.
     * The employee must exist in the address book.
     */
    void deleteEmployee(Employee target);

    /**
     * Adds the given employee.
     * {@code employee} must not already exist in the address book.
     */
    void addEmployee(Employee employee);

    /**
     * Replaces the given employee {@code target} with {@code editedEmployee}.
     * {@code target} must exist in the address book.
     * The employee identity of {@code editedEmployee} must not be the same as another existing employee in
     * the address book.
     */
    void setEmployee(Employee target, Employee editedEmployee);

    /** Records leave for an existing employee and returns the assigned record. */
    Leave addLeave(EmployeeId employeeId, LeavePeriod leavePeriod);

    /** Deletes and returns the leave record identified by {@code leaveId}. */
    Leave deleteLeave(LeaveId leaveId);

    /** Returns the employee's remaining annual leave entitlement for the given year. */
    int getRemainingLeave(EmployeeId employeeId, int year);

    /** Returns an unmodifiable view of the filtered employee list */
    ObservableList<Employee> getFilteredEmployeeList();

    /**
     * Returns an unmodifiable snapshot of all employees in the current display sort order, ignoring the filter.
     * Without an active sort, employees follow their stored order. Equal sort values retain stored order.
     */
    List<Employee> getAllEmployeesInDisplayOrder();

    /**
     * Updates the filter of the filtered employee list to filter by the given {@code predicate}.
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredEmployeeList(Predicate<Employee> predicate);

    /**
     * Sorts the displayed employee list, preserving the active filter and stored employee order.
     * The sort remains active as the displayed list changes.
     * @throws NullPointerException if {@code comparator} is null.
     */
    void sortFilteredEmployeeList(Comparator<Employee> comparator);
}
