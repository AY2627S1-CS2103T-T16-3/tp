package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.storage.JsonAdaptedEmployee.MISSING_FIELD_MESSAGE_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalEmployees.BENSON;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.employee.Address;
import seedu.address.model.employee.Email;
import seedu.address.model.employee.Employee;
import seedu.address.model.employee.Name;
import seedu.address.model.employee.Phone;
import seedu.address.model.employee.Salary;
import seedu.address.model.employee.Task;
import seedu.address.testutil.EmployeeBuilder;

public class JsonAdaptedEmployeeTest {
    private static final String INVALID_NAME = "R@chel";
    private static final String INVALID_PHONE = "+651234";
    private static final String INVALID_ADDRESS = " ";
    private static final String INVALID_EMAIL = "example.com";
    private static final String INVALID_TAG = "#friend";

    private static final String VALID_SALARY = BENSON.getSalary().salary;

    private static final String VALID_NAME = BENSON.getName().toString();
    private static final String VALID_PHONE = BENSON.getPhone().toString();
    private static final String VALID_EMAIL = BENSON.getEmail().toString();
    private static final String VALID_ADDRESS = BENSON.getAddress().toString();
    private static final List<JsonAdaptedTag> VALID_TAGS = BENSON.getTags().stream()
            .map(JsonAdaptedTag::new)
            .collect(Collectors.toList());

    @Test
    public void toModelType_validEmployeeDetails_returnsEmployee() throws Exception {
        Employee original = BENSON.addTask(new Task("Prepare report"));
        JsonAdaptedEmployee employee = new JsonAdaptedEmployee(original);
        Employee restored = employee.toModelType();
        assertEquals(original, restored);
        assertEquals(original.getSalary(), restored.getSalary());
        assertEquals(original.getTasks(), restored.getTasks());
    }

    @Test
    public void toModelType_validTasks_returnsEmployeeWithTasks() throws Exception {
        JsonAdaptedEmployee employee = new JsonAdaptedEmployee(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                VALID_ADDRESS, VALID_SALARY, VALID_TAGS, List.of(new JsonAdaptedTask("Prepare report"),
                        new JsonAdaptedTask("Submit report")));

        assertEquals(List.of(new Task("Prepare report"), new Task("Submit report")), employee.toModelType().getTasks());
    }

    @Test
    public void constructor_employeeWithTasks_roundTripsTasks() throws Exception {
        Employee employeeWithTasks = BENSON.addTask(new Task("Prepare report"));
        JsonAdaptedEmployee employee = new JsonAdaptedEmployee(employeeWithTasks);

        assertEquals(employeeWithTasks, employee.toModelType());
    }

    @Test
    public void toModelType_nullTasks_returnsEmployeeWithEmptyTasks() throws Exception {
        JsonAdaptedEmployee employee = new JsonAdaptedEmployee(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                VALID_ADDRESS, VALID_SALARY, VALID_TAGS, null);

        assertEquals(List.of(), employee.toModelType().getTasks());
    }

    @Test
    public void toModelType_duplicateTasks_throwsIllegalValueException() {
        JsonAdaptedEmployee employee = new JsonAdaptedEmployee(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                VALID_ADDRESS, VALID_SALARY, VALID_TAGS, List.of(new JsonAdaptedTask("Prepare report"),
                        new JsonAdaptedTask(" prepare  REPORT ")));

        assertThrows(IllegalValueException.class, employee::toModelType);
    }

    @Test
    public void toModelType_invalidTask_throwsIllegalValueException() {
        JsonAdaptedEmployee employee = new JsonAdaptedEmployee(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                VALID_ADDRESS, VALID_SALARY, VALID_TAGS, List.of(new JsonAdaptedTask(" ")));

        assertThrows(IllegalValueException.class, employee::toModelType);
    }

    @Test
    public void toModelType_invalidName_throwsIllegalValueException() {
        JsonAdaptedEmployee employee =
                new JsonAdaptedEmployee(INVALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                        VALID_SALARY, VALID_TAGS, List.of());
        String expectedMessage = Name.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, employee::toModelType);
    }

    @Test
    public void toModelType_nullName_throwsIllegalValueException() {
        JsonAdaptedEmployee employee = new JsonAdaptedEmployee(null, VALID_PHONE, VALID_EMAIL,
                VALID_ADDRESS, VALID_SALARY, VALID_TAGS, List.of());
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, employee::toModelType);
    }

    @Test
    public void toModelType_invalidPhone_throwsIllegalValueException() {
        JsonAdaptedEmployee employee =
                new JsonAdaptedEmployee(VALID_NAME, INVALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                        VALID_SALARY, VALID_TAGS, List.of());
        String expectedMessage = Phone.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, employee::toModelType);
    }

    @Test
    public void toModelType_nullPhone_throwsIllegalValueException() {
        JsonAdaptedEmployee employee = new JsonAdaptedEmployee(VALID_NAME, null, VALID_EMAIL,
                VALID_ADDRESS, VALID_SALARY, VALID_TAGS, List.of());
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, employee::toModelType);
    }

    @Test
    public void toModelType_invalidEmail_throwsIllegalValueException() {
        JsonAdaptedEmployee employee =
                new JsonAdaptedEmployee(VALID_NAME, VALID_PHONE, INVALID_EMAIL, VALID_ADDRESS,
                        VALID_SALARY, VALID_TAGS, List.of());
        String expectedMessage = Email.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, employee::toModelType);
    }

    @Test
    public void toModelType_nullEmail_throwsIllegalValueException() {
        JsonAdaptedEmployee employee = new JsonAdaptedEmployee(VALID_NAME, VALID_PHONE, null,
                VALID_ADDRESS, VALID_SALARY, VALID_TAGS, List.of());
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Email.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, employee::toModelType);
    }

    @Test
    public void toModelType_invalidAddress_throwsIllegalValueException() {
        JsonAdaptedEmployee employee =
                new JsonAdaptedEmployee(VALID_NAME, VALID_PHONE, VALID_EMAIL, INVALID_ADDRESS,
                        VALID_SALARY, VALID_TAGS, List.of());
        String expectedMessage = Address.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, employee::toModelType);
    }

    @Test
    public void toModelType_nullAddress_throwsIllegalValueException() {
        JsonAdaptedEmployee employee = new JsonAdaptedEmployee(VALID_NAME, VALID_PHONE, VALID_EMAIL, null,
                VALID_SALARY, VALID_TAGS, List.of());
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Address.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, employee::toModelType);
    }

    @Test
    public void toModelType_invalidTags_throwsIllegalValueException() {
        List<JsonAdaptedTag> invalidTags = new ArrayList<>(VALID_TAGS);
        invalidTags.add(new JsonAdaptedTag(INVALID_TAG));
        JsonAdaptedEmployee employee =
                new JsonAdaptedEmployee(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                        VALID_SALARY, invalidTags, List.of());
        assertThrows(IllegalValueException.class, employee::toModelType);
    }

    @Test
    public void toModelType_invalidSalary_throwsIllegalValueException() {
        JsonAdaptedEmployee employee = new JsonAdaptedEmployee(
                VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, "50001", VALID_TAGS, List.of());
        assertThrows(IllegalValueException.class, Salary.MESSAGE_CONSTRAINTS, employee::toModelType);
    }

    @Test
    public void toModelType_missingSalary_defaultsToZero() throws Exception {
        JsonAdaptedEmployee employee = new JsonAdaptedEmployee(
                VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, null, VALID_TAGS, List.of());
        assertEquals(new Salary("0"), employee.toModelType().getSalary());
    }

    @Test
    public void toModelType_missingSalaryAndTasks_defaultsBoth() throws Exception {
        JsonAdaptedEmployee employee = new JsonAdaptedEmployee(
                VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, null, VALID_TAGS, null);
        Employee restored = employee.toModelType();
        assertEquals(new Salary("0"), restored.getSalary());
        assertEquals(List.of(), restored.getTasks());
    }

    @Test
    public void toModelType_nonDefaultSalary_preserved() throws Exception {
        Employee original = new EmployeeBuilder(BENSON).withSalary("5000").build();
        assertEquals(original.getSalary(), new JsonAdaptedEmployee(original).toModelType().getSalary());
    }

}
