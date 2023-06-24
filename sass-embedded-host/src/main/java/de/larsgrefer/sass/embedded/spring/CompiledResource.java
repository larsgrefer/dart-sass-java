package de.larsgrefer.sass.embedded.spring;

import com.sass_lang.embedded_protocol.OutputStyle;
import de.larsgrefer.sass.embedded.CompileSuccess;
import lombok.Getter;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.resource.HttpResource;
import org.springframework.web.servlet.resource.TransformedResource;

/**
 * @author Lars Grefer
 */
class CompiledResource extends TransformedResource implements HttpResource {

    private final String filename;

    @Getter
    private final OutputStyle outputStyle;

    private final boolean hasSourceMap;

    @Override
    public String getFilename() {
        return this.filename;
    }

    public CompiledResource(Resource original, CompileSuccess compileSuccess, String filename, OutputStyle outputStyle) {
        super(original, compileSuccess.getCssBytes().toByteArray());
        this.filename = filename;
        this.outputStyle = outputStyle;
        this.hasSourceMap = StringUtils.hasText(compileSuccess.getSourceMap());
    }

    @SuppressWarnings("UastIncorrectHttpHeaderInspection")
    @Override
    @NonNull
    public HttpHeaders getResponseHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        if (hasSourceMap) {
            httpHeaders.set("SourceMap", filename + ".map");
            httpHeaders.set("X-SourceMap", filename + ".map");
        }
        return httpHeaders;
    }
}
