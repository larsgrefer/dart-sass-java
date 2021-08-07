package de.larsgrefer.sass.embedded.importer;

import lombok.RequiredArgsConstructor;

import javax.annotation.Nullable;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.jar.JarEntry;

@RequiredArgsConstructor
public class RelativeUrlImporter extends CustomUrlImporter {

    private final URL startUrl;

    @Nullable
    @Override
    public URL canonicalizeUrl(String url) throws Exception {
        URL newUrl = new URL(startUrl, url);

        if (isFile(newUrl)) {
            return newUrl;
        }
        else {
            return null;
        }
    }
}
