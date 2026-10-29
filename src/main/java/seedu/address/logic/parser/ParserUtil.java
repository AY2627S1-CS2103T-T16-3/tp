package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.employee.Address;
import seedu.address.model.employee.Email;
import seedu.address.model.employee.LeaveEntitlement;
import seedu.address.model.employee.Name;
import seedu.address.model.employee.Phone;
import seedu.address.model.employee.Salary;
import seedu.address.model.employee.Task;
import seedu.address.model.leave.LeaveId;
import seedu.address.model.tag.Tag;

/**
 * Contains utility methods used for parsing strings in the various *Parser classes.
 */
public class ParserUtil {

    public static final String MESSAGE_INVALID_INDEX = "Index must be a positive integer.";
    public static final String MESSAGE_INVALID_START_DATE =
            "Start date must be a valid date from year 0001 to 9999 in DD-MM-YYYY format.";
    public static final String MESSAGE_INVALID_END_DATE =
            "End date must be a valid date from year 0001 to 9999 in DD-MM-YYYY format.";

    private static final DateTimeFormatter LEAVE_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-uuuu",
            Locale.ENGLISH).withResolverStyle(ResolverStyle.STRICT);
    private static final String LEAVE_DATE_PATTERN = "[0-9]{2}-[0-9]{2}-[0-9]{4}";

    /**
     * Parses {@code oneBasedIndex} into an {@code Index} and returns it. Leading and trailing whitespaces will be
     * trimmed.
     * @throws ParseException if the specified index is invalid (not a non-zero unsigned integer).
     */
    public static Index parseIndex(String oneBasedIndex) throws ParseException {
        String trimmedIndex = oneBasedIndex.trim();
        if (!StringUtil.isNonZeroUnsignedInteger(trimmedIndex)) {
            throw new ParseException(MESSAGE_INVALID_INDEX);
        }
        return Index.fromOneBased(Integer.parseInt(trimmedIndex));
    }

    /**
     * Parses an annual leave entitlement in whole days. Leading and trailing whitespace is trimmed, and leading
     * zeros are accepted.
     */
    public static LeaveEntitlement parseLeaveEntitlement(String leaveEntitlement) throws ParseException {
        requireNonNull(leaveEntitlement);
        String trimmedEntitlement = leaveEntitlement.trim();
        if (!trimmedEntitlement.matches("[0-9]+")) {
            throw new ParseException(LeaveEntitlement.MESSAGE_CONSTRAINTS);
        }
        String normalizedEntitlement = trimmedEntitlement.replaceFirst("^0+(?!$)", "");
        try {
            int value = Integer.parseInt(normalizedEntitlement);
            if (!LeaveEntitlement.isValidLeaveEntitlement(value)) {
                throw new ParseException(LeaveEntitlement.MESSAGE_CONSTRAINTS);
            }
            return new LeaveEntitlement(value);
        } catch (NumberFormatException e) {
            throw new ParseException(LeaveEntitlement.MESSAGE_CONSTRAINTS);
        }
    }

    /** Parses a positive leave-record ID. */
    public static LeaveId parseLeaveId(String leaveId) throws ParseException {
        requireNonNull(leaveId);
        String trimmedLeaveId = leaveId.trim();
        if (!StringUtil.isNonZeroUnsignedInteger(trimmedLeaveId)) {
            throw new ParseException(LeaveId.MESSAGE_CONSTRAINTS);
        }
        return new LeaveId(Integer.parseInt(trimmedLeaveId));
    }

    /** Parses {@code startDate} as a strict calendar date in dd-MM-yyyy format. */
    public static LocalDate parseStartDate(String startDate) throws ParseException {
        return parseLeaveDate(startDate, MESSAGE_INVALID_START_DATE);
    }

    /** Parses {@code endDate} as a strict calendar date in dd-MM-yyyy format. */
    public static LocalDate parseEndDate(String endDate) throws ParseException {
        return parseLeaveDate(endDate, MESSAGE_INVALID_END_DATE);
    }

    private static LocalDate parseLeaveDate(String date, String errorMessage) throws ParseException {
        requireNonNull(date);
        String trimmedDate = date.trim();
        if (!trimmedDate.matches(LEAVE_DATE_PATTERN)) {
            throw new ParseException(errorMessage);
        }
        try {
            LocalDate parsedDate = LocalDate.parse(trimmedDate, LEAVE_DATE_FORMATTER);
            if (parsedDate.getYear() == 0) {
                throw new ParseException(errorMessage);
            }
            return parsedDate;
        } catch (DateTimeParseException e) {
            throw new ParseException(errorMessage);
        }
    }

    /**
     * Parses a {@code String name} into a {@code Name}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code name} is invalid.
     */
    public static Name parseName(String name) throws ParseException {
        requireNonNull(name);
        String trimmedName = name.trim();
        if (!Name.isValidName(trimmedName)) {
            throw new ParseException(Name.MESSAGE_CONSTRAINTS);
        }
        return new Name(trimmedName);
    }

    /**
     * Parses a {@code String phone} into a {@code Phone}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code phone} is invalid.
     */
    public static Phone parsePhone(String phone) throws ParseException {
        requireNonNull(phone);
        String trimmedPhone = phone.trim();
        if (!Phone.isValidPhone(trimmedPhone)) {
            throw new ParseException(Phone.MESSAGE_CONSTRAINTS);
        }
        return new Phone(trimmedPhone);
    }

    /**
     * Parses a {@code String address} into an {@code Address}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code address} is invalid.
     */
    public static Address parseAddress(String address) throws ParseException {
        requireNonNull(address);
        String trimmedAddress = address.trim();
        if (!Address.isValidAddress(trimmedAddress)) {
            throw new ParseException(Address.MESSAGE_CONSTRAINTS);
        }
        return new Address(trimmedAddress);
    }

    /**
     * Parses a {@code String email} into an {@code Email}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code email} is invalid.
     */
    public static Email parseEmail(String email) throws ParseException {
        requireNonNull(email);
        String trimmedEmail = email.trim();
        if (!Email.isValidEmail(trimmedEmail)) {
            throw new ParseException(Email.MESSAGE_CONSTRAINTS);
        }
        return new Email(trimmedEmail);
    }

    /**
     * Parses a {@code String tag} into a {@code Tag}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code tag} is invalid.
     */
    public static Tag parseTag(String tag) throws ParseException {
        requireNonNull(tag);
        String trimmedTag = tag.trim();
        if (!Tag.isValidTagName(trimmedTag)) {
            throw new ParseException(Tag.MESSAGE_CONSTRAINTS);
        }
        return new Tag(trimmedTag);
    }

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>}.
     */
    public static Set<Tag> parseTags(Collection<String> tags) throws ParseException {
        requireNonNull(tags);
        final Set<Tag> tagSet = new HashSet<>();
        for (String tagName : tags) {
            tagSet.add(parseTag(tagName));
        }
        return tagSet;
    }

    /**
     * Parses a {@code String salary} into a {@code Salary}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code salary} is invalid.
     */
    public static Salary parseSalary(String salary) throws ParseException {
        requireNonNull(salary);
        String trimmedSalary = salary.trim();
        if (!Salary.isValidSalary(trimmedSalary)) {
            throw new ParseException(Salary.MESSAGE_CONSTRAINTS);
        }
        return new Salary(trimmedSalary);
    }

    /**
     * Parses task details after trimming leading and trailing whitespace.
     *
     * @param details Task details to parse.
     * @return The parsed task.
     * @throws ParseException If the task details are invalid.
     */
    public static Task parseTask(String details) throws ParseException {
        requireNonNull(details);
        try {
            return new Task(details);
        } catch (IllegalArgumentException exception) {
            throw new ParseException(Task.MESSAGE_CONSTRAINTS, exception);
        }
    }

    /**
     * Replaces escaped slashes in task text with literal slashes.
     *
     * @param taskText Task text containing zero or more escaped slashes.
     * @return Task text with each {@code \/} replaced by {@code /}.
     */
    public static String unescapeTaskText(String taskText) {
        requireNonNull(taskText);
        return taskText.replace("\\/", "/");
    }
}
