package de.larsgrefer.sass.embedded.bundled;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class BundledCompilerFactoryTest {

    @Test
    void getBundledDartExec() throws IOException, InterruptedException {

        Process dartSass = new ProcessBuilder(BundledCompilerFactory.getBundledDartExec().getAbsolutePath(), "--embedded", "--version")
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .start();

        dartSass.waitFor();
    }

    @Test
    void isRunningOnRosetta2() {
        boolean runningOnRosetta2 = BundledCompilerFactory.isRunningOnRosetta2();
    }

}