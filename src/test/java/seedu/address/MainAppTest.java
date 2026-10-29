package seedu.address;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.stage.Stage;
import seedu.address.logic.LogicManager;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.util.SampleDataUtil;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.EmployeeBuilder;
import seedu.address.ui.Ui;

public class MainAppTest {
    @TempDir
    public Path temporaryFolder;

    private MainApp app;
    private RecordingUi ui;
    private Path dataFile;
    private StorageManager storage;

    @BeforeEach
    public void setUp() {
        app = new MainApp();
        ui = new RecordingUi();
        app.ui = ui;
        dataFile = temporaryFolder.resolve("addressbook.json");
        storage = new StorageManager(new JsonAddressBookStorage(dataFile),
                new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json")));
        app.storage = storage;
    }

    @Test
    public void start_nullLeave_showsLoadingErrorAndPreservesFileThroughShutdown() throws Exception {
        String data = "{\"persons\":[],\"leaves\":[null]}";
        Files.writeString(dataFile, data);
        loadModelAndStart();

        assertEquals(new AddressBook(), app.model.getAddressBook());
        assertLoadingErrorShown();
        assertEquals(data, Files.readString(dataFile));
        app.stop();
        assertEquals(data, Files.readString(dataFile));
    }

    @Test
    public void start_nullLeaveWithValidEmployees_preservesOriginalDataForRecovery() throws Exception {
        AddressBook saved = new AddressBook();
        saved.addEmployee(new EmployeeBuilder().build());
        storage.saveAddressBook(saved);
        String data = Files.readString(dataFile).replaceFirst("\"leaves\"\\s*:\\s*\\[\\s*\\]", "\"leaves\":[null]");
        assertTrue(data.contains("[null]"));
        Files.writeString(dataFile, data);
        loadModelAndStart();

        assertLoadingErrorShown();
        assertEquals(new AddressBook(), app.model.getAddressBook());
        app.stop();
        assertEquals(data, Files.readString(dataFile));
    }

    @Test
    public void start_invalidJson_showsLoadingErrorAndPreservesFileUntilSuccessfulCommand() throws Exception {
        String data = "not JSON";
        Files.writeString(dataFile, data);
        loadModelAndStart();

        assertLoadingErrorShown();
        assertEquals(data, Files.readString(dataFile));
        LogicManager logic = new LogicManager(app.model, storage);
        assertThrows(ParseException.class, () -> logic.execute("invalid-command"));
        assertEquals(data, Files.readString(dataFile));
        logic.execute("list");
        assertEquals(new AddressBook(), new JsonAddressBookStorage(dataFile).readAddressBook().orElseThrow());
    }

    @Test
    public void start_validData_loadsEmployeesWithoutLoadingError() throws Exception {
        AddressBook saved = new AddressBook();
        saved.addEmployee(new EmployeeBuilder().build());
        storage.saveAddressBook(saved);
        loadModelAndStart();

        assertEquals(saved, app.model.getAddressBook());
        assertTrue(ui.started);
        assertNull(ui.feedback);
    }

    @Test
    public void start_missingFile_loadsSampleDataWithoutLoadingErrorOrCreatingFile() {
        loadModelAndStart();

        assertEquals(new AddressBook(SampleDataUtil.getSampleAddressBook()), app.model.getAddressBook());
        assertTrue(ui.started);
        assertNull(ui.feedback);
        assertFalse(Files.exists(dataFile));
    }

    @Test
    public void start_validDataAfterFailedLoad_doesNotKeepPreviousLoadingError() throws Exception {
        Files.writeString(dataFile, "{\"persons\":[],\"leaves\":[null]}");
        app.initModelManager(storage, new UserPrefs());
        storage.saveAddressBook(new AddressBook());
        loadModelAndStart();

        assertEquals(new AddressBook(), app.model.getAddressBook());
        assertNull(ui.feedback);
    }

    private void loadModelAndStart() {
        app.model = app.initModelManager(storage, new UserPrefs());
        app.start(null);
    }

    private void assertLoadingErrorShown() {
        assertEquals(String.format(MainApp.MESSAGE_DATA_LOADING_ERROR, dataFile), ui.feedback);
    }

    private static class RecordingUi implements Ui {
        private boolean started;
        private String feedback;

        @Override
        public void start(Stage primaryStage) {
            started = true;
        }

        @Override
        public void setFeedbackToUser(String feedbackToUser) {
            assertTrue(started, "Feedback must appear after the main window starts.");
            feedback = feedbackToUser;
        }
    }
}
