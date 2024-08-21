package de.larsgrefer.sass.embedded.connection;

import androidx.annotation.RequiresApi;
import de.larsgrefer.sass.embedded.util.DirCleaner;
import de.larsgrefer.sass.embedded.util.IOUtils;
import de.larsgrefer.sass.embedded.util.PropertyUtils;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@RequiresApi(1000)
public abstract class DartSassPackageProvider {

    private File dartSassExecutable;

    @Nullable
    public File getDartSassExecutable() throws IOException {
        if (dartSassExecutable == null || !dartSassExecutable.exists()) {
            this.dartSassExecutable = extractPackage();
        }

        return dartSassExecutable;
    }

    synchronized File extractPackage() throws IOException {
        URL dist = getPackageUrl();

        if (dist == null) {
            return null;
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

        File result;

        if (execFile == null || execFile.length != 1) {
            throw new IllegalStateException("No (unique) executable file found in " + execDir);
        } else {
            result = execFile[0];
        }

        result.setExecutable(true, true);
        return result;
    }

    Path getTargetPath() throws IOException {
        String dartSassVersion = PropertyUtils.getDartSassVersion();
        String hostVersion = PropertyUtils.getHostVersion();

        if (hostVersion == null || dartSassVersion == null) {
            // No version information available, use random Path and cleanup afterward.
            Path tempDirectory = Files.createTempDirectory("dart-sass");
            Runtime.getRuntime().addShutdownHook(new Thread(new DirCleaner(tempDirectory)));
            return tempDirectory;
        }

        File tmpDir = new File(System.getProperty("java.io.tmpdir"));

        String target = String.format("%s/%s/dart-sass/%s", this.getClass().getName(), hostVersion, dartSassVersion);

        File targetDir = new File(tmpDir, target);
        if (targetDir.isDirectory() || targetDir.mkdirs()) {
            return targetDir.toPath();
        } else {
            throw new IOException(targetDir + " is not directory or can't be created");
        }
    }

    @Nullable
    protected abstract URL getPackageUrl() throws IOException;

}
