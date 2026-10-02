package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.employee.Address;
import seedu.address.model.employee.Email;
import seedu.address.model.employee.Employee;
import seedu.address.model.employee.EmployeeId;
import seedu.address.model.employee.Leave;
import seedu.address.model.employee.Name;
import seedu.address.model.employee.Phone;
import seedu.address.model.employee.Salary;

public class JsonAdaptedEmployeeFieldsTest {

    @Test
    public void toModelType_jsonRoundTrip_preservesEmployeeFields() throws Exception {
        Employee employee = new Employee(new Name("Alice"), new Phone("91234567"),
                new Email("alice@example.com"), new Address("123 Street"),
                new Salary("50000"), new EmployeeId("00123"), new Leave("14"), Set.of());
        String json = JsonUtil.toJsonString(new JsonAdaptedEmployee(employee));
        Employee restored = JsonUtil.fromJsonString(json, JsonAdaptedEmployee.class).toModelType();

        assertEquals(employee, restored);
        assertEquals(employee.getSalary(), restored.getSalary());
        assertEquals(employee.getEmployeeId(), restored.getEmployeeId());
        assertEquals(employee.getLeave(), restored.getLeave());
    }

    @Test
    public void toModelType_invalidOrMissingEmployeeFields_throwsIllegalValueException() {
        assertThrows(IllegalValueException.class, () ->
                createAdaptedEmployee(null, "001", "14").toModelType());
        assertThrows(IllegalValueException.class, () ->
                createAdaptedEmployee("3000", null, "14").toModelType());
        assertThrows(IllegalValueException.class, () ->
                createAdaptedEmployee("3000", "001", null).toModelType());
        assertThrows(IllegalValueException.class, () ->
                createAdaptedEmployee("50001", "001", "14").toModelType());
        assertThrows(IllegalValueException.class, () ->
                createAdaptedEmployee("3000", "E001", "14").toModelType());
        assertThrows(IllegalValueException.class, () ->
                createAdaptedEmployee("3000", "001", "-1").toModelType());
    }

    private JsonAdaptedEmployee createAdaptedEmployee(String salary, String employeeId, String leave) {
        return new JsonAdaptedEmployee("Alice", "91234567", "alice@example.com", "123 Street",
                salary, employeeId, leave, List.of());
    }
}
