package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;
import seedu.address.MainApp;
import seedu.address.logic.LogicManager;
import seedu.address.logic.commands.ListCommand;
import seedu.address.model.AddressBook;
import seedu.address.model.ModelManager;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

public class UiManagerTest {
    @TempDir
    public Path temporaryFolder;

    @BeforeAll
    public static void startToolkit() throws Exception {
        System.setProperty("prism.order", "sw");
        FutureTask<Void> startup = new FutureTask<>(() -> Platform.setImplicitExit(false), null);
        try {
            Platform.startup(startup);
        } catch (IllegalStateException e) {
            Platform.runLater(startup);
        }
        startup.get(10, TimeUnit.SECONDS);
    }

    @Test
    public void setFeedbackToUser_displaysStartupErrorAndNextCommandUsesExistingResultBox() throws Exception {
        FutureTask<Void> task = new FutureTask<>(() -> {
            Path dataFile = temporaryFolder.resolve("addressbook.json");
            JsonAddressBookStorage storage = new JsonAddressBookStorage(dataFile);
            LogicManager logic = new LogicManager(new ModelManager(), new StorageManager(storage,
                    new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json"))));
            UiManager ui = new UiManager(logic, dataFile);
            Stage primaryStage = new Stage();
            try {
                ui.start(primaryStage);
                TextArea results = (TextArea) primaryStage.getScene().lookup("#resultDisplay");
                assertEquals("", results.getText());
                String feedback = String.format(MainApp.MESSAGE_DATA_LOADING_ERROR, dataFile);
                ui.setFeedbackToUser(feedback);
                assertEquals(feedback, results.getText());
                assertTrue(feedback.contains("invalid or unreadable"));
                assertTrue(feedback.contains("next successful command will overwrite"));
                assertEquals(1, Window.getWindows().stream().filter(Window::isShowing).count());
                assertFalse(Files.exists(dataFile));

                TextField command = (TextField) primaryStage.getScene().lookup("#commandTextField");
                command.setText("list");
                command.fireEvent(new ActionEvent());
                assertEquals(ListCommand.MESSAGE_SUCCESS, results.getText());
                assertEquals(new AddressBook(), storage.readAddressBook().orElseThrow());
            } finally {
                List.copyOf(Window.getWindows()).forEach(Window::hide);
            }
            return null;
        });
        Platform.runLater(task);
        task.get(20, TimeUnit.SECONDS);
    }
}
