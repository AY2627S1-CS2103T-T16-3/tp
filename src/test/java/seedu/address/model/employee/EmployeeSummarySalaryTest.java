package seedu.address.model.employee;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.EmployeeBuilder;

public class EmployeeSummarySalaryTest {
    @Test
    public void fromEmployees_empty_salaryTotalIsZeroAndOtherStatisticsAreAbsent() {
        EmployeeSummary summary = EmployeeSummary.fromEmployees(List.of());
        assertEquals(0L, summary.getTotalSalary());
        assertTrue(summary.getAverageSalary().isEmpty());
        assertTrue(summary.getLowestSalary().isEmpty());
        assertTrue(summary.getHighestSalary().isEmpty());
    }

    @Test
    public void fromEmployees_singleEmployee_includesZeroAndMaximumSalary() {
        for (String salary : List.of("0", "50000")) {
            EmployeeSummary summary = EmployeeSummary.fromEmployees(List.of(employee(salary)));
            assertEquals(Long.parseLong(salary), summary.getTotalSalary());
            assertEquals(new BigDecimal(salary).setScale(2), summary.getAverageSalary().orElseThrow());
            assertEquals(Integer.parseInt(salary), summary.getLowestSalary().orElseThrow());
            assertEquals(Integer.parseInt(salary), summary.getHighestSalary().orElseThrow());
        }
    }

    @Test
    public void fromEmployees_mixedSalaries_countsZerosAndRoundsAverage() {
        EmployeeSummary summary = EmployeeSummary.fromEmployees(List.of(employee("0"), employee("1"), employee("1")));
        assertEquals(2L, summary.getTotalSalary());
        assertEquals(new BigDecimal("0.67"), summary.getAverageSalary().orElseThrow());
        assertEquals(0, summary.getLowestSalary().orElseThrow());
        assertEquals(1, summary.getHighestSalary().orElseThrow());
    }

    @Test
    public void fromEmployees_halfCentAverage_roundsHalfUp() {
        List<Employee> employees = new ArrayList<>(Collections.nCopies(7, employee("0")));
        employees.add(employee("1"));
        assertEquals(new BigDecimal("0.13"), EmployeeSummary.fromEmployees(employees).getAverageSalary().orElseThrow());
    }

    @Test
    public void fromEmployees_largeTotal_doesNotOverflowInt() {
        EmployeeSummary summary = EmployeeSummary.fromEmployees(Collections.nCopies(50000, employee("50000")));
        assertEquals(2500000000L, summary.getTotalSalary());
        assertEquals(new BigDecimal("50000.00"), summary.getAverageSalary().orElseThrow());
    }

    @Test
    public void fromEmployees_snapshot_doesNotChangeWhenInputChanges() {
        List<Employee> employees = new ArrayList<>(List.of(employee("1000")));
        EmployeeSummary summary = EmployeeSummary.fromEmployees(employees);
        employees.set(0, employee("5000"));
        assertEquals(1000L, summary.getTotalSalary());
        assertEquals(5000L, EmployeeSummary.fromEmployees(employees).getTotalSalary());
    }

    private Employee employee(String salary) {
        return new EmployeeBuilder().withSalary(salary).build();
    }
}
