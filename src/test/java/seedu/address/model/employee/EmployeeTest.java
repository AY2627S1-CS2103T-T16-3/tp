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
    public void addTask_validTask_taskIsStoredInOrder() {
        Employee employee = new EmployeeBuilder().build();

        employee = employee.addTask(new Task("Prepare report"));
        employee = employee.addTask(new Task("Submit report"));

        assertEquals(List.of(new Task("Prepare report"), new Task("Submit report")), employee.getTasks());
    }

    @Test
    public void addTask_duplicateTask_throwsIllegalArgumentException() {
        Employee employee = new EmployeeBuilder().build();
        employee = employee.addTask(new Task("Prepare report"));
        Employee employeeWithTask = employee;

        assertThrows(IllegalArgumentException.class, () ->
                employeeWithTask.addTask(new Task(" prepare  REPORT ")));
    }

    @Test
    public void deleteTask_validIndex_removesTask() {
        Employee employee = new EmployeeBuilder().build();
        employee = employee.addTask(new Task("Prepare report"));
        employee = employee.addTask(new Task("Submit report"));

        employee = employee.deleteTask(0);

        assertEquals(List.of(new Task("Submit report")), employee.getTasks());
    }

    @Test
    public void deleteTask_invalidIndex_throwsIndexOutOfBoundsException() {
        Employee employee = new EmployeeBuilder().build();

        assertThrows(IndexOutOfBoundsException.class, () -> employee.deleteTask(0));
    }

    @Test
    public void deleteTask_negativeIndex_throwsIndexOutOfBoundsException() {
        Employee employee = new EmployeeBuilder().build();

        assertThrows(IndexOutOfBoundsException.class, () -> employee.deleteTask(-1));
    }

    @Test
    public void getTasks_modifyReturnedList_throwsUnsupportedOperationException() {
        Employee employee = new EmployeeBuilder().build();

        assertThrows(UnsupportedOperationException.class, () -> employee.getTasks().add(new Task("Task")));
    }

    @Test
    public void addTask_originalEmployeeRemainsUnchanged() {
        Employee employee = new EmployeeBuilder().build();
        Employee employeeWithTask = employee.addTask(new Task("Prepare report"));

        assertEquals(0, employee.getTasks().size());
        assertEquals(1, employeeWithTask.getTasks().size());
    }

    @Test
    public void equals_differentTasks_returnsFalse() {
        Employee employee = new EmployeeBuilder().build();
        Employee employeeWithTask = employee.addTask(new Task("Prepare report"));

        assertFalse(employee.equals(employeeWithTask));
        assertFalse(employee.hashCode() == employeeWithTask.hashCode());
    }

    @Test
    public void asObservableList_modifyList_throwsUnsupportedOperationException() {
        Employee employee = new EmployeeBuilder().build();
        assertThrows(UnsupportedOperationException.class, () -> employee.getTags().remove(0));
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

        // same object -> returns true
        assertTrue(ALICE.equals(ALICE));

        // null -> returns false
        assertFalse(ALICE.equals(null));

        // different type -> returns false
        assertFalse(ALICE.equals(5));

        // different employee -> returns false
        assertFalse(ALICE.equals(BOB));

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
    }

    @Test
    public void toStringMethod() {
        String expected = Employee.class.getCanonicalName() + "{name=" + ALICE.getName() + ", phone=" + ALICE.getPhone()
                + ", email=" + ALICE.getEmail() + ", address=" + ALICE.getAddress() + ", tags=" + ALICE.getTags()
                + ", tasks=" + ALICE.getTasks() + "}";
        assertEquals(expected, ALICE.toString());
    }
}
