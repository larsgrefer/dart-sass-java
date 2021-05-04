package de.larsgrefer.sass.embedded.importer;

import sass.embedded_protocol.EmbeddedSass.InboundMessage.ImportResponse.ImportSuccess;
import sass.embedded_protocol.EmbeddedSass.OutboundMessage.CanonicalizeRequest;
import sass.embedded_protocol.EmbeddedSass.OutboundMessage.ImportRequest;

/**
 * A custom importer as specified by the embedded sass protocol.
 *
 * @author Lars Grefer
 */
public abstract class CustomImporter extends Importer {

    /**
     * @param url The url to canonicalize
     * @return The canonicalized URL (including a scheme)
     * @see CanonicalizeRequest
     */
    public abstract String canonicalize(String url) throws Exception;

    /**
     * @param url The url to import
     * @see ImportRequest
     */
    public abstract ImportSuccess handleImport(String url) throws Exception;

    public CustomImporter autoCanonicalize() {
        return new AutoCanonicalizingImporter(this);
    }
}
