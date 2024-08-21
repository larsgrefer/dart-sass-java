package de.larsgrefer.sass.embedded.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
@UtilityClass
public class PlatformUtils {

    public static boolean isAndroid() {
        return "Dalvik".equalsIgnoreCase(System.getProperty("java.vm.name"));
    }

    public static String getDartSassPackageSuffix() {
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

        return classifier + '.' +  archiveExtension;
    }

    /**
     * Check if we are running on an Intel x86_64 JDK emulated by Rosetta2 on an ARM mac.
     */
    public static boolean isRunningOnRosetta2() {
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

    public static boolean isMuslLibC() {
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
}
