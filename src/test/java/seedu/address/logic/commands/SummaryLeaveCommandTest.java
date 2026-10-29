package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.SummaryTestUtil.assertSummaryValue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.AddressBookParser;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.employee.Employee;
import seedu.address.model.employee.EmployeeId;
import seedu.address.model.leave.LeavePeriod;
import seedu.address.testutil.EmployeeBuilder;

public class SummaryLeaveCommandTest {
    private final AddressBookParser parser = new AddressBookParser();

    @Test
    public void execute_summaryAliases_countRecordsAcrossYearsInsteadOfDays() throws Exception {
        Model model = modelWithLeaves();
        for (String command : List.of("summary", "/summary")) {
            CommandResult result = parser.parseCommand(command).execute(model);
            assertEquals(CommandResult.DisplayFormat.TABLE, result.getDisplayFormat());
            assertSummaryValue(result.getFeedbackToUser(), "Leave records", "3");
        }
    }

    @Test
    public void execute_scopes_countOnlySelectedEmployeesAndPreserveModel() throws Exception {
        Model model = modelWithLeaves();
        parser.parseCommand("find Alice").execute(model);
        AddressBook before = new AddressBook(model.getAddressBook());
        List<Employee> displayed = List.copyOf(model.getFilteredEmployeeList());

        assertLeaveCount(model, "summary", "2");
        assertLeaveCount(model, "summary scope/all", "3");

        assertEquals(before, model.getAddressBook());
        assertEquals(displayed, model.getFilteredEmployeeList());
    }

    @Test
    public void execute_indexSelections_applyInclusionsAndExclusionsToLeaveRecords() throws Exception {
        Model model = modelWithLeaves();
        assertLeaveCount(model, "summary i/1 1", "2");
        assertLeaveCount(model, "summary e/1", "1");
        assertLeaveCount(model, "summary i/1 2 e/1", "1");
        assertLeaveCount(model, "summary scope/all i/3", "0");
        assertLeaveCount(model, "summary e/1 2 3", "0");
    }

    @Test
    public void execute_sortedFilteredScope_usesDisplayedEmployeeIndexes() throws Exception {
        Model model = modelWithLeaves();
        parser.parseCommand("find Alice Bob").execute(model);
        parser.parseCommand("sort n/des").execute(model);

        assertLeaveCount(model, "summary i/1", "1");
        assertLeaveCount(model, "summary e/1", "2");
        assertEquals(List.of("Bob", "Alice"), model.getFilteredEmployeeList().stream()
                .map(employee -> employee.getName().fullName).toList());
    }

    @Test
    public void execute_allScopeAfterRepeatedSorts_countsLeavesUsingFullDisplayOrder() throws Exception {
        Model model = modelWithLeaves();
        parser.parseCommand("sort n/des").execute(model);
        parser.parseCommand("sort s/asc").execute(model);
        parser.parseCommand("list").execute(model);
        assertEquals(List.of("Carol", "Bob", "Alice"), model.getFilteredEmployeeList().stream()
                .map(employee -> employee.getName().fullName).toList());

        assertLeaveCount(model, "summary i/1", "0");
        assertLeaveCount(model, "summary scope/all i/1", "0");
        assertLeaveCount(model, "summary e/1", "3");
        assertLeaveCount(model, "summary scope/all e/1", "3");

        parser.parseCommand("find Bob").execute(model);
        assertLeaveCount(model, "summary i/1", "1");
        assertLeaveCount(model, "summary scope/all i/3", "2");
        assertEquals(List.of("Bob"), model.getFilteredEmployeeList().stream()
                .map(employee -> employee.getName().fullName).toList());
    }

    @Test
    public void execute_emptySelection_returnsZeroWithoutIncludingHiddenEmployees() throws Exception {
        Model model = modelWithLeaves();
        model.updateFilteredEmployeeList(employee -> false);

        assertLeaveCount(model, "summary", "0");
        assertLeaveCount(model, "summary scope/all", "3");
        assertLeaveCount(new ModelManager(), "summary", "0");
    }

    @Test
    public void execute_afterChanges_recountsLeaveRecordsUsingCurrentEmployeeIds() throws Exception {
        Model model = modelWithLeaves();
        Command command = parser.parseCommand("summary i/1");
        assertSummaryValue(command.execute(model).getFeedbackToUser(), "Leave records", "2");

        parser.parseCommand("leave delete 1").execute(model);
        parser.parseCommand("edit 1 n/Alicia").execute(model);
        assertSummaryValue(command.execute(model).getFeedbackToUser(), "Leave records", "1");

        parser.parseCommand("delete 1").execute(model);
        assertLeaveCount(model, "summary scope/all", "1");
    }

    private void assertLeaveCount(Model model, String command, String expected) throws Exception {
        assertSummaryValue(parser.parseCommand(command).execute(model).getFeedbackToUser(), "Leave records", expected);
    }

    private static Model modelWithLeaves() {
        Model model = new ModelManager();
        model.addEmployee(new EmployeeBuilder().withName("Alice").withEmployeeId(1).build());
        model.addEmployee(new EmployeeBuilder().withName("Bob").withEmployeeId(2).build());
        model.addEmployee(new EmployeeBuilder().withName("Carol").withEmployeeId(3).build());
        model.addLeave(new EmployeeId(1), new LeavePeriod(LocalDate.of(2025, 1, 6), LocalDate.of(2025, 1, 6)));
        model.addLeave(new EmployeeId(1), new LeavePeriod(LocalDate.of(2026, 1, 5), LocalDate.of(2026, 1, 9)));
        model.addLeave(new EmployeeId(2), new LeavePeriod(LocalDate.of(2027, 1, 4), LocalDate.of(2027, 1, 5)));
        return model;
    }
}
