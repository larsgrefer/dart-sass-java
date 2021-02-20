package de.larsgrefer.sass.embedded.importer;

import lombok.RequiredArgsConstructor;

import java.net.URL;

@RequiredArgsConstructor
public class ClasspathImporter extends CustomUrlImporter {

    private final ClassLoader classLoader;

    public ClasspathImporter() {
        this(ClasspathImporter.class.getClassLoader());
    }

    @Override
    public URL canonicalizeUrl(String url) {
        return classLoader.getResource(url);
    }
}
