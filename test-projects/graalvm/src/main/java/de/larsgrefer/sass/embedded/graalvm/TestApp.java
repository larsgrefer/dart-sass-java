package de.larsgrefer.sass.embedded.graalvm;

import de.larsgrefer.sass.embedded.CompileSuccess;
import de.larsgrefer.sass.embedded.SassCompilationFailedException;
import de.larsgrefer.sass.embedded.SassCompiler;
import de.larsgrefer.sass.embedded.SassCompilerFactory;

import java.io.IOException;

public class TestApp {

    public static void main(String[] args) throws SassCompilationFailedException, IOException {
        try(SassCompiler sassCompiler = SassCompilerFactory.bundled()) {
            CompileSuccess compileSuccess = sassCompiler.compileScssString("body { width: 2+2px }");

            System.out.println(compileSuccess.getCss());
        }
    }
}
