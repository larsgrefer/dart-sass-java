package de.larsgrefer.sass.embedded;


import lombok.Getter;
import sass.embedded_protocol.EmbeddedSass;

/**
 * @author Lars Grefer
 */
@Getter
public class SassProtocolErrorException extends RuntimeException {

    private final EmbeddedSass.ProtocolError protocolError;

    public SassProtocolErrorException(EmbeddedSass.ProtocolError error) {
        super(error.getType() + ": " + error.getMessage());
        this.protocolError = error;
    }

}
