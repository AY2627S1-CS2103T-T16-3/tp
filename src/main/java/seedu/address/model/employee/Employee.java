package seedu.address.model.employee;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.tag.Tag;

/**
 * Represents an Employee in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Employee {

    // Identity fields
    private final Name name;
    private final Phone phone;
    private final Email email;

    // Data fields
    private final Address address;
    private final Salary salary;
    private final Set<Tag> tags = new HashSet<>();
    private final List<Task> tasks = new ArrayList<>();

    /**
     * Every field must be present and not null.
     */
    public Employee(Name name, Phone phone, Email email, Address address, Salary salary,
            Set<Tag> tags, List<Task> tasks) {
        requireAllNonNull(name, phone, email, address, tags, salary, tasks);
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.salary = salary;
        this.tags.addAll(tags);
        this.tasks.addAll(tasks);
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
        if (tasks.contains(task)) {
            throw new IllegalArgumentException("Task already exists for this employee.");
        }
        List<Task> updatedTasks = new ArrayList<>(tasks);
        updatedTasks.add(task);
        return new Employee(name, phone, email, address, salary, tags, updatedTasks);
    }

    /** Deletes a task using a zero-based index. */
    public Employee deleteTask(int index) {
        if (index < 0 || index >= tasks.size()) {
            throw new IndexOutOfBoundsException("Task index is out of bounds.");
        }
        List<Task> updatedTasks = new ArrayList<>(tasks);
        updatedTasks.remove(index);
        return new Employee(name, phone, email, address, salary, tags, updatedTasks);
    }

    /** Returns the employee's salary. */
    public Salary getSalary() {
        return salary;
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

        return name.equals(otherEmployee.name)
                && phone.equals(otherEmployee.phone)
                && email.equals(otherEmployee.email)
                && address.equals(otherEmployee.address)
                && tags.equals(otherEmployee.tags)
                && tasks.equals(otherEmployee.tasks)
                && salary.equals(otherEmployee.salary);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, phone, email, address, tags, tasks, salary);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("phone", phone)
                .add("email", email)
                .add("address", address)
                .add("salary", salary)
                .add("tags", tags)
                .add("tasks", tasks)
                .toString();
    }

}
