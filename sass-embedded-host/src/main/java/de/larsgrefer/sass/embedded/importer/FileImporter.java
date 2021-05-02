package de.larsgrefer.sass.embedded.importer;

import java.io.File;

/**
 * A file importer as specified by the embedded sass protocol.
 *
 * @author Lars Grefer
 * @see sass.embedded_protocol.EmbeddedSass.InboundMessage.CompileRequest.Importer
 */
public abstract class FileImporter extends Importer {

    /**
     * @see sass.embedded_protocol.EmbeddedSass.OutboundMessage.FileImportRequest
     */
    public abstract File handleImport(String url);
}
