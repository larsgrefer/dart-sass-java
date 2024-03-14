package de.larsgrefer.sass.embedded.android;

import android.content.Context;
import android.os.Build;
import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;
import de.larsgrefer.sass.embedded.connection.CompilerConnection;
import de.larsgrefer.sass.embedded.connection.ConnectionFactory;
import de.larsgrefer.sass.embedded.connection.ProcessConnection;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Lars Grefer
 * @see ConnectionFactory
 */
public final class AndroidConnectionFactory {

    @NonNull
    @CheckResult
    public static CompilerConnection bundled(@NonNull Context context) throws IOException {

        String nativeLibraryDir = context.getApplicationInfo().nativeLibraryDir;

        File libDir = new File(nativeLibraryDir);

        if (!libDir.isDirectory()) {
            throw new IllegalStateException("nativeLibraryDir " + nativeLibraryDir + " does not exist.");
        }

        File dartExec = new File(libDir, "libdart.so");

        if (!dartExec.isFile()) {
            throw new IllegalStateException("No dart binary found for ABIs " + Arrays.toString(Build.SUPPORTED_ABIS));
        }

        if (!dartExec.canExecute()) {
            throw new IllegalStateException("Dart binary is not executable " + dartExec);
        }

        File sassSnapshotFile = getSassSnapshotFile(context);

        List<String> cmd = new ArrayList<>();

        cmd.add(dartExec.getAbsolutePath());
        cmd.add(sassSnapshotFile.getAbsolutePath());
        cmd.add("--embedded");

        ProcessBuilder processBuilder = new ProcessBuilder(cmd);
        return new ProcessConnection(processBuilder.start());
    }

    private static File getSassSnapshotFile(Context context) throws IOException {
        File sassSnapshotFile = new File(context.getCodeCacheDir(), BuildConfig.DART_SASS_VERSION + "-sass.snapshot");

        if (sassSnapshotFile.exists() && sassSnapshotFile.isFile()) {
            return sassSnapshotFile;
        }

        List<URL> urls = new ArrayList<>();

        for (String supportedAbi : Build.SUPPORTED_ABIS) {
            URL sassSnapshotUrl = AndroidConnectionFactory.class.getResource("/lib/" + supportedAbi + "/dart-sass/src/sass.snapshot");
            if (sassSnapshotUrl != null) {
                urls.add(sassSnapshotUrl);
            }
        }

        if (urls.isEmpty()) {
            throw new IllegalStateException("No sass.snapshot found for ABIs " + Arrays.toString(Build.SUPPORTED_ABIS));
        }

        try (InputStream in = urls.get(0).openStream();
             FileOutputStream out = new FileOutputStream(sassSnapshotFile)) {
            copy(in, out);
        }

        return sassSnapshotFile;
    }

    private static final int bufferSize = 4096;

    /**
     * Backport of {@link InputStream#transferTo(OutputStream)}
     * in order to support JDK 8
     *
     * @see InputStream#transferTo(OutputStream)
     */
    private static void copy(InputStream inputStream, OutputStream os) throws IOException {
        byte[] buffer = new byte[bufferSize];
        int read;
        while ((read = inputStream.read(buffer, 0, bufferSize)) >= 0) {
            os.write(buffer, 0, read);
        }
    }
}
