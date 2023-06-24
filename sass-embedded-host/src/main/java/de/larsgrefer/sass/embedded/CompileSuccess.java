package de.larsgrefer.sass.embedded;

import com.google.protobuf.ByteString;
import com.sass_lang.embedded_protocol.OutboundMessage;
import lombok.Getter;

import java.util.List;

/**
 * @author Lars Grefer
 * @see com.sass_lang.embedded_protocol.OutboundMessage.CompileResponse.CompileSuccess
 */
@Getter
public class CompileSuccess implements CompilationResult {

    private final OutboundMessage.CompileResponse compileResponse;

    public CompileSuccess(OutboundMessage.CompileResponse compileResponse) {
        if (!compileResponse.hasSuccess()) {
            throw new IllegalArgumentException("CompileResponse ist not successful");
        }
        this.compileResponse = compileResponse;
    }


    /**
     * The compiled CSS.
     */
    public String getCss() {
        return compileResponse.getSuccess().getCss();
    }

    /**
     * The compiled CSS.
     */
    public ByteString getCssBytes() {
        return compileResponse.getSuccess().getCssBytes();
    }

    /**
     * The JSON-encoded source map, or the empty string if `CompileRequest.source_map` was `false`.
     * <p>
     * The compiler must not add a `"file"` key to this source map.
     * It's the host's (or the host's user's) responsibility to determine how the generated CSS can be reached from the source map.
     */
    public String getSourceMap() {
        return compileResponse.getSuccess().getSourceMap();
    }

    /**
     * The JSON-encoded source map, or the empty string if `CompileRequest.source_map` was `false`.
     * <p>
     * The compiler must not add a `"file"` key to this source map.
     * It's the host's (or the host's user's) responsibility to determine how the generated CSS can be reached from the source map.
     */
    public ByteString getSourceMapBytes() {
        return compileResponse.getSuccess().getSourceMapBytes();
    }

    /**
     * The canonical URLs of all source files loaded during the compilation.
     */
    public List<String> getLoadedUrls() {
        return compileResponse.getLoadedUrlsList();
    }

}
