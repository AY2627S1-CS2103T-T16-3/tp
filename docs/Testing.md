---
layout: page
title: Testing guide
---

* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## Running tests

You can run tests in two ways.

* **Method 1: Using IntelliJ JUnit test runner**
  * To run all tests, right-click on the `src/test/java` folder and choose `Run 'All Tests'`
  * To run a subset of tests, you can right-click on a test package,
    test class, or a test and choose `Run 'ABC'`
* **Method 2: Using Gradle**
  * Open a console and run the command `gradlew clean test` (Mac/Linux: `./gradlew clean test`)

<div markdown="span" class="alert alert-secondary">:link: **Link**: Read [this Gradle Tutorial from the se-edu/guides](https://se-education.org/guides/tutorials/gradle.html) to learn more about using Gradle.
</div>

--------------------------------------------------------------------------------------------------------------------

## Types of tests

This project has three types of tests:

1. *Unit tests* target the lowest-level methods and classes.<br>
   For example: `seedu.address.commons.StringUtilTest`
1. *Integration tests* check how multiple code units work together; the individual units are assumed to work.<br>
   For example: `seedu.address.storage.StorageManagerTest`
1. *Hybrid tests* combine unit and integration testing. These tests check both the individual units and how they work together.<br>
   For example: `seedu.address.logic.LogicManagerTest`

## Save safety regression and stress tests

Run `gradlew test --tests seedu.address.storage.SafeSaveStressTest --tests seedu.address.storage.JsonAddressBookStorageTest`
for the save-focused stress tests, then `gradlew clean test checkstyleMain checkstyleTest` for full verification.

`SafeSaveStressTest` covers:

* 315 failure-and-retry scenarios: 21 valid commands, three display states, and five injected failures
  (partial write, failure after complete JSON is written, replacement failure, access denied, unsupported atomic move).
  Every failed save must preserve the old file byte-for-byte, restore the model and displayed list, and retain
  the same observable list. Every retry must match a command executed once on the original state.
* 63 successful-command comparisons against the original parse/execute/direct-serialization flow, including
  command results, display order, all persisted fields and counters, exact JSON bytes, and reload results.
  Command and parser implementations remain unchanged; this is a behavioral comparison, not a second application build.
* 300 reproducible mixed successful/failed operations using seed 2103, compared after every operation with
  a model that executes only accepted commands.
* Ten failed clear/successful save cycles with 1,000 employees, verifying all records after reload.
* Parse and command errors that must not reach storage, empty-filter restoration, relative Unicode paths,
  a parent path occupied by a file, and a destination occupied by a nonempty directory.

`JsonAddressBookStorageTest` additionally covers first-save failures, leftover complete temporary files that
must not be adopted, cleanup failures before and after commit, and normal save/read compatibility.
`ModelManagerTest` covers snapshot independence, initial unsorted state, filter/sort restoration, and retained
observable bindings. `LogicManagerTest` covers both save-error messages and rollback across command types.

These tests use real temporary files and real atomic replacements on the host file system. Write/close-stage,
permission, and unsupported-move errors are simulated at file-operation boundaries; they do not physically
exhaust a disk, alter OS access rules, or cut power. No GUI interaction or process-kill test is included.
Passing tests therefore does not prove every possible failure mode or platform is covered.
