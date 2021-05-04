package de.larsgrefer.sass.embedded.importer;

import lombok.RequiredArgsConstructor;
import sass.embedded_protocol.EmbeddedSass;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lars Grefer
 */
@RequiredArgsConstructor
class AutoCanonicalizingImporter extends CustomImporter {

    public final CustomImporter delegate;

    @Override
    public String canonicalize(String url) throws Exception {
        String canonUrl = delegate.canonicalize(url);

        if (canonUrl != null) {
            return canonUrl;
        }

        canonUrl = canonicalize(url, CanonicalizationHelper.resolvePossiblePaths(url));

        if (canonUrl != null) {
            return canonUrl;
        }

        return canonicalize(url, CanonicalizationHelper.resolvePossibleIndexPaths(url));
    }

    private String canonicalize(String baseUrl, List<String> possibleUrls) throws Exception {
        List<String> canonicalizedUrls = new ArrayList<>(possibleUrls.size());

        for (String indexPath : possibleUrls) {
            String canonUrl = delegate.canonicalize(indexPath);
            if (canonUrl != null) {
                canonicalizedUrls.add(canonUrl);
            }
        }

        if (canonicalizedUrls.isEmpty()) {
            return null;
        }
        else if (canonicalizedUrls.size() == 1) {
            return canonicalizedUrls.get(0);
        }
        else {
            throw new IllegalStateException("Import '" + baseUrl + "' is ambiguous: " + canonicalizedUrls);
        }
    }

    @Override
    public EmbeddedSass.InboundMessage.ImportResponse.ImportSuccess handleImport(String url) throws Exception {
        return delegate.handleImport(url);
    }

    @Override
    public AutoCanonicalizingImporter autoCanonicalize() {
        return this;
    }
}
