package de.larsgrefer.sass.embedded;

import de.larsgrefer.sass.embedded.importer.ClasspathImporter;
import de.larsgrefer.sass.embedded.importer.WebjarsImporter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import sass.embedded_protocol.EmbeddedSass;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.assertj.core.api.Assertions.assertThat;

public class Issue104Test {

    private SassCompiler sassCompiler;

    @TempDir
    private File tempDir;
    private URLClassLoader classLoader;

    @BeforeEach
    public void setUp() throws IOException {
        sassCompiler = SassCompilerFactory.bundled();

        File dummyJar = new File(tempDir, "dummy.jar");

        try (ZipOutputStream out = new ZipOutputStream(Files.newOutputStream(dummyJar.toPath()))) {
            out.putNextEntry(new ZipEntry("css/gh-104.scss"));

            PrintWriter printWriter = new PrintWriter(out);
            printWriter.println("@import \"scss/bootstrap\";");
            printWriter.flush();
            printWriter.close();
        }

        classLoader = new URLClassLoader(new URL[]{dummyJar.toURI().toURL()});
        sassCompiler.registerImporter(new ClasspathImporter(classLoader).autoCanonicalize());
        sassCompiler.registerImporter(new WebjarsImporter().autoCanonicalize());
    }

    @Test
    public void directClasspath() throws SassCompilationFailedException, IOException {
        URL resource = classLoader.getResource("css/gh-104.scss");
        EmbeddedSass.OutboundMessage.CompileResponse.CompileSuccess result = sassCompiler.compile(resource);

        result.getLoadedUrlsList().forEach(System.out::println);
        assertThat(result.getCss()).isNotEmpty();
    }

    @Test
    public void importClasspath() throws SassCompilationFailedException, IOException {
        EmbeddedSass.OutboundMessage.CompileResponse.CompileSuccess result = sassCompiler.compileScssString("@import 'css/gh-104';");

        result.getLoadedUrlsList().forEach(System.out::println);
        assertThat(result.getCss()).isNotEmpty();
    }
}
