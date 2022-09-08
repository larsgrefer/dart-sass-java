package de.larsgrefer.sass.embedded.spring;

import lombok.Getter;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.resource.HttpResource;
import org.springframework.web.servlet.resource.TransformedResource;
import sass.embedded_protocol.EmbeddedSass.OutboundMessage.CompileResponse.CompileSuccess;
import sass.embedded_protocol.EmbeddedSass.OutputStyle;

/**
 * @author Lars Grefer
 */
class SourceMapResource extends TransformedResource {

    private final String filename;

    @Getter
    private final OutputStyle outputStyle;

    @Override
    public String getFilename() {
        return this.filename;
    }

    public SourceMapResource(Resource original, CompileSuccess compileSuccess, String filename, OutputStyle outputStyle) {
        super(original, compileSuccess.getSourceMapBytes().toByteArray());
        this.filename = filename;
        this.outputStyle = outputStyle;
    }

}
