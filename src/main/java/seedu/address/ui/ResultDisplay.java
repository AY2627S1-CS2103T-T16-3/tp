package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Region;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.CommandResult.DisplayFormat;

/**
 * A UI component that displays the result of a command execution.
 */
public class ResultDisplay extends UiPart<Region> {

    private static final String FXML = "ResultDisplay.fxml";
    private static final PseudoClass TABLE = PseudoClass.getPseudoClass("table");

    @FXML
    private TextArea resultDisplay;

    public ResultDisplay() {
        super(FXML);
    }

    public void setFeedbackToUser(String feedbackToUser) {
        requireNonNull(feedbackToUser);
        resultDisplay.pseudoClassStateChanged(TABLE, false);
        resultDisplay.setText(feedbackToUser);
        resultDisplay.positionCaret(0);
    }

    /** Displays command feedback using its requested presentation. */
    public void setFeedbackToUser(CommandResult commandResult) {
        requireNonNull(commandResult);
        setFeedbackToUser(commandResult.getFeedbackToUser());
        resultDisplay.pseudoClassStateChanged(TABLE, commandResult.getDisplayFormat() == DisplayFormat.TABLE);
    }

}
