package de.larsgrefer.sass.embedded;

import sass.embedded_protocol.EmbeddedSass;

public class SassCompilationFailedException extends Exception {

    private EmbeddedSass.OutboundMessage.CompileResponse.CompileFailure compileFailure;

    public SassCompilationFailedException(EmbeddedSass.OutboundMessage.CompileResponse.CompileFailure failure) {
        super(failure.getMessage());
        this.compileFailure = failure;
    }

    public EmbeddedSass.OutboundMessage.CompileResponse.CompileFailure getCompileFailure() {
        return compileFailure;
    }
}
