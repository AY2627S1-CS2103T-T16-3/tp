package seedu.address.model;

import java.util.Comparator;
import java.util.function.Predicate;

import seedu.address.model.employee.Employee;

/**
 * Captures command data and display rules for restoration after a failed save.
 * Its contents are accessible only within the model component.
 */
public final class ModelSnapshot {
    final AddressBook addressBook;
    final Predicate<? super Employee> predicate;
    final Comparator<? super Employee> comparator;

    ModelSnapshot(ReadOnlyAddressBook addressBook, Predicate<? super Employee> predicate,
            Comparator<? super Employee> comparator) {
        this.addressBook = new AddressBook(addressBook);
        this.predicate = predicate;
        this.comparator = comparator;
    }
}
