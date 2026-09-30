package de.larsgrefer.sass.embedded.importer;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.webjars.NotFoundException;
import org.webjars.WebJarAssetLocator;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Lars Grefer
 */
@Slf4j
public class WebjarsImporter extends ClasspathImporter {

    private static final Pattern fullPathPattern = Pattern.compile("META-INF/resources/webjars/(.*?)/(.*)");

    private final WebJarAssetLocator webJarAssetLocator;

    public WebjarsImporter() {
        this(new WebJarAssetLocator());
    }

    public WebjarsImporter(WebJarAssetLocator webJarAssetLocator) {
        this(webJarAssetLocator.getClass().getClassLoader(), webJarAssetLocator);
    }

    public WebjarsImporter(ClassLoader webjarsLoader) {
        this(webjarsLoader, new WebJarAssetLocator(webjarsLoader));
    }

    public WebjarsImporter(ClassLoader webjarsLoader, WebJarAssetLocator webJarAssetLocator) {
        super(webjarsLoader);
        this.webJarAssetLocator = webJarAssetLocator;
    }

    @Nullable
    @Override
    public URL canonicalizeUrl(String url) throws IOException {

        String fullPath = null;

        // META-INF/resources/webjars/<webjar>/<subpath>

        Matcher matcher = fullPathPattern.matcher(url);
        if (matcher.find()) {
            String webjar = matcher.group(1);
            String subPath = matcher.group(2);

            try {
                fullPath = webJarAssetLocator.getFullPath(webjar, subPath);
            } catch (NotFoundException e) {
                log.debug("Path {} not found in webjar {}", subPath, webjar);
                log.trace(e.getLocalizedMessage(), e);
            }
        }

        // <webjar>/<subpath>

        if (fullPath == null) {
            int endOffset = url.indexOf('/', 1);
            if (endOffset != -1) {
                int startOffset = (url.startsWith("/") ? 1 : 0);
                String webjar = url.substring(startOffset, endOffset);
                String subPath = url.substring(endOffset + 1);
                try {
                    fullPath = webJarAssetLocator.getFullPath(webjar, subPath);
                } catch (NotFoundException e) {
                    log.debug("Path {} not found in webjar {}", subPath, webjar);
                    log.trace(e.getLocalizedMessage(), e);
                }
            }
        }

        // <subpath>

        if (fullPath == null) {
            try {
                fullPath = webJarAssetLocator.getFullPath(url);
            } catch (NotFoundException e) {
                log.debug("Path {} not found in webjars", url);
                log.trace(e.getLocalizedMessage(), e);
            }
        }

        if (fullPath == null) {
            return null;
        }

        return super.canonicalizeUrl(fullPath);
    }

}
