package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.SummaryTestUtil.assertSummaryValue;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.employee.Employee;
import seedu.address.model.employee.Task;
import seedu.address.testutil.EmployeeBuilder;

public class SummaryTaskCommandTest {
    private static final Employee ALICE = new EmployeeBuilder().withName("Alice").withEmployeeId(1).build();
    private static final Employee BOB = new EmployeeBuilder().withName("Bob").withEmployeeId(2)
            .withTasks(new Task("Report"), new Task("Review")).build();
    private static final Employee CAROL = new EmployeeBuilder().withName("Carol").withEmployeeId(3).build();

    @Test
    public void execute_noTagsAndFilteredView_countsAllTasksWithoutChangingModel() {
        Model model = new ModelManager();
        Employee alice = ALICE.addTask(new Task("Report"));
        Employee bob = BOB;
        Employee carol = CAROL;
        model.addEmployee(alice);
        model.addEmployee(bob);
        model.addEmployee(carol);
        model.updateFilteredEmployeeList(employee -> employee.equals(alice));
        String result = new SummaryCommand(SummaryCommand.Scope.ALL).execute(model).getFeedbackToUser();
        assertSummaryValue(result, "Employees", "3");
        assertSummaryValue(result, "Assigned tasks", "3");
        assertSummaryValue(result, "Employees with tasks", "2");
        assertEquals(List.of(alice, bob, carol), model.getAddressBook().getEmployeeList());
        assertEquals(List.of(alice), model.getFilteredEmployeeList());
    }

    @Test
    public void execute_afterTaskAndEmployeeChanges_recomputesCounts() {
        Model model = new ModelManager();
        Employee alice = ALICE;
        model.addEmployee(alice);
        SummaryCommand summary = new SummaryCommand();
        assertSummaryValue(summary.execute(model).getFeedbackToUser(), "Assigned tasks", "0");
        Employee assigned = alice.addTask(new Task("Report"));
        model.setEmployee(alice, assigned);
        String result = summary.execute(model).getFeedbackToUser();
        assertSummaryValue(result, "Assigned tasks", "1");
        assertSummaryValue(result, "Employees with tasks", "1");
        Employee cleared = assigned.deleteTask(0);
        model.setEmployee(assigned, cleared);
        assertSummaryValue(summary.execute(model).getFeedbackToUser(), "Employees with tasks", "0");
        model.deleteEmployee(cleared);
        result = summary.execute(model).getFeedbackToUser();
        assertSummaryValue(result, "Employees", "0");
        assertSummaryValue(result, "Assigned tasks", "0");
    }
}
