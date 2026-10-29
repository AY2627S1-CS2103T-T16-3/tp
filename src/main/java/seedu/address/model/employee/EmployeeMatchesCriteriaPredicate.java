package seedu.address.model.employee;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Matches employees against all supplied field criteria. Text matches are case-insensitive substrings;
 * task matches are case-insensitive substrings, tag matches are case-insensitive whole tag names,
 * and salary and annual leave entitlement support comparisons and inclusive ranges.
 */
public class EmployeeMatchesCriteriaPredicate implements Predicate<Employee> {

    /**
     * Fields available for filtering.
     */
    public enum Field {
        NAME, PHONE, EMAIL, ADDRESS, TAG, TASK, SALARY, LEAVE_ENTITLEMENT
    }

    private final Map<Field, List<String>> criteria;

    /**
     * Creates an immutable predicate with at least one non-empty criterion.
     */
    public EmployeeMatchesCriteriaPredicate(Map<Field, List<String>> criteria) {
        requireNonNull(criteria);
        checkArgument(!criteria.isEmpty(), "At least one filter criterion is required");
        Map<Field, List<String>> copy = new EnumMap<>(Field.class);
        criteria.forEach((field, values) -> {
            requireNonNull(field);
            requireNonNull(values);
            checkArgument(!values.isEmpty(), "A field must have at least one criterion");
            values.forEach(value -> {
                requireNonNull(value);
                checkArgument(!value.isBlank(), "Filter criteria must not be blank");
            });
            copy.put(field, List.copyOf(values));
        });
        this.criteria = Map.copyOf(copy);
    }

    @Override
    public boolean test(Employee employee) {
        requireNonNull(employee);
        return criteria.entrySet().stream().allMatch(entry -> entry.getValue().stream()
                .allMatch(value -> matches(employee, entry.getKey(), value)));
    }

    private boolean matches(Employee employee, Field field, String value) {
        return switch (field) {
            case NAME -> containsIgnoreCase(employee.getName().fullName, value);
            case PHONE -> containsIgnoreCase(employee.getPhone().value, value);
            case EMAIL -> containsIgnoreCase(employee.getEmail().value, value);
            case ADDRESS -> containsIgnoreCase(employee.getAddress().value, value);
            case TASK -> employee.getTasks().stream()
                    .anyMatch(task -> containsIgnoreCase(task.getDetails(), value));
            case SALARY -> matchesAmount(employee.getSalary().salary, value);
            case LEAVE_ENTITLEMENT -> matchesAmount(Integer.toString(employee.getLeaveEntitlement().value), value);
            case TAG -> employee.getTags().stream().anyMatch(tag -> tag.tagName.equalsIgnoreCase(value));
        };
    }

    private boolean matchesAmount(String value, String criterion) {
        int amount = Integer.parseInt(value);
        if (criterion.contains("-")) {
            String[] bounds = criterion.split("-");
            return amount >= Integer.parseInt(bounds[0])
                    && amount <= Integer.parseInt(bounds[1]);
        }
        if (criterion.startsWith("<=") || criterion.startsWith(">=")) {
            int comparison = Integer.compare(amount, Integer.parseInt(criterion.substring(2)));
            return criterion.startsWith("<=") ? comparison <= 0 : comparison >= 0;
        }
        if (criterion.startsWith("<") || criterion.startsWith(">")) {
            int comparison = Integer.compare(amount, Integer.parseInt(criterion.substring(1)));
            return criterion.startsWith("<") ? comparison < 0 : comparison > 0;
        }
        return amount == Integer.parseInt(criterion);
    }

    private boolean containsIgnoreCase(String fieldValue, String query) {
        return fieldValue.toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT));
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof EmployeeMatchesCriteriaPredicate otherPredicate
                && criteria.equals(otherPredicate.criteria);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("criteria", criteria).toString();
    }
}
