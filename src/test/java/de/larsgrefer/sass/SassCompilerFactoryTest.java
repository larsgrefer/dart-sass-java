package de.larsgrefer.sass;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SassCompilerFactoryTest {

    @Test
    void bundled() throws Exception {
        SassCompiler bundled = SassCompilerFactory.bundled();

        System.out.println(bundled);

        assertTrue(bundled.getVersion().startsWith("1."));
    }
}