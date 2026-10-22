package seedu.address.storage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.employee.Address;
import seedu.address.model.employee.Email;
import seedu.address.model.employee.Employee;
import seedu.address.model.employee.EmployeeId;
import seedu.address.model.employee.LeaveEntitlement;
import seedu.address.model.employee.Name;
import seedu.address.model.employee.Phone;
import seedu.address.model.employee.Salary;
import seedu.address.model.employee.Task;
import seedu.address.model.tag.Tag;

/**
 * Jackson-friendly version of {@link Employee}.
 */
class JsonAdaptedEmployee {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Employee's %s field is missing!";

    @JsonProperty("employeeId")
    private final Integer employeeId;
    private final String name;
    private final String phone;
    private final String email;
    private final String address;
    private final String salary;
    @JsonProperty("leaveEntitlement")
    private final Integer leaveEntitlement;
    private final List<JsonAdaptedTag> tags = new ArrayList<>();
    private final List<JsonAdaptedTask> tasks = new ArrayList<>();

    /**
     * Constructs a {@code JsonAdaptedEmployee} with the given employee details.
     */
    @JsonCreator
    public JsonAdaptedEmployee(@JsonProperty("employeeId") Integer employeeId,
            @JsonProperty("name") String name, @JsonProperty("phone") String phone,
            @JsonProperty("email") String email, @JsonProperty("address") String address,
            @JsonProperty("salary") String salary,
            @JsonProperty("leaveEntitlement") Integer leaveEntitlement,
            @JsonProperty("tags") List<JsonAdaptedTag> tags,
            @JsonProperty("tasks") List<JsonAdaptedTask> tasks) {
        this.employeeId = employeeId;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.salary = salary;
        this.leaveEntitlement = leaveEntitlement;
        if (tags != null) {
            this.tags.addAll(tags);
        }
        if (tasks != null) {
            this.tasks.addAll(tasks);
        }
    }

    /**
     * Constructs a legacy employee without an internal employee ID.
     */
    public JsonAdaptedEmployee(String name, String phone, String email, String address,
            String salary, List<JsonAdaptedTag> tags, List<JsonAdaptedTask> tasks) {
        this(null, name, phone, email, address, salary, null, tags, tasks);
    }

    /**
     * Constructs an employee with an optional internal ID using the legacy fields.
     */
    public JsonAdaptedEmployee(Integer employeeId, String name, String phone, String email, String address,
            String salary, List<JsonAdaptedTag> tags, List<JsonAdaptedTask> tasks) {
        this(employeeId, name, phone, email, address, salary, null, tags, tasks);
    }

    /**
     * Converts a given {@code Employee} into this class for Jackson use.
     */
    public JsonAdaptedEmployee(Employee source) {
        employeeId = source.getEmployeeId().map(EmployeeId::getValue).orElse(null);
        name = source.getName().fullName;
        phone = source.getPhone().value;
        email = source.getEmail().value;
        address = source.getAddress().value;
        salary = source.getSalary().salary;
        leaveEntitlement = source.getLeaveEntitlement().value;
        tags.addAll(source.getTags().stream()
                .map(JsonAdaptedTag::new)
                .collect(Collectors.toList()));
        tasks.addAll(source.getTasks().stream()
                .map(JsonAdaptedTask::new)
                .collect(Collectors.toList()));
    }

    /**
     * Converts this Jackson-friendly adapted employee object into the model's {@code Employee} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted employee.
     */
    public Employee toModelType() throws IllegalValueException {
        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName()));
        }
        if (!Name.isValidName(name)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }
        final Name modelName = new Name(name);

        if (phone == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName()));
        }
        if (!Phone.isValidPhone(phone)) {
            throw new IllegalValueException(Phone.MESSAGE_CONSTRAINTS);
        }
        final Phone modelPhone = new Phone(phone);

        if (email == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Email.class.getSimpleName()));
        }
        if (!Email.isValidEmail(email)) {
            throw new IllegalValueException(Email.MESSAGE_CONSTRAINTS);
        }
        final Email modelEmail = new Email(email);

        if (address == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Address.class.getSimpleName()));
        }
        if (!Address.isValidAddress(address)) {
            throw new IllegalValueException(Address.MESSAGE_CONSTRAINTS);
        }
        final Address modelAddress = new Address(address);

        final List<Tag> employeeTags = new ArrayList<>();
        for (JsonAdaptedTag tag : tags) {
            employeeTags.add(tag.toModelType());
        }
        final Set<Tag> modelTags = new HashSet<>(employeeTags);

        final List<Task> modelTasks = new ArrayList<>();
        for (JsonAdaptedTask task : tasks) {
            Task modelTask = task.toModelType();
            boolean hasDuplicateDetails = modelTasks.stream()
                    .anyMatch(existingTask -> existingTask.hasSameDetails(modelTask));
            if (hasDuplicateDetails) {
                throw new IllegalValueException("Duplicate task details are not allowed.");
            }
            modelTasks.add(modelTask);
        }

        // Older save files have no salary field.
        String salaryValue = salary == null ? "0" : salary;
        if (!Salary.isValidSalary(salaryValue)) {
            throw new IllegalValueException(Salary.MESSAGE_CONSTRAINTS);
        }
        final Salary modelSalary = new Salary(salaryValue);

        int leaveEntitlementValue = leaveEntitlement == null ? 0 : leaveEntitlement;
        if (!LeaveEntitlement.isValidLeaveEntitlement(leaveEntitlementValue)) {
            throw new IllegalValueException(LeaveEntitlement.MESSAGE_CONSTRAINTS);
        }
        final LeaveEntitlement modelLeaveEntitlement = new LeaveEntitlement(leaveEntitlementValue);
        Employee employee = Employee.createUnassigned(modelName, modelPhone, modelEmail, modelAddress,
                modelSalary, modelLeaveEntitlement, modelTags, modelTasks);
        if (employeeId == null) {
            return employee;
        }
        if (!EmployeeId.isValidEmployeeId(employeeId)) {
            throw new IllegalValueException(EmployeeId.MESSAGE_CONSTRAINTS);
        }
        return employee.withEmployeeId(new EmployeeId(employeeId));
    }

}
