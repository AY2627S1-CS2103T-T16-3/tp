package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_EMPLOYEE;

import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.employee.Address;
import seedu.address.model.employee.Email;
import seedu.address.model.employee.Employee;
import seedu.address.model.employee.Name;
import seedu.address.model.employee.Phone;
import seedu.address.model.employee.Salary;

public class SalaryCommandTest {

    @Test
    public void execute_validIndex_updatesSalaryAndPreservesDetails() throws Exception {
        Model model = new ModelManager();
        Employee original = new Employee(new Name("Alice"), new Phone("91234567"),
                new Email("alice@example.com"), new Address("123 Main Street"), Set.of(), new Salary("1000"));
        model.addEmployee(original);
        Salary updatedSalary = new Salary("5000");

        CommandResult result = new SalaryCommand(INDEX_FIRST_EMPLOYEE, updatedSalary).execute(model);
        Employee updated = model.getFilteredEmployeeList().get(0);

        assertEquals(updatedSalary, updated.getSalary());
        assertEquals(original.getName(), updated.getName());
        assertEquals(original.getPhone(), updated.getPhone());
        assertEquals(original.getEmail(), updated.getEmail());
        assertEquals(original.getAddress(), updated.getAddress());
        assertEquals(original.getTags(), updated.getTags());
        assertEquals(String.format(SalaryCommand.MESSAGE_SUCCESS, Messages.format(updated)),
                result.getFeedbackToUser());
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        Model model = new ModelManager();
        SalaryCommand command = new SalaryCommand(INDEX_FIRST_EMPLOYEE, new Salary("5000"));

        assertCommandFailure(command, model, Messages.MESSAGE_INVALID_EMPLOYEE_DISPLAYED_INDEX);
    }
}
