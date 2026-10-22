---
layout: page
title: User Guide
---

HR Book is a **desktop application for managing contacts, optimized for use through a Command Line Interface (CLI)** while retaining the benefits of a Graphical User Interface (GUI). If you type quickly, HR Book can help you manage contacts faster than traditional GUI applications.

* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## Quick start

1. Ensure that Java `25` or later is installed on your computer.<br>
   **Mac users:** Ensure you have the precise JDK version prescribed [here](https://se-education.org/guides/tutorials/javaInstallationMac.html).

1. Download the latest `.jar` file from [here](https://github.com/se-edu/addressbook-level3/releases).

1. Copy the file to the folder you want to use as the _home folder_ for your HR Book.

1. Open a terminal, `cd` to the folder containing the JAR file, and run `java -jar hr-book.jar`.<br>
   A GUI similar to the one below should appear in a few seconds. Note how the app contains some sample data.<br>
   ![Ui](images/Ui.png)

1. Type a command in the command box and press Enter to execute it. For example, type **`help`** and press Enter to open the help window.<br>
   Some example commands you can try:

   * `list` : Lists all contacts.

   * `add n/John Doe p/98765432 e/johnd@example.com a/John street, block 123, #01-01 le/14` : Adds an
     employee named `John Doe` with 14 days of annual leave entitlement.

   * `leave add 1 from/20-10-2026 to/22-10-2026` : Records leave for the first employee currently shown.

   * `leave list` : Lists leave records for the employees currently displayed and shows each record's current status.

   * `delete 3` : Deletes the 3rd employee shown in the current list and their leave records.

   * `clear` : Deletes all employee and leave records.

   * `exit` : Exits the app.

1. Refer to the [Features](#features) section below for details of each command.

--------------------------------------------------------------------------------------------------------------------

## Features

<div markdown="block" class="alert alert-info">

**:information_source: Notes about the command format:**<br>

* Words in `UPPER_CASE` are the parameters to be supplied by the user.<br>
  For example, in `add n/NAME`, replace `NAME` with a value such as `John Doe`.

* Items in square brackets are optional.<br>
  For example, `n/NAME [t/TAG]` can be used as `n/John Doe t/friend` or as `n/John Doe`.

* Items followed by `…`​ can appear zero or more times.<br>
  For example, `[t/TAG]…​` may be omitted, or written as `t/friend` or `t/friend t/family`.

* Parameters can be in any order.<br>
  For example, if the command specifies `n/NAME p/PHONE_NUMBER`, `p/PHONE_NUMBER n/NAME` is also acceptable.

* Extraneous parameters for commands that take no parameters, such as `help`, `list`, `exit`, and `clear`, are ignored.<br>
  For example, `help 123` is interpreted as `help`.
  Exception: `/summary` and `summary` accept only an optional `scope/all` or `scope/filtered` parameter.

* If you are using a PDF version of this document, be careful when copying and pasting commands that span multiple lines as space characters surrounding line-breaks may be omitted when copied over to the application.
</div>

### Viewing help: `help`

Shows a message explaining how to access the help page.

![help message](images/helpMessage.png)

Format: `help`


### Adding an employee: `add`

Adds an employee to the address book.

Format: `add n/NAME p/PHONE_NUMBER e/EMAIL a/ADDRESS le/ANNUAL_LEAVE_DAYS [s/SALARY] [t/TAG]…​`

Salary is optional and defaults to `0`. It must be a whole number from `0` to `50000`.
Leading zeros are accepted and removed: `s/000000001` is stored as `1`.
Employee cards display salary with a `$` prefix, for example `$5000`.

Annual leave entitlement is compulsory. It must be a whole number from `0` to `365`. A value of `0` is valid for an
employee who is not entitled to annual leave. Leading zeros are accepted and removed: for example, `le/00014` is
stored as `14`. The value after removing leading zeros must still be within the allowed range.

<div markdown="span" class="alert alert-primary">:bulb: **Tip:**
An employee can have any number of tags, including zero.
</div>

Examples:
* `add n/John Doe p/98765432 e/johnd@example.com a/John street, block 123, #01-01 le/14`
* `add n/Betsy Crowe t/friend e/betsycrowe@example.com a/Newgate Prison p/1234567 le/20 t/criminal`

### Listing all employees: `list`

Shows a list of all employees in the address book.

Format: `list`

### Viewing the workforce summary: `summary`

Shows employee counts by tag, task-assignment counts, and recorded salary statistics in the selected scope.

Format: `summary [scope/all|scope/filtered]` (alias: `/summary`)

Omit the parameter or use `scope/filtered` to summarize the currently displayed employees after `find`
or `filter`. For example, run `filter t/engineering` then `summary`. Use `summary scope/all` to summarize
all stored employees while keeping the current filter active.
Values are case-sensitive and must immediately follow `scope/`; surrounding whitespace is allowed.
Empty, unknown, repeated parameters and extra text report a usage error, e.g. `summary t/engineering`.

Without a parameter the summary includes **only the currently displayed employees**. Run `list` first
to display and summarize everyone, or use `summary scope/all` without changing the displayed list.
The output labels its scope explicitly. An empty filtered result produces zero counts, not a fallback
to the full book. Neither scope changes employee records or the current filter. Run the command again
after changes to see fresh counts; with no active filter, both scopes include the same employees.

Example output:

```text
Employee summary
Scope: Filtered employees
Total employees: 5
Employees without tags: 1

Employees by tag:
  engineering: 3
  intern: 1
  remote: 2

Employees may have multiple tags; tag counts can overlap.

Task overview
Total assigned tasks: 6
Employees with tasks: 3
Employees without tasks: 2
Most tasks assigned to one employee: 3

Salary overview
Total recorded salary: $15000.00
Average salary: $3000.00
Lowest salary: $0.00
Highest salary: $5000.00
```

* Tags are grouped case-insensitively, displayed in lowercase, and sorted alphabetically.
  An employee tagged with both `Engineering` and `engineering` counts once in that group.
* Employees with several different tags count in each group, so tag counts can exceed the total headcount.
* Having no tags is allowed; the untagged count is a categorization aid, not an error.
* If there are no tags, the output says `No tags to summarize.` An empty book shows both counts as `0`.
* These are counts of stored records, not attendance or active-employment figures. Arbitrary tags are
  not interpreted as structured departments. Leave balances and reminders are not included.
* Salary statistics include every salary in the selected scope, including zero. For no employees, the total is `$0.00`
  and average/lowest/highest are `N/A`. Amounts display two decimal places; averages round half up.
* These are recorded amounts, not a calculation of monthly/annual payroll, tax, benefits, or payments.
  Salary statistics still appear when there are no tags and refresh after a salary change.

The result appears in the existing command-result area; scroll within it to read a long summary.

Task counts use the selected scope, like headcount and salary statistics. The same description assigned to two employees
counts as two assignments. An empty book shows zero for every task statistic; if employees have no
tasks, the employees-without-tasks count equals headcount. Task statistics appear even without tags.
Run the summary again after task changes or employee deletion to refresh it. These counts do not
indicate completion, deadlines, effort, or productivity; those properties are not stored for tasks.

### Changing an employee's salary: `salary`

Changes the salary of an existing employee in the address book.

Format: `salary INDEX s/SALARY`

* Updates the employee at the specified `INDEX` in the currently displayed employee list, including search results. The index **must be a positive integer** 1, 2, 3, …​
* The salary is required and must contain only digits, representing a whole number from `0` to `50000` inclusive. Do not include a `$` sign, commas, or decimal points.
* Supply `s/` exactly once. The new salary replaces the employee's existing salary; other employee details are preserved.
* Leading zeros are accepted and removed: `s/005000` is stored as `5000` and displayed as `$5000`.

Examples:
* `salary 1 s/5000` changes the 1st employee's salary in the displayed list to `$5000`.
* `find Betsy` followed by `salary 1 s/6000` changes the salary of the 1st employee in the search results to `$6000`.

### Editing an employee: `edit`

Edits an existing employee in the address book.

Salary is preserved when editing other details. The `edit` command rejects `s/`;
Use `salary INDEX s/SALARY` to change salary.

Format: `edit INDEX [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS] [le/ANNUAL_LEAVE_DAYS] [t/TAG]…​`

* Edits the employee at the specified `INDEX`. The index refers to the index number shown in the displayed employee list. The index **must be a positive integer** 1, 2, 3, …​
* At least one of the optional fields must be provided.
* Existing values will be updated to the input values.
* `le/ANNUAL_LEAVE_DAYS` changes the employee's annual entitlement and accepts a whole number from `0` to `365`.
  Leading zeros are accepted and removed: for example, `le/00021` is stored as `21`.
  Existing leave records are not deleted if the entitlement is reduced below the number of days already used. Until
  enough leave is deleted or the entitlement is increased, no further leave can be added for that year.
* When editing tags, all of the employee's existing tags are removed; adding tags is not cumulative.
* To remove all of an employee's tags, enter `t/` without a tag after it.

Examples:
*  `edit 1 p/91234567 e/johndoe@example.com` Edits the phone number and email address of the 1st employee to be `91234567` and `johndoe@example.com` respectively.
*  `edit 2 n/Betsy Crower t/` Edits the name of the 2nd employee to be `Betsy Crower` and clears all existing tags.
*  `edit 3 le/00021` Changes the 3rd employee's annual leave entitlement to 21 days.

### Locating employees by name: `find`

Finds employees whose names contain any of the given keywords.

Format: `find KEYWORD [MORE_KEYWORDS]`

* The search is case-insensitive; for example, `hans` matches `Hans`.
* Keyword order does not matter; for example, `Hans Bo` matches `Bo Hans`.
* The search considers only names.
* Only full words match; for example, `Han` does not match `Hans`.
* Employees matching at least one keyword are returned (an `OR` search); for example, `Hans Bo` returns `Hans Gruber` and `Bo Yang`.

Examples:
* `find John` returns `john` and `John Doe`
* `find alex david` returns `Alex Yeoh`, `David Li`<br>
  ![result for 'find alex david'](images/findAlexDavidResult.png)

### Filtering employees by any field: `filter`

Lists employees who match all supplied criteria.

Format: `filter [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS] [t/TAG]... [task/DETAILS]... [s/SALARY]`

* Supply at least one non-empty criterion.
* Name, phone, email, and address use case-insensitive substring matching. For example, `n/ali` matches `Alice Pauline`, and `p/912` matches a phone number containing `912`.
* Task descriptions use case-insensitive substring matching. `task/report` matches an employee with a task containing `report`.
* Salary matches an exact amount from 0 to 50000. Leading zeros are allowed: `s/003000` matches salary 3000.
* Tags match the whole tag name, ignoring case. For example, `t/friend` does not match the tag `friends`.
* All supplied criteria must match (`AND`). Repeated `t/` criteria require every specified tag; repeated `task/` criteria require a matching task for each query. Different queries may match different tasks or the same task.
* Each of `n/`, `p/`, `e/`, `a/`, and `s/` may appear only once. Unsupported prefixes are rejected.
* Every filter searches the entire address book, including employees hidden by an earlier search.
* Use `list` to show all employees again. Filtering does not modify employee details.

Examples:
* `filter p/91235321` lists employees whose phone number contains `91235321`.
* `filter e/@example.com` lists employees with an email address containing `@example.com`.
* `filter n/Alice a/Jurong` lists employees whose name contains `Alice` and whose address contains `Jurong`.
* `filter t/friends t/owesMoney` lists employees with both tags.
* `filter task/report task/review` lists employees with tasks matching both descriptions.
* `filter task/report s/3000` lists employees with a matching task and salary 3000.

### Deleting an employee: `delete`

Deletes the specified employee from the address book.

Format: `delete INDEX`

* Deletes the employee at the specified `INDEX`.
* The index refers to the index number shown in the displayed employee list.
* The index **must be a positive integer** 1, 2, 3, …​
* All leave records belonging to the employee are also deleted. They cannot become orphaned records that refer to a
  missing employee.

Examples:
* `list` followed by `delete 2` deletes the 2nd employee in the address book.
* `find Betsy` followed by `delete 1` deletes the 1st employee in the results of the `find` command.

### Managing employee leave

Leave records are stored separately from employee records and linked internally to the correct employee. You select an
employee using the index currently displayed in the employee list. Internal employee IDs are not shown and are not
accepted by leave commands.

Each recorded leave has a stable, public leave ID. Use `leave list` to find this ID before deleting a leave record. As
`leave list` follows the currently displayed employee list, run `list` first if you need to find a leave record for any
employee.

#### Recording leave: `leave add`

Records one leave period for the employee at the displayed index.

Format: `leave add INDEX from/START_DATE to/END_DATE`

* `INDEX` refers to the index shown in the current employee list and must be a positive integer.
* Dates must use the `DD-MM-YYYY` format. Both dates are included in the leave period.
* The start date must be on or before the end date, and both dates must be in the same calendar year.
* Only Mondays to Fridays count against the employee's entitlement. Saturdays and Sundays are excluded.
* Public holidays are not excluded in the MVP. A public holiday that falls from Monday to Friday counts as a working
  leave day.
* A period containing no weekday is rejected.
* Leave periods for the same employee cannot overlap, including on a weekend or boundary date. Leave periods belonging
  to different employees may overlap.
* The number of weekdays requested cannot exceed the employee's remaining entitlement for that calendar year.

The success message shows the new leave ID, employee name, period, charged working days, and remaining leave for the
year.

Examples:
* `list` followed by `leave add 1 from/20-10-2026 to/22-10-2026` records three working days for the first employee.
* `find Betsy` followed by `leave add 1 from/23-10-2026 to/26-10-2026` records two working days for Betsy, excluding
  the weekend.

#### Listing leave: `leave list`

Lists leave records belonging to employees in the currently displayed employee list, ordered by leave ID.

Format: `leave list`

The command uses whichever employees are currently displayed. For example, after `find Betsy` or a `filter` command,
only leave records belonging to the matching employees are listed. Run `list` before `leave list` to display leave
records for all employees. If none of the displayed employees has a leave record, the command reports that no leave
records were found.

Each line shows the leave ID, employee name, inclusive date range, charged working days, and one of these statuses:

* `Upcoming`: today's date is before the start date.
* `Ongoing`: today's date falls within the inclusive date range.
* `Past`: today's date is after the end date.

The status is calculated whenever the command runs and is not saved. An ongoing period remains `Ongoing` when today is
a weekend inside that period, even though weekends do not consume entitlement.

Example output:

```text
Leave records:
ID 1 | John Doe | 20-10-2026 to 22-10-2026 | 3 working day(s) | Upcoming
```

#### Deleting leave: `leave delete`

Deletes the leave record with the specified leave ID.

Format: `leave delete LEAVE_ID`

* `LEAVE_ID` must be a positive integer displayed by `leave list`; it is not an employee list index.
* Deleting a record restores those days to the employee's remaining entitlement for that calendar year because the
  balance is recalculated from the records that remain.
* Deleted leave IDs are not reused.

Example:
* `leave list` followed by `leave delete 2` deletes the leave record whose ID is `2`.

### Listing tasks on an employee: `list-tasks`

Lists the tasks related to an employee.

Format: `list-tasks EMPLOYEE_INDEX`

* `EMPLOYEE_INDEX` refers to the employee's index in the currently displayed
  employee list and must be a positive integer.
* Tasks are displayed in the order in which they were added.
* The current filtered employee list is preserved after the command.

Example:

* `list-tasks 1` lists the tasks related to the first displayed employee.

### Adding tasks to an employee: `add-task`

Adds a task to an employee.

Format: `add-task EMPLOYEE_INDEX d/DETAILS`

* `EMPLOYEE_INDEX` refers to the employee's index in the currently displayed
  employee list and must be a positive integer.
* `DETAILS` must not be empty, and it must not exceed 120 characters
* A task that already exists for the employee cannot be added again.
  Task comparisons ignore leading/trailing whitespace, repeated whitespace,
  and capitalization. For example, `Prepare report` and `  prepare   REPORT  `
  are treated as duplicates.
* The current filtered employee list is preserved after the command.

Examples:

* `add-task 1 d/Prepare monthly report` adds the task to the first displayed
  employee.
* `find Alex` followed by `add-task 1 d/Prepare monthly report` adds the
  task to the first employee in the filtered results.

### Deleting tasks on an employee: `delete-task`

Deletes a task related to an employee.

Format: `delete-task EMPLOYEE_INDEX i/TASK_INDEX`

* `EMPLOYEE_INDEX` refers to the employee's index in the currently displayed
  employee list and must be a positive integer.
* `TASK_INDEX` refers to the task's index in that employee's task list and
  must be a positive integer.
* The current filtered employee list is preserved after the command.

Examples:

* `delete-task 1 i/2` deletes the second task assigned to the first displayed
  employee.
* `find Alex` followed by `delete-task 1 i/1` deletes the first task from the
  first employee in the filtered results.

### Clearing all entries: `clear`

Clears all employee and leave records from HR Book.

Format: `clear`

### Exiting the program: `exit`

Exits the program.

Format: `exit`

### Saving the data

HR Book automatically saves employee and leave data after every command. You do not need to save manually.

Older save files without salary values load with a salary of `0`.

### Editing the data file

HR Book data is saved automatically as a JSON file `[JAR file location]/data/addressbook.json`. The file contains
employees, leave records, and the next internal identifiers. Editing it manually is not recommended.

<div markdown="span" class="alert alert-warning">:exclamation: **Caution:**
If your changes make the data file invalid, HR Book starts with empty data at the next run. The invalid file remains on
disk until you run a command (HR Book saves after every command). Still, we recommend backing up the file before
editing it.<br>
Furthermore, certain edits can cause HR Book to behave in unexpected ways (e.g., if a value entered is outside of the
acceptable range or a leave refers to a missing employee). Therefore, edit the data file only if you are confident that
you can update it correctly.
</div>

### Archiving data files `[coming in v2.0]`

_Details coming soon ..._

--------------------------------------------------------------------------------------------------------------------

## FAQ

**Q**: How do I transfer my data to another computer?<br>
**A**: Install the app on the other computer and overwrite the data file it creates with the data file from your previous
HR Book home folder. This transfers both employee and leave records.

**Q**: Why does an employee card show a different number from the leave ID?<br>
**A**: The employee list number is a temporary displayed index that can change after a search or filter. A leave ID is a
stable identifier for one leave record. Use the displayed index with `leave add` and the leave ID with `leave delete`.

--------------------------------------------------------------------------------------------------------------------

## Known issues

1. **When using multiple screens**, if you move the application to a secondary screen, and later switch to using only the primary screen, the GUI will open off-screen. The remedy is to delete the `preferences.json` file created by the application before running the application again.
2. **If you minimize the Help Window** and then run the `help` command (or use the `Help` menu, or the keyboard shortcut `F1`) again, the original Help Window will remain minimized, and no new Help Window will appear. The remedy is to manually restore the minimized Help Window.

--------------------------------------------------------------------------------------------------------------------

## Command summary

| Action                | Format, Examples |
|-----------------------|------------------|
| **Add employee**      | `add n/NAME p/PHONE_NUMBER e/EMAIL a/ADDRESS le/ANNUAL_LEAVE_DAYS [s/SALARY] [t/TAG]…​`<br>e.g., `add n/James Ho p/22224444 e/jamesho@example.com a/123 Clementi Rd le/14 t/friend` |
| **Clear**             | `clear` |
| **Delete employee**   | `delete INDEX`<br>e.g., `delete 3` |
| **Salary**            | `salary INDEX s/SALARY`<br>e.g., `salary 1 s/5000` |
| **Edit employee**     | `edit INDEX [n/NAME] [p/PHONE_NUMBER] [e/EMAIL] [a/ADDRESS] [le/ANNUAL_LEAVE_DAYS] [t/TAG]…​`<br>e.g., `edit 2 n/James Lee le/21` |
| **Find**              | `find KEYWORD [MORE_KEYWORDS]`<br>e.g., `find James Jake` |
| **Filter**            | `filter [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS] [t/TAG]... [task/DETAILS]... [s/SALARY]`<br>e.g., `filter task/report s/3000` |
| **List employees**    | `list` |
| **Record leave**      | `leave add INDEX from/START_DATE to/END_DATE`<br>e.g., `leave add 1 from/20-10-2026 to/22-10-2026` |
| **List leaves**       | `leave list` |
| **Delete leave**      | `leave delete LEAVE_ID`<br>e.g., `leave delete 2` |
| **List tasks**        | `list-tasks EMPLOYEE_INDEX`<br>e.g., `list-tasks 1` |
| **Add task**          | `add-task EMPLOYEE_INDEX d/DETAILS`<br>e.g., `add-task 1 d/Prepare report` |
| **Delete task**       | `delete-task EMPLOYEE_INDEX i/TASK_INDEX`<br>e.g., `delete-task 1 i/1` |
| **Workforce summary** | `summary [scope/all\|scope/filtered]` (alias: `/summary`) |
| **Help**              | `help` |
| **Exit**              | `exit` |
