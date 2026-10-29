package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.AddressBook;
import seedu.address.model.employee.EmployeeId;
import seedu.address.model.leave.Leave;
import seedu.address.model.leave.LeaveId;
import seedu.address.model.leave.LeavePeriod;
import seedu.address.testutil.EmployeeBuilder;
import seedu.address.testutil.TypicalEmployees;

public class JsonSerializableAddressBookTest {

    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonSerializableAddressBookTest");
    private static final Path TYPICAL_EMPLOYEES_FILE = TEST_DATA_FOLDER.resolve("typicalEmployeesAddressBook.json");
    private static final Path INVALID_EMPLOYEE_FILE = TEST_DATA_FOLDER.resolve("invalidEmployeeAddressBook.json");
    private static final Path DUPLICATE_EMPLOYEE_FILE = TEST_DATA_FOLDER.resolve("duplicateEmployeeAddressBook.json");

    @Test
    public void toModelType_typicalEmployeesFile_success() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(TYPICAL_EMPLOYEES_FILE,
                JsonSerializableAddressBook.class).get();
        AddressBook addressBookFromFile = dataFromFile.toModelType();
        AddressBook typicalEmployeesAddressBook = TypicalEmployees.getTypicalAddressBook();
        assertEquals(addressBookFromFile, typicalEmployeesAddressBook);
    }

    @Test
    public void toModelType_invalidEmployeeFile_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(INVALID_EMPLOYEE_FILE,
                JsonSerializableAddressBook.class).get();
        assertThrows(IllegalValueException.class, dataFromFile::toModelType);
    }

    @Test
    public void toModelType_duplicateEmployees_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(DUPLICATE_EMPLOYEE_FILE,
                JsonSerializableAddressBook.class).get();
        assertThrows(IllegalValueException.class, JsonSerializableAddressBook.MESSAGE_DUPLICATE_EMPLOYEE,
                dataFromFile::toModelType);
    }

    @Test
    public void toModelType_invalidNextEmployeeId_throwsIllegalValueException() {
        JsonSerializableAddressBook data = new JsonSerializableAddressBook(List.of(), 0L);

        assertThrows(IllegalValueException.class, JsonSerializableAddressBook.MESSAGE_INVALID_EMPLOYEE_ID,
                data::toModelType);
    }

    @Test
    public void toModelType_unassignedEmployeeWithoutAvailableId_throwsIllegalValueException() {
        JsonAdaptedEmployee employee = new JsonAdaptedEmployee(new EmployeeBuilder().withoutEmployeeId().build());
        JsonSerializableAddressBook data = new JsonSerializableAddressBook(List.of(employee),
                (long) Integer.MAX_VALUE + 1);

        assertThrows(IllegalValueException.class, JsonSerializableAddressBook.MESSAGE_INVALID_EMPLOYEE_ID,
                data::toModelType);
    }

    @Test
    public void toModelType_validLeave_success() throws Exception {
        JsonAdaptedEmployee employee = new JsonAdaptedEmployee(new EmployeeBuilder().withEmployeeId(1).build());
        Leave leave = new Leave(new LeaveId(1), new EmployeeId(1), new LeavePeriod(
                LocalDate.of(2026, 10, 5), LocalDate.of(2026, 10, 7)));
        JsonSerializableAddressBook data = new JsonSerializableAddressBook(List.of(employee), 2L,
                List.of(new JsonAdaptedLeave(leave)), 2L);

        AddressBook result = data.toModelType();

        assertEquals(List.of(leave), result.getLeaveList());
        assertEquals(2, result.getNextLeaveId());
    }

    @Test
    public void toModelType_nullLeaveRecord_throwsIllegalValueException() {
        JsonSerializableAddressBook data = new JsonSerializableAddressBook(List.of(), 1L,
                Arrays.asList((JsonAdaptedLeave) null), 1L);

        assertThrows(IllegalValueException.class, JsonSerializableAddressBook.MESSAGE_NULL_LEAVE,
                data::toModelType);
    }

    @Test
    public void toModelType_nullAfterValidLeave_throwsIllegalValueException() {
        JsonAdaptedEmployee employee = new JsonAdaptedEmployee(new EmployeeBuilder().withEmployeeId(1).build());
        JsonAdaptedLeave leave = new JsonAdaptedLeave(1, 1, "2026-10-05", "2026-10-07");
        JsonSerializableAddressBook data = new JsonSerializableAddressBook(List.of(employee), 2L,
                Arrays.asList(leave, null), 2L);

        assertThrows(IllegalValueException.class, JsonSerializableAddressBook.MESSAGE_NULL_LEAVE,
                data::toModelType);
    }

    @Test
    public void toModelType_nullLeaveCollection_loadsLegacyDataWithoutLeave() throws Exception {
        JsonSerializableAddressBook data = new JsonSerializableAddressBook(List.of(), 1L, null, 1L);

        assertEquals(new AddressBook(), data.toModelType());
    }

    @Test
    public void toModelType_duplicateLeaveIds_throwsIllegalValueException() {
        JsonAdaptedEmployee employee = new JsonAdaptedEmployee(new EmployeeBuilder().withEmployeeId(1).build());
        JsonAdaptedLeave firstLeave = new JsonAdaptedLeave(1, 1, "2026-10-05", "2026-10-07");
        JsonAdaptedLeave secondLeave = new JsonAdaptedLeave(1, 1, "2026-11-02", "2026-11-03");
        JsonSerializableAddressBook data = new JsonSerializableAddressBook(List.of(employee), 2L,
                List.of(firstLeave, secondLeave), 2L);

        assertThrows(IllegalValueException.class, JsonSerializableAddressBook.MESSAGE_DUPLICATE_LEAVE,
                data::toModelType);
    }

    @Test
    public void toModelType_orphanedLeave_throwsIllegalValueException() {
        JsonAdaptedLeave leave = new JsonAdaptedLeave(1, 1, "2026-10-05", "2026-10-07");
        JsonSerializableAddressBook data = new JsonSerializableAddressBook(List.of(), 1L,
                List.of(leave), 2L);

        assertThrows(IllegalValueException.class, JsonSerializableAddressBook.MESSAGE_ORPHANED_LEAVE,
                data::toModelType);
    }

    @Test
    public void toModelType_overlappingLeaves_throwsIllegalValueException() {
        JsonAdaptedEmployee employee = new JsonAdaptedEmployee(new EmployeeBuilder().withEmployeeId(1).build());
        JsonAdaptedLeave firstLeave = new JsonAdaptedLeave(1, 1, "2026-10-05", "2026-10-07");
        JsonAdaptedLeave secondLeave = new JsonAdaptedLeave(2, 1, "2026-10-07", "2026-10-08");
        JsonSerializableAddressBook data = new JsonSerializableAddressBook(List.of(employee), 2L,
                List.of(firstLeave, secondLeave), 3L);

        assertThrows(IllegalValueException.class, JsonSerializableAddressBook.MESSAGE_OVERLAPPING_LEAVE,
                data::toModelType);
    }

    @Test
    public void toModelType_invalidNextLeaveId_throwsIllegalValueException() {
        JsonSerializableAddressBook data = new JsonSerializableAddressBook(List.of(), 1L, List.of(), 0L);

        assertThrows(IllegalValueException.class, JsonSerializableAddressBook.MESSAGE_INVALID_NEXT_LEAVE_ID,
                data::toModelType);
    }

}
