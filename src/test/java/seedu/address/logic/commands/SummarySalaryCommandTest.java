package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.SummaryTestUtil.assertSummaryValue;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.employee.Employee;
import seedu.address.model.employee.Salary;
import seedu.address.testutil.EmployeeBuilder;

public class SummarySalaryCommandTest {
    @Test
    public void execute_noTagsAndFilteredList_showsSalaryStatisticsForAllEmployees() {
        Model model = new ModelManager();
        Employee alice = new EmployeeBuilder().withName("Alice").withSalary("3000").build();
        Employee bob = new EmployeeBuilder().withName("Bob").withSalary("5000").build();
        model.addEmployee(alice);
        model.addEmployee(bob);
        Employee storedAlice = model.getAddressBook().getEmployeeList().get(0);
        Employee storedBob = model.getAddressBook().getEmployeeList().get(1);
        model.updateFilteredEmployeeList(employee -> employee.equals(storedAlice));
        String result = new SummaryCommand(SummaryCommand.Scope.ALL).execute(model).getFeedbackToUser();
        assertSummaryValue(result, "Total salary", "$8000.00");
        assertSummaryValue(result, "Average salary", "$4000.00");
        assertEquals(List.of(storedAlice, storedBob), model.getAddressBook().getEmployeeList());
        assertEquals(List.of(storedAlice), model.getFilteredEmployeeList());
    }

    @Test
    public void execute_afterSalaryCommand_recomputesStatistics() throws Exception {
        Model model = new ModelManager();
        model.addEmployee(new EmployeeBuilder().withSalary("1000").build());
        SummaryCommand summary = new SummaryCommand();
        assertSummaryValue(summary.execute(model).getFeedbackToUser(), "Total salary", "$1000.00");
        new SalaryCommand(Index.fromOneBased(1), new Salary("5000")).execute(model);
        String result = summary.execute(model).getFeedbackToUser();
        assertSummaryValue(result, "Total salary", "$5000.00");
        assertSummaryValue(result, "Average salary", "$5000.00");
    }
}
