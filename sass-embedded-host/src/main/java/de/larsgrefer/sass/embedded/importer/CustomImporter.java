package de.larsgrefer.sass.embedded.importer;

import sass.embedded_protocol.EmbeddedSass;

/**
 * A custom importer as specified by the embedded sass protocol.
 *
 * @author Lars Grefer
 */
public abstract class CustomImporter extends Importer {

    /**
     * @param url The url to canonicalize
     * @return The canonicalized URL (including a scheme)
     * @see sass.embedded_protocol.EmbeddedSass.OutboundMessage.CanonicalizeRequest
     */
    public abstract String canonicalize(String url) throws Exception;

    /**
     * @param url The url to import
     * @see sass.embedded_protocol.EmbeddedSass.OutboundMessage.ImportRequest
     */
    public abstract EmbeddedSass.InboundMessage.ImportResponse.ImportSuccess handleImport(String url) throws Exception;

    public CustomImporter autoCanonicalize() {
        return new AutoCanonicalizingImporter(this);
    }
}
