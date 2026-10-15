package seedu.address.model.employee;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalEmployees.ALICE;
import static seedu.address.testutil.TypicalEmployees.BOB;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.EmployeeBuilder;

public class EmployeeTest {

    @Test
    public void asObservableList_modifyList_throwsUnsupportedOperationException() {
        Employee employee = new EmployeeBuilder().build();
        assertThrows(UnsupportedOperationException.class, () -> employee.getTags().remove(0));
    }

    @Test
    public void createUnassigned_hasNoEmployeeId() {
        Employee employee = new EmployeeBuilder().build();

        assertTrue(employee.getEmployeeId().isEmpty());
    }

    @Test
    public void withEmployeeId_unassignedEmployee_assignsEmployeeId() {
        Employee employee = new EmployeeBuilder().build();

        Employee assignedEmployee = employee.withEmployeeId(new EmployeeId(1));

        assertEquals(new EmployeeId(1), assignedEmployee.getEmployeeId().orElseThrow());
        assertTrue(employee.getEmployeeId().isEmpty());
    }

    @Test
    public void withEmployeeId_null_throwsNullPointerException() {
        Employee employee = new EmployeeBuilder().build();

        assertThrows(NullPointerException.class, () -> employee.withEmployeeId(null));
    }

    @Test
    public void withEmployeeId_assignedEmployee_throwsIllegalStateException() {
        Employee employee = new EmployeeBuilder().withEmployeeId(1).build();

        assertThrows(IllegalStateException.class, () -> employee.withEmployeeId(new EmployeeId(2)));
    }

    @Test
    public void updateMethods_assignedEmployee_preserveEmployeeId() {
        Employee employee = new EmployeeBuilder().withEmployeeId(1)
                .withTasks(new Task("Prepare report"))
                .build();

        Employee withDetails = employee.withDetails(new Name(VALID_NAME_BOB), new Phone(VALID_PHONE_BOB),
                new Email(VALID_EMAIL_BOB), new Address(VALID_ADDRESS_BOB), employee.getTags());
        Employee withSalary = employee.withSalary(new Salary("5000"));
        Employee withAddedTask = employee.addTask(new Task("Submit report"));
        Employee withDeletedTask = employee.deleteTask(0);

        assertEquals(employee.getEmployeeId(), withDetails.getEmployeeId());
        assertEquals(employee.getEmployeeId(), withSalary.getEmployeeId());
        assertEquals(employee.getEmployeeId(), withAddedTask.getEmployeeId());
        assertEquals(employee.getEmployeeId(), withDeletedTask.getEmployeeId());
    }

    @Test
    public void addTask_duplicateTask_throwsIllegalArgumentException() {
        Employee employeeWithTask = new EmployeeBuilder().build()
                .addTask(new Task("Prepare report"));

        assertThrows(IllegalArgumentException.class, () ->
                employeeWithTask.addTask(new Task(" prepare  REPORT ")));
    }

    @Test
    public void deleteTask_validIndex_returnsEmployeeWithoutTask() {
        Employee employee = new EmployeeBuilder().build()
                .addTask(new Task("Prepare report"))
                .addTask(new Task("Submit report"));

        Employee updatedEmployee = employee.deleteTask(0);

        assertEquals(List.of(new Task("Submit report")), updatedEmployee.getTasks());
        assertEquals(employee.getSalary(), updatedEmployee.getSalary());
    }

    @Test
    public void deleteTask_invalidIndex_throwsIndexOutOfBoundsException() {
        Employee employee = new EmployeeBuilder().build()
                .addTask(new Task("Prepare report"));

        assertThrows(IndexOutOfBoundsException.class, () -> employee.deleteTask(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> employee.deleteTask(employee.getTasks().size()));
    }

    @Test
    public void isSameEmployee() {
        // same object -> returns true
        assertTrue(ALICE.isSameEmployee(ALICE));

        // null -> returns false
        assertFalse(ALICE.isSameEmployee(null));

        // same name, all other attributes different -> returns true
        Employee editedAlice = new EmployeeBuilder(ALICE).withPhone(VALID_PHONE_BOB).withEmail(VALID_EMAIL_BOB)
                .withAddress(VALID_ADDRESS_BOB).withTags(VALID_TAG_HUSBAND).build();
        assertTrue(ALICE.isSameEmployee(editedAlice));

        // different name, all other attributes same -> returns false
        editedAlice = new EmployeeBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertFalse(ALICE.isSameEmployee(editedAlice));

        // name differs in case, all other attributes same -> returns false
        Employee editedBob = new EmployeeBuilder(BOB).withName(VALID_NAME_BOB.toLowerCase()).build();
        assertFalse(BOB.isSameEmployee(editedBob));

        // name has trailing spaces, all other attributes same -> returns false
        String nameWithTrailingSpaces = VALID_NAME_BOB + " ";
        editedBob = new EmployeeBuilder(BOB).withName(nameWithTrailingSpaces).build();
        assertFalse(BOB.isSameEmployee(editedBob));
    }

    @Test
    public void equals() {
        // same values -> returns true
        Employee aliceCopy = new EmployeeBuilder(ALICE).build();
        assertTrue(ALICE.equals(aliceCopy));
        assertEquals(ALICE.hashCode(), aliceCopy.hashCode());

        // same object -> returns true
        assertTrue(ALICE.equals(ALICE));

        // null -> returns false
        assertFalse(ALICE.equals(null));

        // different type -> returns false
        assertFalse(ALICE.equals(5));

        // different employee -> returns false
        assertFalse(ALICE.equals(BOB));

        // different employee ID -> returns false
        Employee employeeWithDifferentId = new EmployeeBuilder(ALICE).withEmployeeId(99).build();
        assertFalse(ALICE.equals(employeeWithDifferentId));

        // different name -> returns false
        Employee editedAlice = new EmployeeBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different phone -> returns false
        editedAlice = new EmployeeBuilder(ALICE).withPhone(VALID_PHONE_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different email -> returns false
        editedAlice = new EmployeeBuilder(ALICE).withEmail(VALID_EMAIL_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different address -> returns false
        editedAlice = new EmployeeBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different tags -> returns false
        editedAlice = new EmployeeBuilder(ALICE).withTags(VALID_TAG_HUSBAND).build();
        assertFalse(ALICE.equals(editedAlice));

        // different salary -> returns false
        editedAlice = new EmployeeBuilder(ALICE).withSalary("5000").build();
        assertFalse(ALICE.equals(editedAlice));

        // different tasks -> returns false
        editedAlice = ALICE.addTask(new Task("Prepare report"));
        assertFalse(ALICE.equals(editedAlice));

        // different salary does not affect employee identity
        editedAlice = new EmployeeBuilder(ALICE).withSalary("5000").build();
        assertTrue(ALICE.isSameEmployee(editedAlice));

        // different tasks do not affect employee identity
        editedAlice = ALICE.addTask(new Task("Prepare report"));
        assertTrue(ALICE.isSameEmployee(editedAlice));
    }

    @Test
    public void copy_nonDefaultSalary_preservesSalary() {
        Employee original = new EmployeeBuilder(ALICE).withSalary("5000").build();
        Employee copy = new EmployeeBuilder(original).build();
        assertEquals(original.getSalary(), copy.getSalary());
    }

    @Test
    public void copy_assignedEmployee_preservesEmployeeId() {
        Employee original = new EmployeeBuilder(ALICE).withEmployeeId(1).build();
        Employee copy = new EmployeeBuilder(original).build();

        assertEquals(original.getEmployeeId(), copy.getEmployeeId());
    }

    @Test
    public void toStringMethod() {
        String expected = Employee.class.getCanonicalName()
                + "{name=" + ALICE.getName()
                + ", phone=" + ALICE.getPhone()
                + ", email=" + ALICE.getEmail()
                + ", address=" + ALICE.getAddress()
                + ", salary=" + ALICE.getSalary()
                + ", tags=" + ALICE.getTags()
                + ", tasks=" + ALICE.getTasks() + "}";
        assertEquals(expected, ALICE.toString());
    }
}
