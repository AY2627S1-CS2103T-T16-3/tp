package seedu.address.model.employee;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.tag.Tag;

/**
 * Represents an Employee in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Employee {

    // Identity fields
    private final Optional<EmployeeId> employeeId;
    private final Name name;
    private final Phone phone;
    private final Email email;

    // Data fields
    private final Address address;
    private final Salary salary;
    private final LeaveEntitlement leaveEntitlement;
    private final Set<Tag> tags = new HashSet<>();
    private final List<Task> tasks = new ArrayList<>();

    private Employee(Optional<EmployeeId> employeeId, Name name, Phone phone, Email email, Address address,
            Salary salary, LeaveEntitlement leaveEntitlement, Set<Tag> tags, List<Task> tasks) {
        requireAllNonNull(employeeId, name, phone, email, address, tags, salary, leaveEntitlement, tasks);
        this.employeeId = employeeId;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.salary = salary;
        this.leaveEntitlement = leaveEntitlement;
        this.tags.addAll(tags);
        this.tasks.addAll(tasks);
    }

    /**
     * Creates an employee whose internal identifier has not yet been assigned by the address book.
     */
    public static Employee createUnassigned(Name name, Phone phone, Email email, Address address, Salary salary,
            LeaveEntitlement leaveEntitlement, Set<Tag> tags, List<Task> tasks) {
        return new Employee(Optional.empty(), name, phone, email, address, salary, leaveEntitlement, tags, tasks);
    }

    /**
     * Returns a copy with the given internal identifier assigned.
     * This operation is only valid for an employee that has not already been assigned an identifier.
     */
    public Employee withEmployeeId(EmployeeId employeeId) {
        requireNonNull(employeeId);
        if (this.employeeId.isPresent()) {
            throw new IllegalStateException("Employee ID has already been assigned.");
        }
        return new Employee(Optional.of(employeeId), name, phone, email, address, salary, leaveEntitlement,
                tags, tasks);
    }

    public Optional<EmployeeId> getEmployeeId() {
        return employeeId;
    }

    public Name getName() {
        return name;
    }

    public Phone getPhone() {
        return phone;
    }

    public Email getEmail() {
        return email;
    }

    public Address getAddress() {
        return address;
    }

    /**
     * Returns a copy with updated employee details while preserving internal identity, salary, and tasks.
     */
    public Employee withDetails(Name name, Phone phone, Email email, Address address,
            LeaveEntitlement leaveEntitlement, Set<Tag> tags) {
        requireAllNonNull(name, phone, email, address, leaveEntitlement, tags);
        return new Employee(employeeId, name, phone, email, address, salary, leaveEntitlement, tags, tasks);
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    /** Returns an immutable task list. */
    public List<Task> getTasks() {
        return Collections.unmodifiableList(tasks);
    }

    /** Adds a task to this employee. */
    public Employee addTask(Task task) {
        requireNonNull(task);
        if (tasks.stream().anyMatch(existingTask -> existingTask.hasSameDetails(task))) {
            throw new IllegalArgumentException("Task already exists for this employee.");
        }
        List<Task> updatedTasks = new ArrayList<>(tasks);
        updatedTasks.add(task);
        return new Employee(employeeId, name, phone, email, address, salary, leaveEntitlement, tags, updatedTasks);
    }

    /** Deletes a task using a zero-based index. */
    public Employee deleteTask(int index) {
        if (index < 0 || index >= tasks.size()) {
            throw new IndexOutOfBoundsException("Task index is out of bounds.");
        }
        List<Task> updatedTasks = new ArrayList<>(tasks);
        updatedTasks.remove(index);
        return new Employee(employeeId, name, phone, email, address, salary, leaveEntitlement, tags, updatedTasks);
    }

    /** Replaces a task using a zero-based index. */
    public Employee updateTask(int index, Task task) {
        requireNonNull(task);
        if (index < 0 || index >= tasks.size()) {
            throw new IndexOutOfBoundsException("Task index is out of bounds.");
        }
        List<Task> updatedTasks = new ArrayList<>(tasks);
        updatedTasks.set(index, task);
        return new Employee(employeeId, name, phone, email, address, salary, leaveEntitlement, tags, updatedTasks);
    }

    /** Returns the employee's salary. */
    public Salary getSalary() {
        return salary;
    }

    /** Returns the employee's annual leave entitlement. */
    public LeaveEntitlement getLeaveEntitlement() {
        return leaveEntitlement;
    }

    /** Returns a copy with the updated salary while preserving internal identity and all other fields. */
    public Employee withSalary(Salary salary) {
        requireNonNull(salary);
        return new Employee(employeeId, name, phone, email, address, salary, leaveEntitlement, tags, tasks);
    }

    /**
     * Returns true if both employees have the same name.
     * This defines a weaker notion of equality between two employees.
     */
    public boolean isSameEmployee(Employee otherEmployee) {
        if (otherEmployee == this) {
            return true;
        }

        return otherEmployee != null
                && otherEmployee.getName().equals(getName());
    }

    /**
     * Returns true if both employees have the same identity and data fields.
     * This defines a stronger notion of equality between two employees.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Employee otherEmployee)) {
            return false;
        }

        return employeeId.equals(otherEmployee.employeeId)
                && name.equals(otherEmployee.name)
                && phone.equals(otherEmployee.phone)
                && email.equals(otherEmployee.email)
                && address.equals(otherEmployee.address)
                && tags.equals(otherEmployee.tags)
                && tasks.equals(otherEmployee.tasks)
                && salary.equals(otherEmployee.salary)
                && leaveEntitlement.equals(otherEmployee.leaveEntitlement);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(employeeId, name, phone, email, address, tags, tasks, salary, leaveEntitlement);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("phone", phone)
                .add("email", email)
                .add("address", address)
                .add("salary", salary)
                .add("leaveEntitlement", leaveEntitlement)
                .add("tags", tags)
                .add("tasks", tasks)
                .toString();
    }

}
