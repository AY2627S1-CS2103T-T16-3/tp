package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.employee.Employee;
import seedu.address.model.employee.UniqueEmployeeList;
import seedu.address.model.employee.exceptions.DuplicateEmployeeException;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.EmployeeBuilder;

/** Verifies contact uniqueness through commands, the model, and persisted data. */
public class ContactUniquenessTest {
    @TempDir
    public Path folder;

    private Model model;
    private Logic logic;
    private Path dataFile;

    @BeforeEach
    public void setUp() throws Exception {
        model = new ModelManager();
        dataFile = folder.resolve("addressbook.json");
        logic = new LogicManager(model, new StorageManager(new JsonAddressBookStorage(dataFile),
                new JsonUserPrefsStorage(folder.resolve("preferences.json"))));
        logic.execute("add n/Alice p/90000001 e/alice@example.com a/One le/14");
        logic.execute("add n/Bob p/90000002 e/bob@example.com a/Two le/14");
    }

    @Test
    public void add_sameNameUniqueContacts_succeedsAndRoundTrips() throws Exception {
        logic.execute("add n/Alice p/90000003 e/another@example.com a/Three le/14");
        assertEquals(3, model.getAddressBook().getEmployeeList().size());
        assertEquals(model.getAddressBook(), new JsonAddressBookStorage(dataFile).readAddressBook().orElseThrow());
    }

    @Test
    public void add_eitherContactAlreadyUsed_rejectsEvenWhenHidden() throws Exception {
        logic.execute("find Bob");
        for (String details : List.of("p/90000001 e/new@example.com", "p/90000003 e/alice@example.com",
                "p/90000003 e/ALICE@EXAMPLE.COM", "p/90000001 e/bob@example.com")) {
            assertRejectedUnchanged("add n/Charlie " + details + " a/Three le/14");
        }
    }

    @Test
    public void edit_eitherContactAlreadyUsed_rejectsWhenOtherContactUnchanged() throws Exception {
        for (String details : List.of("p/90000002", "e/bob@example.com", "e/BOB@EXAMPLE.COM",
                "p/90000002 e/bob@example.com", "n/Bob p/90000002", "n/Bob e/bob@example.com")) {
            assertRejectedUnchanged("edit 1 " + details);
        }
    }

    @Test
    public void edit_hiddenEmployeeContacts_rejects() throws Exception {
        logic.execute("find Alice");
        assertRejectedUnchanged("edit 1 p/90000002");
        assertRejectedUnchanged("edit 1 e/bob@example.com");
    }

    @Test
    public void edit_sameNameOwnContactsAndNewContacts_succeed() throws Exception {
        logic.execute("edit 1 n/Bob p/90000001 e/alice@example.com");
        assertEquals("Bob", model.getAddressBook().getEmployeeList().getFirst().getName().fullName);
        logic.execute("edit 1 p/90000003");
        logic.execute("edit 1 e/new@example.com");
        logic.execute("edit 1 p/90000004 e/fresh@example.com");
        Employee edited = model.getAddressBook().getEmployeeList().getFirst();
        assertEquals("90000004", edited.getPhone().value);
        assertEquals("fresh@example.com", edited.getEmail().value);
        assertEquals(model.getAddressBook(), new JsonAddressBookStorage(dataFile).readAddressBook().orElseThrow());
    }

    @Test
    public void model_directEditCannotBypassContactProtection() {
        Employee target = model.getAddressBook().getEmployeeList().getFirst();
        for (Employee edited : List.of(new EmployeeBuilder(target).withPhone("90000002").build(),
                new EmployeeBuilder(target).withEmail("BOB@EXAMPLE.COM").build())) {
            assertThrows(DuplicateEmployeeException.class, () -> model.setEmployee(target, edited));
            assertEquals(target, model.getAddressBook().getEmployeeList().getFirst());
        }
    }

    @Test
    public void contactConflictsAgainstDifferentEmployees_areBothChecked() throws Exception {
        assertRejectedUnchanged("edit 1 p/90000003 e/bob@example.com");
        assertRejectedUnchanged("edit 1 p/90000002 e/new@example.com");
    }

    @Test
    public void list_bulkReplacementRejectsContactDuplicates() {
        Employee alice = model.getAddressBook().getEmployeeList().getFirst();
        UniqueEmployeeList employees = new UniqueEmployeeList();
        for (Employee conflict : List.of(new EmployeeBuilder(alice).withName("Charlie")
                .withEmail("other@example.com").build(), new EmployeeBuilder(alice).withName("Charlie")
                .withPhone("90000003").withEmail("ALICE@EXAMPLE.COM").build())) {
            assertThrows(DuplicateEmployeeException.class, () -> employees.setEmployees(List.of(alice, conflict)));
            assertEquals(0, employees.asUnmodifiableObservableList().size());
        }
    }

    @Test
    public void storage_existingContactConflicts_areRejectedWithoutChangingFile() throws Exception {
        String valid = Files.readString(dataFile);
        for (String invalid : List.of(valid.replace("90000002", "90000001"),
                valid.replace("bob@example.com", "alice@example.com"),
                valid.replace("bob@example.com", "ALICE@EXAMPLE.COM"))) {
            Files.writeString(dataFile, invalid);
            assertThrows(DataLoadingException.class, () -> new JsonAddressBookStorage(dataFile).readAddressBook());
            assertEquals(invalid, Files.readString(dataFile));
        }
    }

    @Test
    public void add_contactErrors_identifyTheFieldAndExistingOwner() throws Exception {
        assertEquals("Phone number 90000002 is already used by Bob.",
                assertRejectedUnchanged("add n/Charlie p/90000002 e/new@example.com a/Three le/14").getMessage());
        assertEquals("Email address BOB@EXAMPLE.COM is already used by Bob.",
                assertRejectedUnchanged("add n/Charlie p/90000003 e/BOB@EXAMPLE.COM a/Three le/14").getMessage());
    }

    @Test
    public void add_contactsConflictWithDifferentEmployees_namesBothOwners() throws Exception {
        assertEquals("Email address alice@example.com is already used by Alice.\n"
                        + "Phone number 90000002 is already used by Bob.",
                assertRejectedUnchanged("add n/Charlie p/90000002 e/alice@example.com a/Three le/14").getMessage());
    }

    @Test
    public void edit_contactErrors_identifyHiddenOwnerAndBothConflicts() throws Exception {
        logic.execute("find Alice");
        assertEquals("Phone number 90000002 is already used by Bob.",
                assertRejectedUnchanged("edit 1 p/90000002").getMessage());
        assertEquals("Email address BOB@EXAMPLE.COM is already used by Bob.",
                assertRejectedUnchanged("edit 1 e/BOB@EXAMPLE.COM").getMessage());
        assertEquals("Phone number 90000002 is already used by Bob.\n"
                        + "Email address bob@example.com is already used by Bob.",
                assertRejectedUnchanged("edit 1 p/90000002 e/bob@example.com").getMessage());
    }

    @Test
    public void add_phoneWithLeadingZero_comparesDigitStringsRatherThanNumericValues() throws Exception {
        logic.execute("add n/Charlie p/090000001 e/new@example.com a/Three le/14");
        assertEquals(3, model.getAddressBook().getEmployeeList().size());
        assertEquals("090000001", model.getAddressBook().getEmployeeList().getLast().getPhone().value);
    }

    private CommandException assertRejectedUnchanged(String command) throws Exception {
        List<Employee> before = List.copyOf(model.getAddressBook().getEmployeeList());
        List<Employee> displayed = List.copyOf(model.getFilteredEmployeeList());
        String saved = Files.readString(dataFile);
        CommandException failure = assertThrows(CommandException.class, () -> logic.execute(command));
        assertEquals(before, model.getAddressBook().getEmployeeList());
        assertEquals(displayed, model.getFilteredEmployeeList());
        assertEquals(saved, Files.readString(dataFile));
        return failure;
    }
}
