package de.larsgrefer.sass.embedded.importer;

import sass.embedded_protocol.EmbeddedSass.OutboundMessage.FileImportRequest;

import java.io.File;

/**
 * A file importer as specified by the embedded sass protocol.
 *
 * @author Lars Grefer
 * @see sass.embedded_protocol.EmbeddedSass.InboundMessage.CompileRequest.Importer
 */
public abstract class FileImporter extends Importer {

    /**
     * @param url The (non-canonicalized) URL of the import.
     * @param fromImport Whether this request comes from an `@import` rule.
     * @see FileImportRequest
     */
    public abstract File handleImport(String url, boolean fromImport);
}
