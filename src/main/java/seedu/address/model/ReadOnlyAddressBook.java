package seedu.address.model;

import javafx.collections.ObservableList;
import seedu.address.model.employee.Employee;
import seedu.address.model.leave.Leave;

/**
 * Unmodifiable view of an address book
 */
public interface ReadOnlyAddressBook {

    /** Returns the next internal employee ID reserved by this address book. */
    long getNextEmployeeId();

    /** Returns the next leave ID reserved by this address book. */
    long getNextLeaveId();

    /**
     * Returns an unmodifiable view of the employees list.
     * This list will not contain any duplicate employees.
     */
    ObservableList<Employee> getEmployeeList();

    /** Returns an unmodifiable view of the centrally stored leave records. */
    ObservableList<Leave> getLeaveList();

}
