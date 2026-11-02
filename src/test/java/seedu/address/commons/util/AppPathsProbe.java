package seedu.address.commons.util;

import seedu.address.commons.core.LogsCenter;

/** Entry point packaged by AppPathsTest to exercise JAR paths and early logging in a separate JVM. */
public class AppPathsProbe {
    public static void main(String[] args) {
        LogsCenter.getLogger(AppPathsProbe.class).info("Checking the packaged application home.");
        System.out.println("HOME=" + AppPaths.getHomeDirectory());
        System.out.println("DATA=" + AppPaths.resolve("data/hrbook.json"));
        System.out.println("PREFS=" + AppPaths.resolve("preferences.json"));
    }
}
