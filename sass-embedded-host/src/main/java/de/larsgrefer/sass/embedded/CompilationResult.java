package de.larsgrefer.sass.embedded;

import java.util.List;

/**
 * @author Lars Grefer
 * @see CompileSuccess
 * @see SassCompilationFailedException
 */
public interface CompilationResult {

    List<String> getLoadedUrls();
}
