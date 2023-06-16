package de.larsgrefer.sass.embedded.importer;

import jakarta.servlet.ServletContext;
import lombok.RequiredArgsConstructor;

import java.net.URL;

/**
 * @author Lars Grefer
 */
@RequiredArgsConstructor
public class JakartaServletContextImporter extends CustomUrlImporter {

    private final ServletContext servletContext;

    @Override
    public URL canonicalizeUrl(String url) throws Exception {
        return servletContext.getResource(url);
    }

}
