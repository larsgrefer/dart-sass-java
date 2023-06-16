package de.larsgrefer.sass.embedded.spring;

import com.sass_lang.embedded_protocol.OutputStyle;
import de.larsgrefer.sass.embedded.CompileSuccess;
import lombok.Getter;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.resource.TransformedResource;

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
