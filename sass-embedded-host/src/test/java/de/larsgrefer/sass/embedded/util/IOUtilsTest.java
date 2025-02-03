package de.larsgrefer.sass.embedded.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class IOUtilsTest {

    @Test
    void isEmpty(@TempDir Path tempDir) throws IOException {

        assertThat(IOUtils.isEmpty(tempDir)).isTrue();

        File subfolder1 = new File(tempDir.toFile(), "foo");
        subfolder1.mkdir();

        assertThat(IOUtils.isEmpty(tempDir)).isTrue();

        File subfolder2 = new File(tempDir.toFile(), "bar");
        subfolder2.mkdir();

        assertThat(IOUtils.isEmpty(tempDir)).isTrue();

        File file1 = new File(subfolder1, "dummy.txt");
        file1.createNewFile();

        assertThat(IOUtils.isEmpty(tempDir)).isFalse();

    }
}