package de.larsgrefer.sass.embedded.importer;

import lombok.extern.slf4j.Slf4j;
import org.webjars.MultipleMatchesException;
import org.webjars.WebJarAssetLocator;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        this(webjarsLoader, new WebJarAssetLocator());
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
            } catch (MultipleMatchesException e) {
                throw e;
            } catch (IllegalArgumentException e) {
                log.info(e.getMessage());
            }
        }
        else {
            try {
                fullPath = webJarAssetLocator.getFullPath(url);
            } catch (MultipleMatchesException e) {
                throw e;
            } catch (IllegalArgumentException e) {
                log.info(e.getMessage());
            }
        }

        if (fullPath == null) {
            return null;
        }

        return super.canonicalizeUrl(fullPath);
    }
}
