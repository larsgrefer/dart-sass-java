package de.larsgrefer.sass.embedded.bundled;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;

@Slf4j
public class BundledCompilerFactory implements Callable<File> {

    /**
     * Path of the extracted compiler executable.
     */
    private static File bundledDartExec;


    static File getBundledDartExec() throws IOException {
        if (bundledDartExec == null) {
            extractBundled();
        }
        return bundledDartExec;
    }

    synchronized static void extractBundled() throws IOException {
        String resourcePath = getBundledCompilerDistPath();

        URL dist = BundledCompilerFactory.class.getResource(resourcePath);

        if (dist == null) {
            throw new IllegalStateException("Resource not found: " + resourcePath);
        }

        Path targetPath = getTargetPath();

        if (IOUtils.isEmpty(targetPath)) {
            try {
                IOUtils.extract(dist, targetPath);
            } catch (IOException e) {
                throw new IOException(String.format("Failed to extract %s into %s", dist, targetPath), e);
            }
        }

        File execDir = targetPath.resolve("dart-sass").toFile();

        File[] execFile = execDir.listFiles(pathname -> pathname.isFile() && pathname.getName().startsWith("sass"));

        if (execFile == null || execFile.length != 1) {
            throw new IllegalStateException("No (unique) executable file found in " + execDir);
        } else {
            bundledDartExec = execFile[0];
        }

        bundledDartExec.setExecutable(true, true);
    }

    private static String getBundledCompilerDistPath() {
        String osName = System.getProperty("os.name").toLowerCase();
        String osArch = System.getProperty("os.arch").toLowerCase();

        String classifier;
        String archiveExtension = "tar.gz";

        if (osName.contains("mac")) {
            if (osArch.equals("aarch64") || osArch.contains("arm") || isRunningOnRosetta2()) {
                classifier = "macos-arm64";
            } else {
                classifier = "macos-x64";
            }
        } else if (osName.contains("win")) {
            archiveExtension = "zip";
            classifier = osArch.contains("64") ? "windows-x64" : "windows-ia32";
        } else {
            if (osArch.equals("aarch64") || osArch.equals("arm64")) {
                classifier = "linux-arm64";
            } else if (osArch.contains("arm")) {
                classifier = "linux-arm";
            } else if (osArch.contains("risc")) {
                classifier = "linux-riscv64";
            } else if (osArch.contains("64")) {
                classifier = "linux-x64";
            } else {
                classifier = "linux-ia32";
            }

            if (isMuslLibC()) {
                classifier += "-musl";
            }
        }

        return String.format("/de/larsgrefer/sass/embedded/bundled/dart-sass-%s.%s", classifier, archiveExtension);
    }

    static Path getTargetPath() throws IOException {
        Package aPackage = BundledCompilerFactory.class.getPackage();
        String specificationVersion = aPackage.getSpecificationVersion();
        String implementationVersion = aPackage.getImplementationVersion();

        if (implementationVersion == null || specificationVersion == null) {
            // No version information available, use random Path and cleanup afterward.
            Path tempDirectory = Files.createTempDirectory("dart-sass");
            Runtime.getRuntime().addShutdownHook(new Thread(new DirCleaner(tempDirectory)));
            return tempDirectory;
        }

        File tmpDir = new File(System.getProperty("java.io.tmpdir"));

        String target = String.format("%s/%s/dart-sass/%s", aPackage.getName(), implementationVersion, specificationVersion);

        File targetDir = new File(tmpDir, target);
        if (targetDir.isDirectory() || targetDir.mkdirs()) {
            return targetDir.toPath();
        } else {
            throw new IOException(targetDir + " is not directory or can't be created");
        }
    }

    /**
     * Check if we are running on an Intel x86_64 JDK emulated by Rosetta2 on an ARM mac.
     */
    static boolean isRunningOnRosetta2() {
        try {
            Process sysctl = Runtime.getRuntime().exec("sysctl -in sysctl.proc_translated");
            String stdOut;
            try (InputStream in = sysctl.getInputStream()) {
                byte[] buffer = new byte[16];
                int read = in.read(buffer);
                if (read > 0) {
                    stdOut = new String(buffer, 0, read, StandardCharsets.UTF_8);
                } else {
                    stdOut = "";
                }
            }
            int exitValue = sysctl.waitFor();
            if (exitValue == 0 && stdOut.equals("1\n")) {
                return true;
            }
        } catch (Exception e) {
            log.info("Unable to check for rosetta", e);
        }
        return false;
    }

    static boolean isMuslLibC() {
        try {
            Process sysctl = Runtime.getRuntime().exec("ldd --version");
            String stdOut;
            try (InputStream in = sysctl.getInputStream()) {
                byte[] buffer = new byte[255];
                int read = in.read(buffer);
                if (read > 0) {
                    stdOut = new String(buffer, 0, read, StandardCharsets.UTF_8);
                } else {
                    stdOut = "";
                }
            }
            String stdErr;
            try (InputStream in = sysctl.getErrorStream()) {
                byte[] buffer = new byte[255];
                int read = in.read(buffer);
                if (read > 0) {
                    stdErr = new String(buffer, 0, read, StandardCharsets.UTF_8);
                } else {
                    stdErr = "";
                }
            }
            int exitValue = sysctl.waitFor();
            if (stdOut.toLowerCase().contains("musl") || stdErr.toLowerCase().contains("musl")) {
                return true;
            }
        } catch (Exception e) {
            log.info("Unable to check for musl", e);
        }
        return false;
    }

    @Override
    public File call() throws IOException {
        return getBundledDartExec();
    }
}
