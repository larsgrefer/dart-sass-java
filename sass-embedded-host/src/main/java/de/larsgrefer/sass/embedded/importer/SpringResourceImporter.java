package de.larsgrefer.sass.embedded.importer;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.File;
import java.net.URL;

/**
 * @author Lars Grefer
 */
@RequiredArgsConstructor
public class SpringResourceImporter extends CustomUrlImporter {

    private final ResourceLoader resourceLoader;

    @Override
    public URL canonicalizeUrl(String url) throws Exception {
        Resource resource = resourceLoader.getResource(url);

        if (!resource.exists() || !resource.isReadable()) {
            return null;
        }

        Exception ex = null;

        if (resource.isFile()) {
            try {
                File file = resource.getFile();
                return file.toURI().toURL();
            } catch (Exception e) {
                ex = e;
            }
        }

        try {
            return resource.getURL();
        } catch (Exception e) {
            if (ex != null) {
                ex.addSuppressed(e);
            }
            else {
                ex = e;
            }
        }

        throw ex;
    }
}
