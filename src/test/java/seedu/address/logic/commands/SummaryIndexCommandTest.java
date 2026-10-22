package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.SummaryTestUtil.assertSummaryValue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.SummaryCommandParser;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.employee.Employee;
import seedu.address.model.employee.Task;
import seedu.address.testutil.EmployeeBuilder;

public class SummaryIndexCommandTest {
    private static final Employee ALICE = new EmployeeBuilder().withName("Alice").withEmployeeId(1)
            .withTags("engineering").withSalary("1000").withTasks(new Task("Report")).build();
    private static final Employee BOB = new EmployeeBuilder().withName("Bob").withEmployeeId(2)
            .withTags("remote").withSalary("2000").build();
    private static final Employee CAROL = new EmployeeBuilder().withName("Carol").withEmployeeId(3)
            .withTags("engineering").withSalary("3000").withTasks(new Task("Review"), new Task("Plan")).build();

    @Test
    public void execute_includeIndexes_summarizesOnlySelectedEmployees() throws Exception {
        Model model = modelWithEmployees(ALICE, BOB, CAROL);
        CommandResult commandResult = new SummaryCommandParser().parse("i/3 1 3").execute(model);
        assertEquals(CommandResult.DisplayFormat.TABLE, commandResult.getDisplayFormat());
        String result = commandResult.getFeedbackToUser();
        assertTrue(result.contains("Included indexes: [1, 3]\n"));
        assertSummaryValue(result, "Employees", "2");
        assertSummaryValue(result, "Assigned tasks", "3");
        assertSummaryValue(result, "Total salary", "$4000.00");
        assertSummaryValue(result, "Average salary", "$2000.00");
    }

    @Test
    public void execute_excludeIndexes_summarizesRemainingEmployees() throws Exception {
        Model model = modelWithEmployees(ALICE, BOB, CAROL);
        String expected = new SummaryCommand().execute(modelWithEmployees(ALICE, CAROL)).getFeedbackToUser()
                .replace("Scope: Filtered employees", "Scope: Filtered employees\nExcluded indexes: [2]");
        assertEquals(expected, new SummaryCommandParser().parse("e/2").execute(model).getFeedbackToUser());
    }

    @Test
    public void execute_overlappingSelections_exclusionTakesPrecedence() throws Exception {
        String expected = new SummaryCommand().execute(modelWithEmployees(ALICE)).getFeedbackToUser()
                .replace("Scope: Filtered employees", "Scope: Filtered employees\nIncluded indexes: [1, 3]"
                        + "\nExcluded indexes: [3]");
        assertEquals(expected, new SummaryCommandParser().parse("i/1 3 e/3")
                .execute(modelWithEmployees(ALICE, BOB, CAROL)).getFeedbackToUser());
    }

    @Test
    public void execute_excludeAll_returnsEmptySummary() throws Exception {
        String expected = new SummaryCommand().execute(new ModelManager()).getFeedbackToUser()
                .replace("Scope: Filtered employees", "Scope: Filtered employees\nExcluded indexes: [1, 2]");
        assertEquals(expected, new SummaryCommandParser().parse("e/1 2")
                .execute(modelWithEmployees(ALICE, BOB)).getFeedbackToUser());
    }

    @Test
    public void execute_filteredScope_usesDisplayedIndexesAndPreservesModel() throws Exception {
        Model model = modelWithEmployees(ALICE, BOB, CAROL);
        model.updateFilteredEmployeeList(employee -> !employee.equals(ALICE));
        AddressBook before = new AddressBook(model.getAddressBook());
        String expected = new SummaryCommand().execute(modelWithEmployees(BOB)).getFeedbackToUser()
                .replace("Scope: Filtered employees", "Scope: Filtered employees\nIncluded indexes: [1]");
        assertEquals(expected, new SummaryCommandParser().parse("i/1").execute(model).getFeedbackToUser());
        assertEquals(before, model.getAddressBook());
        assertEquals(List.of(BOB, CAROL), model.getFilteredEmployeeList());

        Employee renamedAlice = new EmployeeBuilder(ALICE).withName("Alicia").build();
        model.setEmployee(ALICE, renamedAlice);
        assertEquals(List.of(renamedAlice, BOB, CAROL), model.getFilteredEmployeeList());
    }

    @Test
    public void execute_allScope_usesFullListIndexesAndPreservesFilter() throws Exception {
        Model model = modelWithEmployees(ALICE, BOB, CAROL);
        model.updateFilteredEmployeeList(employee -> employee.equals(CAROL));
        AddressBook before = new AddressBook(model.getAddressBook());
        String expected = new SummaryCommand(SummaryCommand.Scope.ALL)
                .execute(modelWithEmployees(ALICE, CAROL)).getFeedbackToUser()
                .replace("Scope: All employees", "Scope: All employees\nExcluded indexes: [2]");
        assertEquals(expected, new SummaryCommandParser().parse("scope/all e/2").execute(model).getFeedbackToUser());
        assertEquals(before, model.getAddressBook());
        assertEquals(List.of(CAROL), model.getFilteredEmployeeList());
    }

    @Test
    public void execute_invalidIndexes_throwsCommandExceptionWithoutChangingModel() throws Exception {
        Model model = modelWithEmployees(ALICE, BOB, CAROL);
        model.updateFilteredEmployeeList(employee -> employee.equals(BOB));
        AddressBook before = new AddressBook(model.getAddressBook());
        for (String input : new String[] {"i/2", "e/2", "i/1 e/2", "i/2 e/2", "scope/all i/4",
            "scope/all e/4"}) {
            SummaryCommand command = new SummaryCommandParser().parse(input);
            String expected = input.startsWith("scope/all")
                    ? "Index 4 is invalid. All employees: 3. Valid indexes: 1-3."
                    : "Index 2 is invalid. Filtered employees: 1. Valid indexes: 1-1.";
            assertThrows(CommandException.class, expected, () -> command.execute(model));
        }
        assertEquals(before, model.getAddressBook());
        assertEquals(List.of(BOB), model.getFilteredEmployeeList());
        model.updateFilteredEmployeeList(employee -> false);
        assertThrows(CommandException.class,
                "Index 1 is invalid. Filtered employees: 0. Valid indexes: none.", () ->
                new SummaryCommandParser().parse("i/1").execute(model));
        assertThrows(CommandException.class,
                "Index 1 is invalid. Filtered employees: 0. Valid indexes: none.", () ->
                new SummaryCommandParser().parse("e/1").execute(new ModelManager()));
    }

    @Test
    public void execute_multipleInvalidIndexes_reportsSmallestInvalidIndex() throws Exception {
        Model model = modelWithEmployees(ALICE, BOB, CAROL);
        SummaryCommand command = new SummaryCommandParser().parse("i/7 5 e/4");
        assertThrows(CommandException.class,
                "Index 4 is invalid. Filtered employees: 3. Valid indexes: 1-3.", () ->
                command.execute(model));
    }

    @Test
    public void execute_afterParsing_resolvesIndexesAgainstCurrentData() throws Exception {
        Model model = modelWithEmployees(ALICE, BOB, CAROL);
        SummaryCommand command = new SummaryCommandParser().parse("i/2");
        model.deleteEmployee(ALICE);
        assertSummaryValue(command.execute(model).getFeedbackToUser(), "Total salary", "$3000.00");
        model.deleteEmployee(BOB);
        assertThrows(CommandException.class,
                "Index 2 is invalid. Filtered employees: 1. Valid indexes: 1-1.", () ->
                command.execute(model));
    }

    @Test
    public void constructor_indexSets_areDefensivelyCopied() {
        Set<Index> included = new HashSet<>(indexes(1));
        Set<Index> excluded = new HashSet<>(indexes(2));
        SummaryCommand command = new SummaryCommand(SummaryCommand.Scope.FILTERED, included, excluded);
        included.clear();
        excluded.clear();
        assertEquals(new SummaryCommand(SummaryCommand.Scope.FILTERED, indexes(1), indexes(2)), command);
        assertThrows(NullPointerException.class, () ->
                new SummaryCommand(SummaryCommand.Scope.FILTERED, null, Set.of()));
        assertThrows(NullPointerException.class, () ->
                new SummaryCommand(SummaryCommand.Scope.FILTERED, Set.of(), null));
    }

    @Test
    public void equalsAndHashCode_compareScopeAndSelections() {
        SummaryCommand command = new SummaryCommand(SummaryCommand.Scope.FILTERED, indexes(1), indexes(2));
        SummaryCommand same = new SummaryCommand(SummaryCommand.Scope.FILTERED, indexes(1), indexes(2));
        assertEquals(command, same);
        assertEquals(command.hashCode(), same.hashCode());
        assertNotEquals(command, new SummaryCommand(SummaryCommand.Scope.ALL, indexes(1), indexes(2)));
        assertNotEquals(command, new SummaryCommand(SummaryCommand.Scope.FILTERED, indexes(2), indexes(1)));
        assertNotEquals(command, new SummaryCommand());
    }

    private static Set<Index> indexes(int index) {
        return Set.of(Index.fromOneBased(index));
    }

    private static Model modelWithEmployees(Employee... employees) {
        Model model = new ModelManager();
        for (Employee employee : employees) {
            model.addEmployee(employee);
        }
        return model;
    }
}
