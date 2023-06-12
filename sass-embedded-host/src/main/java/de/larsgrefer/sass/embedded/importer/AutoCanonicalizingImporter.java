package de.larsgrefer.sass.embedded.importer;

import com.sass_lang.embedded_protocol.InboundMessage;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * @author Lars Grefer
 */
@RequiredArgsConstructor
class AutoCanonicalizingImporter extends CustomImporter {

    public final CustomImporter delegate;

    @Override
    public String canonicalize(String url, boolean fromImport) throws Exception {
        String canonUrl = delegate.canonicalize(url, fromImport);

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
        Collection<String> canonicalizedUrls = new LinkedHashSet<>(possibleUrls.size() * 2);

        for (String indexPath : possibleUrls) {
            String canonUrl = delegate.canonicalize(indexPath, false);
            if (canonUrl != null) {
                canonicalizedUrls.add(canonUrl);
            }
        }

        if (canonicalizedUrls.isEmpty()) {
            return null;
        }
        else if (canonicalizedUrls.size() == 1) {
            return canonicalizedUrls.iterator().next();
        }
        else {
            throw new IllegalStateException("Import '" + baseUrl + "' is ambiguous: " + canonicalizedUrls);
        }
    }

    @Override
    public InboundMessage.ImportResponse.ImportSuccess handleImport(String url) throws Exception {
        return delegate.handleImport(url);
    }

    @Override
    public AutoCanonicalizingImporter autoCanonicalize() {
        return this;
    }
}
