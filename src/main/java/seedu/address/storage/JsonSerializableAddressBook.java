package seedu.address.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.employee.Employee;
import seedu.address.model.employee.exceptions.DuplicateEmployeeException;
import seedu.address.model.employee.exceptions.EmployeeIdExhaustedException;

/**
 * An Immutable AddressBook that is serializable to JSON format.
 */
@JsonRootName(value = "addressbook")
class JsonSerializableAddressBook {

    public static final String MESSAGE_DUPLICATE_EMPLOYEE = "Employees list contains duplicate employee(s).";

    // Retain the existing JSON key so saved address books remain compatible.
    @JsonProperty("persons")
    private final List<JsonAdaptedEmployee> employees = new ArrayList<>();

    /**
     * Constructs a {@code JsonSerializableAddressBook} with the given employees.
     */
    @JsonCreator
    public JsonSerializableAddressBook(@JsonProperty("persons") List<JsonAdaptedEmployee> employees) {
        this.employees.addAll(employees);
    }

    /**
     * Converts a given {@code ReadOnlyAddressBook} into this class for Jackson use.
     *
     * @param source future changes to this will not affect the created {@code JsonSerializableAddressBook}.
     */
    public JsonSerializableAddressBook(ReadOnlyAddressBook source) {
        employees.addAll(source.getEmployeeList().stream().map(JsonAdaptedEmployee::new).collect(Collectors.toList()));
    }

    /**
     * Converts this address book into the model's {@code AddressBook} object.
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public AddressBook toModelType() throws IllegalValueException {
        AddressBook addressBook = new AddressBook();
        List<Employee> loadedEmployees = new ArrayList<>();
        for (JsonAdaptedEmployee jsonAdaptedEmployee : employees) {
            loadedEmployees.add(jsonAdaptedEmployee.toModelType());
        }
        try {
            addressBook.setEmployees(loadedEmployees);
        } catch (DuplicateEmployeeException e) {
            throw new IllegalValueException(MESSAGE_DUPLICATE_EMPLOYEE);
        } catch (EmployeeIdExhaustedException e) {
            throw new IllegalValueException(e.getMessage());
        }
        return addressBook;
    }

}
