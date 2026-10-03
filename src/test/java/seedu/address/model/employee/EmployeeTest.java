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

import org.junit.jupiter.api.Test;

import seedu.address.testutil.EmployeeBuilder;

public class EmployeeTest {

    @Test
    public void constructor_optionalFields() {
        assertTrue(ALICE.getSalary().isEmpty());
        assertTrue(ALICE.getEmployeeId().isEmpty());
        assertTrue(ALICE.getLeave().isEmpty());

        Employee populated = withFields(new Salary("0"), new EmployeeId("00123"), new Leave("0"));
        assertEquals(new Salary("0"), populated.getSalary().orElseThrow());
        assertEquals("00123", populated.getEmployeeId().orElseThrow().value);
        assertEquals(new Leave("0"), populated.getLeave().orElseThrow());
        assertTrue(withFields(null, new EmployeeId("00123"), null).getSalary().isEmpty());
        assertEquals(ALICE, withFields(null, null, null));
        assertEquals(ALICE.hashCode(), withFields(null, null, null).hashCode());
        assertThrows(NullPointerException.class, () -> new Employee(null, ALICE.getPhone(), ALICE.getEmail(),
                ALICE.getAddress(), null, null, null, ALICE.getTags()));
    }

    @Test
    public void equals_optionalFieldsAndIdentity() {
        Employee populated = withFields(new Salary("1000"), new EmployeeId("00123"), new Leave("14"));
        Employee copy = withFields(new Salary("1000"), new EmployeeId("00123"), new Leave("14"));
        assertEquals(populated, copy);
        assertEquals(populated.hashCode(), copy.hashCode());
        assertFalse(populated.equals(ALICE));
        assertFalse(populated.equals(withFields(new Salary("1001"), new EmployeeId("00123"), new Leave("14"))));
        assertFalse(populated.equals(withFields(new Salary("1000"), new EmployeeId("123"), new Leave("14"))));
        assertFalse(populated.equals(withFields(new Salary("1000"), new EmployeeId("00123"), new Leave("15"))));
        assertTrue(populated.isSameEmployee(ALICE));
        assertTrue(ALICE.isSameEmployee(populated));
    }

    @Test
    public void salary_validationAndValueSemantics() {
        assertThrows(NullPointerException.class, () -> new Salary(null));
        for (String invalid : new String[] {"", " ", "-1", "+1", "1.0", "1a", "50001",
            "999999999999999999999999999999", "１"}) {
            assertFalse(Salary.isValidSalary(invalid));
            assertThrows(IllegalArgumentException.class, () -> new Salary(invalid));
        }
        for (String valid : new String[] {"0", "1", "49999", "50000", "00001"}) {
            assertTrue(Salary.isValidSalary(valid));
            Salary salary = new Salary(valid);
            assertEquals(valid, salary.value);
            assertEquals(valid, salary.toString());
            assertEquals(salary, new Salary(valid));
            assertEquals(salary.hashCode(), new Salary(valid).hashCode());
            assertFalse(salary.equals(null));
            assertFalse(salary.equals(valid));
        }
        assertFalse(new Salary("0").equals(new Salary("1")));
    }

    @Test
    public void employeeId_validationAndValueSemantics() {
        assertThrows(NullPointerException.class, () -> new EmployeeId(null));
        for (String invalid : new String[] {"", " ", "-1", "+1", "1.0", "1a", " 1", "１"}) {
            assertFalse(EmployeeId.isValidEmployeeId(invalid));
            assertThrows(IllegalArgumentException.class, () -> new EmployeeId(invalid));
        }
        for (String valid : new String[] {"0", "00123", "999999999999999999999999999999"}) {
            assertTrue(EmployeeId.isValidEmployeeId(valid));
            EmployeeId id = new EmployeeId(valid);
            assertEquals(valid, id.value);
            assertEquals(valid, id.toString());
            assertEquals(id, new EmployeeId(valid));
            assertEquals(id.hashCode(), new EmployeeId(valid).hashCode());
            assertFalse(id.equals(null));
            assertFalse(id.equals(valid));
        }
        assertFalse(new EmployeeId("00123").equals(new EmployeeId("123")));
    }

    @Test
    public void leave_validationAndValueSemantics() {
        assertThrows(NullPointerException.class, () -> new Leave(null));
        for (String invalid : new String[] {"", " ", "-1", "+1", "1.0", "1a", "1 ", "１"}) {
            assertFalse(Leave.isValidLeave(invalid));
            assertThrows(IllegalArgumentException.class, () -> new Leave(invalid));
        }
        for (String valid : new String[] {"0", "1", "14", "0001", "999999999999999999999999999999"}) {
            assertTrue(Leave.isValidLeave(valid));
            Leave leave = new Leave(valid);
            assertEquals(valid, leave.value);
            assertEquals(valid, leave.toString());
            assertEquals(leave, new Leave(valid));
            assertEquals(leave.hashCode(), new Leave(valid).hashCode());
            assertFalse(leave.equals(null));
            assertFalse(leave.equals(valid));
        }
        assertFalse(new Leave("0").equals(new Leave("1")));
    }

    private Employee withFields(Salary salary, EmployeeId employeeId, Leave leave) {
        return new Employee(ALICE.getName(), ALICE.getPhone(), ALICE.getEmail(), ALICE.getAddress(),
                salary, employeeId, leave, ALICE.getTags());
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
                + ", email=" + ALICE.getEmail() + ", address=" + ALICE.getAddress() + ", tags=" + ALICE.getTags() + "}";
        assertEquals(expected, ALICE.toString());
    }
}
