package seedu.address.model.employee;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.tag.Tag;

/**
 * Represents an Employee in the address book.
 * Guarantees: contact details and tags are present, field values are validated, immutable.
 * Draft employees have no ID; AddressBook assigns one when they are inserted.
 */
public class Employee {

    // Identity fields
    private final Name name;
    private final Phone phone;
    private final Email email;
    private final EmployeeId employeeId;

    // Data fields
    private final Address address;
    private final Set<Tag> tags = new HashSet<>();

    /**
     * Constructs a draft. Contact details and tags must be present and not null.
     */
    public Employee(Name name, Phone phone, Email email, Address address, Set<Tag> tags) {
        this(name, phone, email, address, tags, null);
    }

    /**
     * Constructs an employee with an existing ID, or a draft with a null ID.
     * AddressBook assigns IDs to drafts when they are added.
     */
    public Employee(Name name, Phone phone, Email email, Address address, Set<Tag> tags, EmployeeId employeeId) {
        requireAllNonNull(name, phone, email, address, tags);
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.tags.addAll(tags);
        this.employeeId = employeeId;
    }

    /**
     * Returns the assigned ID, or empty for a draft that has not been added to AddressBook.
     */
    public Optional<EmployeeId> getEmployeeId() {
        return Optional.ofNullable(employeeId);
    }

    /**
     * Returns a copy with the given ID; all other fields are preserved.
     */
    public Employee withEmployeeId(EmployeeId id) {
        return new Employee(name, phone, email, address, tags, Objects.requireNonNull(id));
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

    /**
     * Returns true if both employees have the same name or the same assigned ID.
     * This defines a weaker notion of equality between two employees.
     */
    public boolean isSameEmployee(Employee otherEmployee) {
        if (otherEmployee == this) {
            return true;
        }

        return otherEmployee != null
                && (otherEmployee.getName().equals(getName())
                || employeeId != null && employeeId.equals(otherEmployee.employeeId));
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
                && Objects.equals(employeeId, otherEmployee.employeeId);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, phone, email, address, tags, employeeId);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("phone", phone)
                .add("email", email)
                .add("address", address)
                .add("tags", tags)
                .toString();
    }

}
