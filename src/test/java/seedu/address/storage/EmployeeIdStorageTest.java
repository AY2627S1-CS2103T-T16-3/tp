package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.model.AddressBook;
import seedu.address.model.employee.Employee;
import seedu.address.model.employee.EmployeeId;
import seedu.address.testutil.EmployeeBuilder;

public class EmployeeIdStorageTest {
    @TempDir
    public Path testFolder;

    @Test
    public void saveReloadAndAdd_preservesExistingIds_andAssignsUnusedId() throws Exception {
        AddressBook book = new AddressBook();
        book.addEmployee(new EmployeeBuilder().withName("Alice").build());
        book.addEmployee(new EmployeeBuilder().withName("Bob").withEmployeeId("999999").build());
        Path path = testFolder.resolve("employees.json");
        JsonAddressBookStorage storage = new JsonAddressBookStorage(path);
        storage.saveAddressBook(book);
        assertTrue(Files.readString(path).contains("\"employeeId\""));
        AddressBook restored = new AddressBook(storage.readAddressBook().orElseThrow());
        assertEquals(book, restored);
        Employee carol = restored.addEmployee(new EmployeeBuilder().withName("Carol").build());
        assertEquals(new EmployeeId("2"), carol.getEmployeeId().orElseThrow());
        storage.saveAddressBook(restored);
        assertEquals(restored, new AddressBook(storage.readAddressBook().orElseThrow()));
    }

    @Test
    public void readLegacyAndMixedIds_assignsWithoutCollisions_preservingOrder() throws Exception {
        Path path = testFolder.resolve("legacy.json");
        Files.writeString(path, "{\"persons\":[" + employeeJson("Alice", "") + ","
                + employeeJson("Bob", ",\"employeeId\":\"1\"") + "]}");
        JsonAddressBookStorage storage = new JsonAddressBookStorage(path);
        AddressBook book = new AddressBook(storage.readAddressBook().orElseThrow());
        assertEquals(List.of("2", "1"), book.getEmployeeList().stream()
                .map(employee -> employee.getEmployeeId().orElseThrow().value).toList());
        storage.saveAddressBook(book);
        assertEquals(book, new AddressBook(storage.readAddressBook().orElseThrow()));
    }

    @Test
    public void readInvalidId_failsWithoutRewritingFile() throws Exception {
        for (String invalid : new String[] {"0", "01", "1000000", "-1", "", " 1 "}) {
            Path path = testFolder.resolve("invalid.json");
            String json = "{\"persons\":[" + employeeJson("Alice", ",\"employeeId\":\"" + invalid + "\"") + "]}";
            Files.writeString(path, json);
            JsonAddressBookStorage storage = new JsonAddressBookStorage(path);
            assertThrows(DataLoadingException.class, storage::readAddressBook);
            assertEquals(json, Files.readString(path));
        }
    }

    @Test
    public void readDuplicateIds_failsWithoutRewritingFile() throws Exception {
        Path path = testFolder.resolve("duplicates.json");
        String json = "{\"persons\":[" + employeeJson("Alice", ",\"employeeId\":\"1\"") + ","
                + employeeJson("Bob", ",\"employeeId\":\"1\"") + "]}";
        Files.writeString(path, json);
        JsonAddressBookStorage storage = new JsonAddressBookStorage(path);
        assertThrows(DataLoadingException.class, storage::readAddressBook);
        assertEquals(json, Files.readString(path));
    }

    private String employeeJson(String name, String idField) {
        return "{\"name\":\"" + name + "\",\"phone\":\"91234567\",\"email\":\"a@example.com\","
                + "\"address\":\"Singapore\",\"tags\":[]" + idField + "}";
    }
}
