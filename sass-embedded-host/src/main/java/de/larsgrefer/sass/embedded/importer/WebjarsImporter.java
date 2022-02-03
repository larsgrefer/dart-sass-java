package de.larsgrefer.sass.embedded.importer;

import lombok.extern.slf4j.Slf4j;
import org.webjars.NotFoundException;
import org.webjars.WebJarAssetLocator;

import javax.annotation.Nullable;
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
        else {
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
