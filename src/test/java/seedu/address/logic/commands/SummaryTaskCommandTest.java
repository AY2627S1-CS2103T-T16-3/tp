package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        String expected = "Employee summary\nTotal employees: 3\nEmployees without tags: 3\n\n"
                + "No tags to summarize.\n\nTask overview\nTotal assigned tasks: 3\n"
                + "Employees with tasks: 2\nEmployees without tasks: 1\nMost tasks assigned to one employee: 2";
        assertEquals(expected, new SummaryCommand().execute(model).getFeedbackToUser());
        assertEquals(List.of(alice, bob, carol), model.getAddressBook().getEmployeeList());
        assertEquals(List.of(alice), model.getFilteredEmployeeList());
    }

    @Test
    public void execute_afterTaskAndEmployeeChanges_recomputesCounts() {
        Model model = new ModelManager();
        Employee alice = ALICE;
        model.addEmployee(alice);
        SummaryCommand summary = new SummaryCommand();
        assertTrue(summary.execute(model).getFeedbackToUser().contains("Total assigned tasks: 0"));
        Employee assigned = alice.addTask(new Task("Report"));
        model.setEmployee(alice, assigned);
        assertTrue(summary.execute(model).getFeedbackToUser().endsWith("Total assigned tasks: 1\n"
                + "Employees with tasks: 1\nEmployees without tasks: 0\nMost tasks assigned to one employee: 1"));
        Employee cleared = assigned.deleteTask(0);
        model.setEmployee(assigned, cleared);
        assertTrue(summary.execute(model).getFeedbackToUser().contains("Employees without tasks: 1"));
        model.deleteEmployee(cleared);
        assertTrue(summary.execute(model).getFeedbackToUser().endsWith("Total assigned tasks: 0\n"
                + "Employees with tasks: 0\nEmployees without tasks: 0\nMost tasks assigned to one employee: 0"));
    }
}
