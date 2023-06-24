package de.larsgrefer.sass.embedded.importer;

import javax.servlet.ServletContext;

/**
 * @author Lars Grefer
 * @deprecated Use {@link JavaxServletContextImporter} instead.
 */
@Deprecated
public class Servlet4ContextImporter extends JavaxServletContextImporter {
    public Servlet4ContextImporter(ServletContext servletContext) {
        super(servletContext);
    }
}
