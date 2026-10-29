package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.employee.EmployeeId;
import seedu.address.model.employee.Task;
import seedu.address.model.leave.LeavePeriod;
import seedu.address.testutil.EmployeeBuilder;

public class JsonAddressBookNullValidationTest {
    @TempDir
    public Path temporaryFolder;

    private Path file;
    private JsonAddressBookStorage storage;
    private String validJson;

    @BeforeEach
    public void setUp() throws Exception {
        file = temporaryFolder.resolve("addressbook.json");
        storage = new JsonAddressBookStorage(file);
        AddressBook book = new AddressBook();
        book.addEmployee(new EmployeeBuilder().withTags("team").withTasks(new Task("Prepare report")).build());
        book.addLeave(new EmployeeId(1), new LeavePeriod(LocalDate.of(2026, 10, 5), LocalDate.of(2026, 10, 7)));
        storage.saveAddressBook(book);
        validJson = Files.readString(file);
    }

    @Test
    public void readAddressBook_nullInAnySavedField_rejectsAndPreservesFile() throws Exception {
        for (String pointer : List.of("/persons", "/leaves", "/nextEmployeeId", "/nextLeaveId",
                "/persons/0/employeeId", "/persons/0/name", "/persons/0/phone", "/persons/0/email",
                "/persons/0/address", "/persons/0/salary", "/persons/0/leaveEntitlement", "/persons/0/tags",
                "/persons/0/tasks", "/persons/0/tasks/0/details", "/persons/0/tasks/0/isDone",
                "/leaves/0/leaveId", "/leaves/0/employeeId", "/leaves/0/startDate", "/leaves/0/endDate")) {
            ObjectNode root = JsonUtil.fromJsonString(validJson, ObjectNode.class);
            int separator = pointer.lastIndexOf('/');
            ObjectNode parent = (ObjectNode) root.at(pointer.substring(0, separator));
            String field = pointer.substring(separator + 1);
            assertTrue(parent.has(field), pointer);
            parent.putNull(field);
            assertNullRejected(root.toString());
        }
    }

    @Test
    public void readAddressBook_nullInAnySavedArray_rejectsAndPreservesFile() throws Exception {
        for (String pointer : List.of("/persons", "/leaves", "/persons/0/tags", "/persons/0/tasks")) {
            ObjectNode root = JsonUtil.fromJsonString(validJson, ObjectNode.class);
            ArrayNode array = (ArrayNode) root.at(pointer);
            assertTrue(array.size() > 0, pointer);
            array.set(0, NullNode.getInstance());
            assertNullRejected(root.toString());
        }
    }

    @Test
    public void readAddressBook_nullDocumentOrUnknownNestedField_rejectsAndPreservesFile() throws Exception {
        assertNullRejected("null");
        assertNullRejected("{\"persons\":[],\"extra\":{\"nested\":[{\"value\":null}]}}");
    }

    @Test
    public void readAddressBook_nullFollowedByDuplicateKey_rejectsBeforeValueIsOverwritten() throws Exception {
        assertNullRejected("{\"persons\":[],\"extra\":null,\"extra\":\"value\"}");
    }

    @Test
    public void readAddressBook_validStringsEmptyCollectionsZeroAndFalse_loadsNormally() throws Exception {
        String json = "{\"persons\":[],\"leaves\":[],\"nextEmployeeId\":1,\"nextLeaveId\":1,"
                + "\"extra\":[\"null\",\"nullable\",0,false,\"\",{},[]]}";
        Files.writeString(file, json);

        assertEquals(new AddressBook(), storage.readAddressBook().orElseThrow());
        assertEquals(json, Files.readString(file));
    }

    @Test
    public void readAddressBook_omittedLegacyFields_keepsExistingDefaults() throws Exception {
        String json = "{\"persons\":[{\"name\":\"Alice\",\"phone\":\"91234567\","
                + "\"email\":\"alice@example.com\",\"address\":\"Null Street\"}]}";
        Files.writeString(file, json);

        ReadOnlyAddressBook loaded = storage.readAddressBook().orElseThrow();
        assertEquals(1, loaded.getEmployeeList().size());
        assertEquals(0, loaded.getEmployeeList().getFirst().getLeaveEntitlement().value);
        assertEquals("0", loaded.getEmployeeList().getFirst().getSalary().salary);
        assertTrue(loaded.getLeaveList().isEmpty());
        assertEquals(json, Files.readString(file));
    }

    @Test
    public void readAddressBook_emptyOrNonObjectDocument_throwsHandledLoadingException() throws Exception {
        for (String json : List.of("", " ", "[]", "false", "0", "\"text\"")) {
            Files.writeString(file, json);
            assertThrows(DataLoadingException.class, storage::readAddressBook, json);
            assertEquals(json, Files.readString(file));
        }
    }

    private void assertNullRejected(String json) throws Exception {
        Files.writeString(file, json);
        DataLoadingException exception = assertThrows(DataLoadingException.class, storage::readAddressBook, json);
        assertEquals(JsonAddressBookStorage.MESSAGE_NULL_VALUE, exception.getCause().getMessage());
        assertEquals(json, Files.readString(file));
    }
}
