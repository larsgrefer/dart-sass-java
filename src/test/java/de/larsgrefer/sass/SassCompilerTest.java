package de.larsgrefer.sass;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class SassCompilerTest {

    SassCompiler sassCompiler = SassCompilerFactory.bundled();

    SassCompilerTest() throws IOException {
    }

    @Test
    void compileString() throws IOException, InterruptedException {
        String sass = ".foo { .bar { color : #ffffff;}}";

        String s = null;
        for (int i = 0; i < 100; i++) {
            s = sassCompiler.compileString(sass);
        }
        System.out.println(s);
    }

    @Test
    void compileStringtoFile() throws IOException, InterruptedException {
        String sass = ".foo { .bar { color : #ffffff;}}";

        String s = null;
        for (int i = 0; i < 100; i++) {
            Path target = Files.createTempFile("target", ".css");
            sassCompiler.compileString(sass, target.toFile());
            s = new String(Files.readAllBytes(target));
        }
        System.out.println(s);
    }

}