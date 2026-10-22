package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.CommandResult.DisplayFormat;

public class ResultDisplayTest {
    @BeforeAll
    public static void startToolkit() throws Exception {
        System.setProperty("prism.order", "sw");
        FutureTask<Void> startup = new FutureTask<>(() -> Platform.setImplicitExit(false), null);
        Platform.startup(startup);
        startup.get(10, TimeUnit.SECONDS);
    }

    @Test
    public void setFeedbackToUser_tableResult_usesEqualWidthCharacters() throws Exception {
        runOnFxThread(() -> {
            ResultDisplay display = createDisplay();
            String table = "| Employees | 5 |\n| Tasks     | 6 |";
            display.setFeedbackToUser(new CommandResult(table, DisplayFormat.TABLE));
            display.getRoot().applyCss();
            TextArea area = (TextArea) display.getRoot().lookup("#resultDisplay");
            Text narrow = new Text("iiii");
            Text wide = new Text("WWWW");
            narrow.setFont(area.getFont());
            wide.setFont(area.getFont());
            assertEquals(narrow.getLayoutBounds().getWidth(), wide.getLayoutBounds().getWidth(), 0.01);
            assertEquals(table, area.getText());
            assertFalse(area.isWrapText());
            assertFalse(area.isEditable());
        });
    }

    @Test
    public void setFeedbackToUser_plainResultOrError_restoresFontAndScrollPosition() throws Exception {
        runOnFxThread(() -> {
            ResultDisplay display = createDisplay();
            TextArea area = (TextArea) display.getRoot().lookup("#resultDisplay");
            Font normalFont = area.getFont();
            for (boolean error : new boolean[] {false, true}) {
                display.setFeedbackToUser(new CommandResult("Summary table", DisplayFormat.TABLE));
                display.getRoot().applyCss();
                area.positionCaret(area.getLength());
                if (error) {
                    display.setFeedbackToUser("Invalid employee index");
                } else {
                    display.setFeedbackToUser(new CommandResult("Listed all employees"));
                }
                display.getRoot().applyCss();
                assertEquals(normalFont, area.getFont());
                assertEquals(error ? "Invalid employee index" : "Listed all employees", area.getText());
                assertEquals(0, area.getCaretPosition());
            }
        });
    }

    @Test
    public void setFeedbackToUser_nullInput_preservesFeedback() throws Exception {
        runOnFxThread(() -> {
            ResultDisplay display = createDisplay();
            display.setFeedbackToUser("Existing feedback");
            assertThrows(NullPointerException.class, () -> display.setFeedbackToUser((String) null));
            assertThrows(NullPointerException.class, () -> display.setFeedbackToUser((CommandResult) null));
            assertEquals("Existing feedback", ((TextArea) display.getRoot().lookup("#resultDisplay")).getText());
        });
    }

    private static ResultDisplay createDisplay() {
        ResultDisplay display = new ResultDisplay();
        Scene scene = new Scene(display.getRoot());
        scene.getStylesheets().add(ResultDisplay.class.getResource("/view/DarkTheme.css").toExternalForm());
        display.getRoot().applyCss();
        return display;
    }

    private static void runOnFxThread(Runnable action) throws Exception {
        FutureTask<Void> task = new FutureTask<>(action, null);
        Platform.runLater(task);
        task.get(10, TimeUnit.SECONDS);
    }
}
