package seedu.address.model.employee;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.leave.Leave;
import seedu.address.model.leave.LeaveId;
import seedu.address.model.leave.LeavePeriod;
import seedu.address.testutil.EmployeeBuilder;

public class EmployeeSummaryLeaveTest {
    private static final Employee ALICE = new EmployeeBuilder().withName("Alice").withEmployeeId(1).build();
    private static final Employee BOB = new EmployeeBuilder().withName("Bob").withEmployeeId(2).build();
    private static final List<Leave> LEAVES = List.of(
            leave(1, 1, "2025-01-06", "2025-01-06"),
            leave(2, 1, "2026-01-05", "2026-01-09"),
            leave(3, 2, "2027-01-04", "2027-01-05"));

    @Test
    public void fromEmployees_mixedPeriods_countsRecordsAcrossAllYearsInsteadOfWorkingDays() {
        EmployeeSummary summary = EmployeeSummary.fromEmployees(List.of(ALICE, BOB), LEAVES);
        assertEquals(3, summary.getTotalLeaveRecords());
    }

    @Test
    public void fromEmployees_selectedEmployee_usesStableIdAfterNameChange() {
        Employee renamedAlice = new EmployeeBuilder(ALICE).withName("Alicia").build();
        assertEquals(2, EmployeeSummary.fromEmployees(List.of(renamedAlice), LEAVES).getTotalLeaveRecords());
        assertEquals(1, EmployeeSummary.fromEmployees(List.of(BOB), LEAVES).getTotalLeaveRecords());
    }

    @Test
    public void fromEmployees_noMatchingRecords_returnsZero() {
        assertEquals(0, EmployeeSummary.fromEmployees(List.of(), LEAVES).getTotalLeaveRecords());
        assertEquals(0, EmployeeSummary.fromEmployees(List.of(ALICE), List.of()).getTotalLeaveRecords());
        assertEquals(0, EmployeeSummary.fromEmployees(List.of(ALICE)).getTotalLeaveRecords());
        Employee unassigned = new EmployeeBuilder().build();
        assertEquals(0, EmployeeSummary.fromEmployees(List.of(unassigned), LEAVES).getTotalLeaveRecords());
    }

    @Test
    public void fromEmployees_mutableInputs_capturesCountWithoutChangingOrRetainingLists() {
        List<Employee> employees = new ArrayList<>(List.of(ALICE));
        List<Leave> leaves = new ArrayList<>(LEAVES);
        EmployeeSummary summary = EmployeeSummary.fromEmployees(employees, leaves);
        assertEquals(List.of(ALICE), employees);
        assertEquals(LEAVES, leaves);

        employees.clear();
        leaves.clear();
        assertEquals(2, summary.getTotalLeaveRecords());
    }

    @Test
    public void fromEmployees_nullInput_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> EmployeeSummary.fromEmployees(null, LEAVES));
        assertThrows(NullPointerException.class, () -> EmployeeSummary.fromEmployees(List.of(ALICE), null));
    }

    private static Leave leave(int id, int employeeId, String start, String end) {
        return new Leave(new LeaveId(id), new EmployeeId(employeeId),
                new LeavePeriod(LocalDate.parse(start), LocalDate.parse(end)));
    }
}
