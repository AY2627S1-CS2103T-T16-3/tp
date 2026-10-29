package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.scene.control.Label;
import seedu.address.model.employee.Employee;
import seedu.address.testutil.EmployeeBuilder;

public class EmployeeCardTest {
    @BeforeAll
    public static void startToolkit() throws Exception {
        System.setProperty("prism.order", "sw");
        FutureTask<Void> startup = new FutureTask<>(() -> Platform.setImplicitExit(false), null);
        try {
            Platform.startup(startup);
        } catch (IllegalStateException e) {
            // Other UI tests may have already started the shared JavaFX toolkit.
            Platform.runLater(startup);
        }
        startup.get(10, TimeUnit.SECONDS);
    }

    @Test
    public void constructor_validSalaries_displaysMonthlySalaryLabel() throws Exception {
        FutureTask<Void> task = new FutureTask<>(() -> {
            for (String salary : List.of("0", "005000", "50000")) {
                Employee employee = new EmployeeBuilder().withSalary(salary).build();
                EmployeeCard card = new EmployeeCard(employee, 1);
                Label salaryLabel = (Label) card.getRoot().lookup("#salary");

                assertEquals("Monthly salary: " + employee.getSalary(), salaryLabel.getText());
            }
        }, null);
        Platform.runLater(task);
        task.get(10, TimeUnit.SECONDS);
    }
}
