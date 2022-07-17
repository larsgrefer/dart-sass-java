package de.larsgrefer.sass.embedded.spring;

import lombok.Getter;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.resource.TransformedResource;
import sass.embedded_protocol.EmbeddedSass.OutboundMessage.CompileResponse.CompileSuccess;
import sass.embedded_protocol.EmbeddedSass.OutputStyle;

/**
 * @author Lars Grefer
 */
class CompiledResource extends TransformedResource {

    private final String filename;

    @Getter
    private final OutputStyle outputStyle;

    @Override
    public String getFilename() {
        return this.filename;
    }

    public CompiledResource(Resource original, CompileSuccess compileSuccess, String filename, OutputStyle outputStyle) {
        super(original, compileSuccess.getCssBytes().toByteArray());
        this.filename = filename;
        this.outputStyle = outputStyle;
    }

}
