package de.larsgrefer.sass.embedded.importer;

import com.sass_lang.embedded_protocol.InboundMessage.ImportResponse.ImportSuccess;
import com.sass_lang.embedded_protocol.OutboundMessage.CanonicalizeRequest;
import com.sass_lang.embedded_protocol.OutboundMessage.ImportRequest;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Collections;
import java.util.Set;
import java.util.function.Supplier;

/**
 * A custom importer as specified by the embedded sass protocol.
 *
 * @author Lars Grefer
 */
public abstract class CustomImporter extends Importer {

    /**
     * @param url        The URL of the import to be canonicalized. This may be either absolute or relative.
     * @param fromImport Whether this request comes from an `@import` rule.
     * @return The canonicalized URL (including a scheme)
     * @see CanonicalizeRequest
     */
    @Nullable
    public abstract String canonicalize(String url, boolean fromImport) throws Exception;

    /**
     * @param url           The URL of the import to be canonicalized. This may be either absolute or relative.
     * @param fromImport    Whether this request comes from an `@import` rule.
     * @param containingUrl The canonical URL of the [current source file] that contained the load to be canonicalized.
     * @return The canonicalized URL (including a scheme)
     * @see CanonicalizeRequest
     */
    @Nullable
    public String canonicalize(String url, boolean fromImport, @NonNull Supplier<@Nullable String> containingUrl) throws Exception {
        return canonicalize(url, fromImport);
    }

    /**
     * @param url The url to import
     * @see ImportRequest
     */
    public abstract ImportSuccess handleImport(String url) throws Exception;

    public CustomImporter autoCanonicalize() {
        return new AutoCanonicalizingImporter(this);
    }

    /**
     * The set of URL schemes that are considered *non-canonical* for this
     * importer.
     * <p>
     * If any element of this contains a character other than a lowercase
     * ASCII letter, an ASCII numeral, U+002B (`+`), U+002D (`-`), or U+002E
     * (`.`), the compiler must treat the compilation as failed.
     */
    @NonNull
    public Set<String> getNonCanonicalSchemes() {
        return Collections.emptySet();
    }
}
