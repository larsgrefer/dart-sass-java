package de.larsgrefer.sass.embedded;

import com.sass_lang.embedded_protocol.ProtocolError;
import lombok.Getter;

/**
 * @author Lars Grefer
 * @see ProtocolError
 */
@Getter
public class SassProtocolErrorException extends RuntimeException {

    private final ProtocolError protocolError;

    public SassProtocolErrorException(ProtocolError error) {
        super(error.getType() + ": " + error.getMessage());
        this.protocolError = error;
    }

}
