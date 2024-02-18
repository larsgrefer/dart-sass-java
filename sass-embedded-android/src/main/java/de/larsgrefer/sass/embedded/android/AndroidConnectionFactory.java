package de.larsgrefer.sass.embedded.android;

import android.content.Context;
import android.os.Build;
import android.util.Log;
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

    public static CompilerConnection bundled(Context context) throws IOException {

        String nativeLibraryDir = context.getApplicationInfo().nativeLibraryDir;

        File libDir = new File(nativeLibraryDir);
        File dartExec = new File(libDir, "libdart.so");

        if (!dartExec.isFile()) {
            throw new IllegalStateException("No dart binary found for ABIs " + Arrays.toString(Build.SUPPORTED_ABIS));
        }

        if (!dartExec.canExecute()) {
            throw new IllegalStateException("Dart binary is not executable " + dartExec);
        }

        URL sassSnapshot = AndroidConnectionFactory.class.getResource("/lib/" + Build.CPU_ABI + "/dart-sass/src/sass.snapshot");

        if (sassSnapshot == null) {
            throw new IllegalStateException("No sass.snapshot found for ABIs " + Arrays.toString(Build.SUPPORTED_ABIS));
        }

        try (
                InputStream is = sassSnapshot.openStream();
                FileOutputStream os = context.openFileOutput("sass.snapshot", Context.MODE_PRIVATE)) {
            copy(is, os);
        }

        List<String> cmd = new ArrayList<>();

        cmd.add(dartExec.getAbsolutePath());
        cmd.add(context.getFileStreamPath("sass.snapshot").getAbsolutePath());
        cmd.add("--embedded");

        ProcessBuilder processBuilder = new ProcessBuilder(cmd);
        return new ProcessConnection(processBuilder);
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
