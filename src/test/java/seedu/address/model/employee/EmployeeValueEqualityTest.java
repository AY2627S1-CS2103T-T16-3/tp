package seedu.address.model.employee;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class EmployeeValueEqualityTest {

    @Test
    public void equals_differentEmployeeFields_returnsFalse() {
        Employee employee = createEmployee("3000", "001", "14");
        assertFalse(employee.equals(createEmployee("3001", "001", "14")));
        assertFalse(employee.equals(createEmployee("3000", "002", "14")));
        assertFalse(employee.equals(createEmployee("3000", "001", "15")));
    }

    @Test
    public void hashCode_equalEmployees_supportsHashSetLookup() {
        Employee employee = createEmployee("3000", "001", "14");
        Employee equalEmployee = createEmployee("03000", "001", "014");

        assertEquals(employee, equalEmployee);
        assertEquals(employee.hashCode(), equalEmployee.hashCode());

        Set<Employee> employees = new HashSet<>();
        employees.add(employee);
        assertTrue(employees.contains(equalEmployee));
        assertFalse(employees.add(equalEmployee));
        assertEquals(1, employees.size());
    }

    private Employee createEmployee(String salary, String employeeId, String leave) {
        return new Employee(new Name("Alice"), new Phone("91234567"),
                new Email("alice@example.com"), new Address("123 Street"),
                new Salary(salary), new EmployeeId(employeeId), new Leave(leave), Set.of());
    }
}
