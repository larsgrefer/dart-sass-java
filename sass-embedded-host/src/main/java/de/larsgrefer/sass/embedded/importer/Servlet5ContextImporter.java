package de.larsgrefer.sass.embedded.importer;

import jakarta.servlet.ServletContext;

/**
 * @author Lars Grefer
 * @deprecated Use {@link JakartaServletContextImporter} instead.
 */
@Deprecated
public class Servlet5ContextImporter extends JakartaServletContextImporter {
    public Servlet5ContextImporter(ServletContext servletContext) {
        super(servletContext);
    }
}
