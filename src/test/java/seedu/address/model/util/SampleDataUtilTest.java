package seedu.address.model.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.employee.Employee;
import seedu.address.model.employee.EmployeeId;

public class SampleDataUtilTest {

    @Test
    public void getSampleEmployees_returnsUnassignedEmployees() {
        Employee[] sampleEmployees = SampleDataUtil.getSampleEmployees();

        assertEquals(6, sampleEmployees.length);
        for (Employee sampleEmployee : sampleEmployees) {
            assertTrue(sampleEmployee.getEmployeeId().isEmpty());
        }
    }

    @Test
    public void getSampleAddressBook_assignsSequentialEmployeeIds() {
        ReadOnlyAddressBook sampleAddressBook = SampleDataUtil.getSampleAddressBook();

        assertEquals(6, sampleAddressBook.getEmployeeList().size());
        for (int i = 0; i < sampleAddressBook.getEmployeeList().size(); i++) {
            assertEquals(new EmployeeId(i + 1),
                    sampleAddressBook.getEmployeeList().get(i).getEmployeeId().orElseThrow());
        }
    }
}
