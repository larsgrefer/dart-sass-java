package de.larsgrefer.sass.embedded;

import lombok.Getter;
import sass.embedded_protocol.EmbeddedSass.OutboundMessage.CompileResponse.CompileFailure;

/**
 * @author Lars Grefer
 */
public class SassCompilationFailedException extends Exception {

    @Getter
    private final CompileFailure compileFailure;

    public SassCompilationFailedException(CompileFailure failure) {
        super(failure.getFormatted());
        this.compileFailure = failure;
    }

}
