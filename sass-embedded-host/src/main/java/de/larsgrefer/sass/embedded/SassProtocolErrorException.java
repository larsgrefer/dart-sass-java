package de.larsgrefer.sass.embedded;

import lombok.Getter;
import sass.embedded_protocol.EmbeddedSass.ProtocolError;

/**
 * @author Lars Grefer
 */
@Getter
public class SassProtocolErrorException extends RuntimeException {

    private final ProtocolError protocolError;

    public SassProtocolErrorException(ProtocolError error) {
        super(error.getType() + ": " + error.getMessage());
        this.protocolError = error;
    }

}
