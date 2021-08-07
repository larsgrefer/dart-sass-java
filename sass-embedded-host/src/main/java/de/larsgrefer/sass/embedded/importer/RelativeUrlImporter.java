package de.larsgrefer.sass.embedded.importer;

import lombok.RequiredArgsConstructor;

import javax.annotation.Nullable;
import java.net.URL;

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
