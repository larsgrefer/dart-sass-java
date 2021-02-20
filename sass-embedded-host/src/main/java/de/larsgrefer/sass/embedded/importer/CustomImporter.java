package de.larsgrefer.sass.embedded.importer;

import sass.embedded_protocol.EmbeddedSass;

public abstract class CustomImporter extends Importer {

    public abstract String canonicalize(String url) throws Exception;

    public abstract EmbeddedSass.InboundMessage.ImportResponse.ImportSuccess handleImport(String url) throws Exception;
}
