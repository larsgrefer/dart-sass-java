package de.larsgrefer.sass.embedded;

import com.sass_lang.embedded_protocol.OutboundMessage;
import com.sass_lang.embedded_protocol.OutboundMessage.CompileResponse.CompileFailure;
import lombok.Getter;

import java.util.List;

/**
 * @author Lars Grefer
 * @see CompileFailure
 */
public class SassCompilationFailedException extends Exception implements CompilationResult {

    @Getter
    private final CompileFailure compileFailure;

    @Getter
    private final List<String> loadedUrls;

    public SassCompilationFailedException(OutboundMessage.CompileResponse response) {
        super(response.getFailure().getFormatted());
        this.compileFailure = response.getFailure();
        this.loadedUrls = response.getLoadedUrlsList();
    }

}
