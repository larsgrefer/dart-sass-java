package de.larsgrefer.sass.embedded.importer;

import com.google.protobuf.ByteString;
import sass.embedded_protocol.EmbeddedSass;

import java.io.InputStream;
import java.net.URL;

public abstract class CustomUrlImporter extends CustomImporter {

    @Override
    public String canonicalize(String url) throws Exception {
        return canonicalizeUrl(url).toString();
    }

    public abstract URL canonicalizeUrl(String url) throws Exception;

    @Override
    public EmbeddedSass.InboundMessage.ImportResponse.ImportSuccess handleImport(String string) throws Exception {
        URL url = new URL(string);
        return handleImport(url);
    }

    public EmbeddedSass.InboundMessage.ImportResponse.ImportSuccess handleImport(URL url) throws Exception {
        EmbeddedSass.InboundMessage.ImportResponse.ImportSuccess.Builder result = EmbeddedSass.InboundMessage.ImportResponse.ImportSuccess.newBuilder();
        try (InputStream in = url.openStream()) {

            ByteString content = ByteString.readFrom(in);

            result.setContentsBytes(content);
        }

        if (url.getPath().endsWith(".css")) {
            result.setSyntax(EmbeddedSass.InboundMessage.Syntax.CSS);
        }
        else if (url.getPath().endsWith(".scss")) {
            result.setSyntax(EmbeddedSass.InboundMessage.Syntax.SCSS);
        }
        else if (url.getPath().endsWith(".sass")) {
            result.setSyntax(EmbeddedSass.InboundMessage.Syntax.INDENTED);
        }

        return result.build();
    }
}
