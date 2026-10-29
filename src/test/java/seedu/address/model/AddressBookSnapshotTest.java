package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.employee.Employee;
import seedu.address.model.employee.EmployeeId;
import seedu.address.model.leave.Leave;
import seedu.address.model.leave.LeaveId;
import seedu.address.model.leave.LeavePeriod;
import seedu.address.testutil.EmployeeBuilder;

/** Tests the trusted copying used by model snapshots. */
public class AddressBookSnapshotTest {
    @Test
    public void copyForSnapshot_mutateEitherBook_keepsCollectionsIndependent() {
        Employee employee = new EmployeeBuilder().withEmployeeId(7).build();
        Leave leave = new Leave(new LeaveId(9), new EmployeeId(7), new LeavePeriod(
                LocalDate.of(2026, 10, 5), LocalDate.of(2026, 10, 5)));
        AddressBook original = new AddressBook(List.of(employee), 20, List.of(leave), 30);

        AddressBook snapshot = original.copyForSnapshot();

        assertEquals(original, snapshot);
        assertSame(employee, snapshot.getEmployeeList().get(0));
        assertSame(leave, snapshot.getLeaveList().get(0));
        original.removeEmployee(employee);
        assertEquals(List.of(employee), snapshot.getEmployeeList());
        assertEquals(List.of(leave), snapshot.getLeaveList());

        original.restoreValidatedData(snapshot);
        snapshot.removeLeave(leave.getLeaveId());
        snapshot.removeEmployee(employee);
        assertEquals(List.of(employee), original.getEmployeeList());
        assertEquals(List.of(leave), original.getLeaveList());
        assertEquals(20, original.getNextEmployeeId());
        assertEquals(30, original.getNextLeaveId());
    }

    @Test
    public void restoreValidatedData_changedBook_preservesBindingsAndExhaustedCounters() {
        Employee employee = new EmployeeBuilder().withEmployeeId(7).build();
        Leave leave = new Leave(new LeaveId(9), new EmployeeId(7), new LeavePeriod(
                LocalDate.of(2026, 10, 5), LocalDate.of(2026, 10, 5)));
        long exhaustedId = (long) Integer.MAX_VALUE + 1;
        AddressBook original = new AddressBook(List.of(employee), exhaustedId, List.of(leave), exhaustedId);
        AddressBook snapshot = original.copyForSnapshot();
        var employees = original.getEmployeeList();
        var leaves = original.getLeaveList();
        original.resetData(new AddressBook());

        original.restoreValidatedData(snapshot);

        assertSame(employees, original.getEmployeeList());
        assertSame(leaves, original.getLeaveList());
        assertEquals(List.of(employee), employees);
        assertEquals(List.of(leave), leaves);
        assertEquals(exhaustedId, original.getNextEmployeeId());
        assertEquals(exhaustedId, original.getNextLeaveId());
        original.removeEmployee(employee);
        original.restoreValidatedData(snapshot);
        assertEquals(snapshot, original);
    }
}
