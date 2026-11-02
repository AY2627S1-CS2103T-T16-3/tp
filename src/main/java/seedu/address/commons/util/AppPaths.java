package seedu.address.commons.util;

import static java.util.Objects.requireNonNull;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.CodeSource;

/** Resolves application files relative to the folder containing the running JAR. */
public final class AppPaths {
    private static final String HOME_ERROR = "Could not locate HR Book's home folder.";

    private AppPaths() {
    }

    /** Returns the JAR's parent folder, or the working folder when running unpackaged classes. */
    public static Path getHomeDirectory() {
        CodeSource source = AppPaths.class.getProtectionDomain().getCodeSource();
        return getHomeDirectory(source == null ? null : source.getLocation(), Path.of(""));
    }

    static Path getHomeDirectory(URL codeLocation, Path workingDirectory) {
        requireNonNull(workingDirectory);
        if (codeLocation == null) {
            throw new IllegalStateException(HOME_ERROR);
        }
        if (!"file".equalsIgnoreCase(codeLocation.getProtocol())) {
            throw new IllegalStateException(HOME_ERROR + " The code location must be a local file.");
        }
        try {
            Path location = Path.of(codeLocation.toURI()).toAbsolutePath().normalize();
            if (Files.isDirectory(location)) {
                return workingDirectory.toAbsolutePath().normalize();
            }
            if (Files.isRegularFile(location)) {
                return location.getParent();
            }
            throw new IllegalStateException(HOME_ERROR + " The code location does not exist: " + location);
        } catch (URISyntaxException | IllegalArgumentException exception) {
            throw new IllegalStateException(HOME_ERROR + " The code location must be a local file.", exception);
        }
    }

    /** Resolves a relative application filename without allowing it to escape the home folder. */
    public static Path resolve(String relativePath) {
        return resolve(getHomeDirectory(), relativePath);
    }

    static Path resolve(Path homeDirectory, String relativePath) {
        requireNonNull(homeDirectory);
        requireNonNull(relativePath);
        Path relative = Path.of(relativePath);
        if (relative.isAbsolute()) {
            throw new IllegalArgumentException("Application file paths must be relative to the home folder.");
        }
        Path home = homeDirectory.toAbsolutePath().normalize();
        Path resolved = home.resolve(relative).normalize();
        if (!resolved.startsWith(home)) {
            throw new IllegalArgumentException("Application file paths must stay inside the home folder.");
        }
        return resolved;
    }

    /** Returns a logging pattern that treats percent signs in the home folder as literal characters. */
    public static String getLogFilePattern(String relativePath) {
        return resolve(relativePath).toString().replace("%", "%%");
    }
}
