package de.larsgrefer.sass.embedded.importer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class ClasspathImporter extends CustomUrlImporter {

    private final ClassLoader classLoader;

    public ClasspathImporter() {
        this(ClasspathImporter.class.getClassLoader());
    }

    @Override
    public URL canonicalizeUrl(String url) throws IOException {
        Enumeration<URL> foundResources = classLoader.getResources(url);

        List<URL> foundUrls = getNonDirectoryUrls(foundResources);

        if (foundUrls.isEmpty()) {
            return null;
        }
        else if (foundUrls.size() == 1) {
            return foundUrls.get(0);
        }
        else {
            throw new IllegalStateException(String.format("Import of '%s' found %d results.", url, foundUrls.size()));
        }
    }

    @Nonnull
    private List<URL> getNonDirectoryUrls(Enumeration<URL> foundResources) throws IOException {
        List<URL> foundUrls = new ArrayList<>();
        while (foundResources.hasMoreElements()) {
            URL candidate = foundResources.nextElement();
            if (!isDirectory(candidate))
                foundUrls.add(candidate);
        }
        return foundUrls;
    }
}
