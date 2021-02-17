package de.larsgrefer.sass.embedded;


import lombok.Getter;
import sass.embedded_protocol.EmbeddedSass;

@Getter
public class SassProtocolErrorException extends RuntimeException {

    private EmbeddedSass.ProtocolError protocolError;

    public SassProtocolErrorException(EmbeddedSass.ProtocolError error) {
        super(error.getType() + ": " + error.getMessage());
        this.protocolError = error;
    }

    public EmbeddedSass.ProtocolError getProtocolError() {
        return protocolError;
    }
}
