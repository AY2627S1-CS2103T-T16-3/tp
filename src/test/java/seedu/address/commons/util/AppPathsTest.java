package seedu.address.commons.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.core.LogsCenter;

public class AppPathsTest {
    @TempDir
    public Path temporaryFolder;

    @Test
    public void getHomeDirectory_packagedLocation_usesJarParentRatherThanWorkingFolder() throws Exception {
        Path home = Files.createDirectory(temporaryFolder.resolve("HR Book #1 José 李明 %h %t %u %g"));
        Path jar = Files.createFile(home.resolve("renamed application.jar"));
        Path working = Files.createDirectory(temporaryFolder.resolve("launch"));

        assertEquals(home, AppPaths.getHomeDirectory(jar.toUri().toURL(), working));
        assertEquals(home, AppPaths.getHomeDirectory(jar.toUri().toURL(), home));
    }

    @Test
    public void getHomeDirectory_unpackedClasses_keepsDevelopmentWorkingFolder() throws Exception {
        Path classes = Files.createDirectory(temporaryFolder.resolve("classes"));
        Path working = Files.createDirectory(temporaryFolder.resolve("project"));

        assertEquals(working, AppPaths.getHomeDirectory(classes.toUri().toURL(), working));
    }

    @Test
    public void getHomeDirectory_missingOrUnsupportedLocation_failsWithoutUsingWorkingFolder() throws Exception {
        assertThrows(IllegalStateException.class, () -> AppPaths.getHomeDirectory(null, temporaryFolder));
        assertThrows(IllegalStateException.class, () -> AppPaths.getHomeDirectory(
                URI.create("https://example.com/app.jar").toURL(), temporaryFolder));
        assertThrows(IllegalStateException.class, () -> AppPaths.getHomeDirectory(
                temporaryFolder.resolve("missing.jar").toUri().toURL(), temporaryFolder));
    }

    @Test
    public void resolve_relativeFiles_normalizesPathsWithinHomeWithoutCreatingFiles() {
        assertEquals(temporaryFolder.resolve("data/addressbook.json"),
                AppPaths.resolve(temporaryFolder, "data/addressbook.json"));
        assertEquals(temporaryFolder.resolve("preferences.json"),
                AppPaths.resolve(temporaryFolder, "data/../preferences.json"));
        assertFalse(Files.exists(temporaryFolder.resolve("data")));
        assertEquals(AppPaths.getHomeDirectory().resolve("data/addressbook.json"),
                AppPaths.resolve("data/addressbook.json"));
    }

    @Test
    public void resolve_absoluteOrEscapingPaths_rejectsInvalidPaths() {
        assertThrows(IllegalArgumentException.class, () ->
                AppPaths.resolve(temporaryFolder, temporaryFolder.resolve("file.json").toString()));
        assertThrows(IllegalArgumentException.class, () -> AppPaths.resolve(temporaryFolder, "../outside.json"));
        assertThrows(IllegalArgumentException.class, () -> AppPaths.resolve(temporaryFolder, "data/../../outside"));
        assertThrows(NullPointerException.class, () -> AppPaths.resolve(temporaryFolder, null));
        assertThrows(NullPointerException.class, () -> AppPaths.resolve(null, "file.json"));
    }

    @Test
    public void packagedLaunch_specialCharacters_keepsPathsAndLogsBesideJar() throws Exception {
        Path home = Files.createDirectory(temporaryFolder.resolve("HR Book #1 José 李明 %h %t %u %g")).toRealPath();
        Path launch = Files.createDirectory(temporaryFolder.resolve("elsewhere"));
        Path jar = createProbeJar(home);

        String output = runProbe(jar, launch);

        assertTrue(output.contains("HOME=" + home), output);
        assertTrue(output.contains("DATA=" + home.resolve("data/addressbook.json")), output);
        assertTrue(output.contains("PREFS=" + home.resolve("preferences.json")), output);
        assertTrue(Files.exists(home.resolve("addressbook.log.0")), output);
        try (var files = Files.list(launch)) {
            assertEquals(0, files.count(), output);
        }
    }

    @Test
    public void packagedLaunch_unwritableLogFile_keepsConsoleLoggingWithoutStartupCrash() throws Exception {
        Path home = Files.createDirectory(temporaryFolder.resolve("blocked-log")).toRealPath();
        Files.createDirectory(home.resolve("addressbook.log.0"));
        Path launch = Files.createDirectory(temporaryFolder.resolve("elsewhere"));

        String output = runProbe(createProbeJar(home), launch);

        assertTrue(output.contains("Logging to the console only."), output);
        assertTrue(output.contains("HOME=" + home), output);
        try (var files = Files.list(launch)) {
            assertEquals(0, files.count(), output);
        }
    }

    private Path createProbeJar(Path home) throws Exception {
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        manifest.getMainAttributes().put(Attributes.Name.MAIN_CLASS, AppPathsProbe.class.getName());
        Path jar = home.resolve("renamed application.jar");
        try (JarOutputStream output = new JarOutputStream(Files.newOutputStream(jar), manifest)) {
            for (Class<?> type : new Class<?>[] {AppPaths.class, LogsCenter.class, AppPathsProbe.class}) {
                String resource = type.getName().replace('.', '/') + ".class";
                output.putNextEntry(new JarEntry(resource));
                try (InputStream input = type.getResourceAsStream("/" + resource)) {
                    input.transferTo(output);
                }
                output.closeEntry();
            }
        }
        return jar;
    }

    private String runProbe(Path jar, Path launch) throws Exception {
        String javaExecutable = Path.of(System.getProperty("java.home"), "bin", "java").toString();
        Process process = new ProcessBuilder(javaExecutable, "-Dstdout.encoding=UTF-8", "-Dstderr.encoding=UTF-8",
                "-jar", jar.toString())
                .directory(launch.toFile()).redirectErrorStream(true).start();
        try {
            assertTrue(process.waitFor(20, TimeUnit.SECONDS), "Packaged path probe timed out.");
            String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            assertEquals(0, process.exitValue(), output);
            return output;
        } finally {
            process.destroyForcibly();
        }
    }
}
