package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Represents the result of a command execution.
 */
public class CommandResult {

    /** Selects how the result text should be displayed. */
    public enum DisplayFormat {
        TEXT,
        TABLE
    }

    private final String feedbackToUser;
    private final DisplayFormat displayFormat;

    /** Help information should be shown to the user. */
    private final boolean showHelp;

    /** The application should exit. */
    private final boolean exit;

    /**
     * Constructs a {@code CommandResult} with the specified fields.
     */
    public CommandResult(String feedbackToUser, boolean showHelp, boolean exit) {
        this(feedbackToUser, showHelp, exit, DisplayFormat.TEXT);
    }

    private CommandResult(String feedbackToUser, boolean showHelp, boolean exit, DisplayFormat displayFormat) {
        this.feedbackToUser = requireNonNull(feedbackToUser);
        this.displayFormat = requireNonNull(displayFormat);
        this.showHelp = showHelp;
        this.exit = exit;
    }

    /**
     * Constructs a {@code CommandResult} with the specified {@code feedbackToUser},
     * and other fields set to their default value.
     */
    public CommandResult(String feedbackToUser) {
        this(feedbackToUser, false, false);
    }

    /** Constructs feedback with the chosen presentation and no help or exit action. */
    public CommandResult(String feedbackToUser, DisplayFormat displayFormat) {
        this(feedbackToUser, false, false, displayFormat);
    }

    public String getFeedbackToUser() {
        return feedbackToUser;
    }

    public DisplayFormat getDisplayFormat() {
        return displayFormat;
    }

    public boolean isShowHelp() {
        return showHelp;
    }

    public boolean isExit() {
        return exit;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof CommandResult otherCommandResult)) {
            return false;
        }

        return feedbackToUser.equals(otherCommandResult.feedbackToUser)
                && displayFormat == otherCommandResult.displayFormat
                && showHelp == otherCommandResult.showHelp
                && exit == otherCommandResult.exit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(feedbackToUser, showHelp, exit, displayFormat);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("feedbackToUser", feedbackToUser)
                .add("displayFormat", displayFormat)
                .add("showHelp", showHelp)
                .add("exit", exit)
                .toString();
    }

}
