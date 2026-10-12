package de.larsgrefer.sass.embedded.importer;

import com.sass_lang.embedded_protocol.InboundMessage;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author Lars Grefer
 */
@RequiredArgsConstructor
class AutoCanonicalizingImporter extends CustomImporter {

    public final CustomImporter delegate;

    @Override
    public @Nullable String canonicalize(String url, boolean fromImport) throws Exception {
        return canonicalize(url, fromImport, () -> null);
    }

    @Override
    public @Nullable String canonicalize(String url, boolean fromImport, @NonNull Supplier<@Nullable String> containingUrl) throws Exception {
        String canonUrl = delegate.canonicalize(url, fromImport, containingUrl);

        if (canonUrl != null) {
            return canonUrl;
        }

        canonUrl = canonicalize(url, CanonicalizationHelper.resolvePossiblePaths(url), fromImport, containingUrl);

        if (canonUrl != null) {
            return canonUrl;
        }

        return canonicalize(url, CanonicalizationHelper.resolvePossibleIndexPaths(url), fromImport, containingUrl);
    }

    private String canonicalize(String baseUrl, List<String> possibleUrls, boolean fromImport, @NonNull Supplier<@Nullable String> containingUrl) throws Exception {
        Collection<String> canonicalizedUrls = new LinkedHashSet<>(possibleUrls.size() * 2);

        for (String indexPath : possibleUrls) {
            String canonUrl = delegate.canonicalize(indexPath, fromImport, containingUrl);
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
