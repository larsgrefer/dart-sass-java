package de.larsgrefer.sass.embedded.spring;

import com.sass_lang.embedded_protocol.InboundMessage.CompileRequest.StringInput;
import com.sass_lang.embedded_protocol.OutboundMessage;
import com.sass_lang.embedded_protocol.OutputStyle;
import com.sass_lang.embedded_protocol.Syntax;
import de.larsgrefer.sass.embedded.CompileSuccess;
import de.larsgrefer.sass.embedded.SassCompiler;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.resource.ResourceTransformer;
import org.springframework.web.servlet.resource.ResourceTransformerChain;

import java.io.IOException;

/**
 * @author Lars Grefer
 */
@AllArgsConstructor
@RequiredArgsConstructor
public class SassResourceTransformer implements ResourceTransformer {

    private final SassCompiler sassCompiler;

    @Getter
    @Setter
    private OutputStyle outputStyle;

    @Override
    public Resource transform(HttpServletRequest request, Resource resource, ResourceTransformerChain transformerChain) throws IOException {

        resource = transformerChain.transform(request, resource);

        if (resource instanceof CompiledResource) {
            if (((CompiledResource) resource).getOutputStyle() == outputStyle) {
                return resource;
            }
        }

        String filename = resource.getFilename();
        if (!"css".equals(StringUtils.getFilenameExtension(filename))) {
            return resource;
        }

        StringInput stringInput = SassResourceUtil.toStringInput(resource, Syntax.CSS);

        try {
            CompileSuccess compileSuccess = sassCompiler.compileString(stringInput, outputStyle);

            return new CompiledResource(resource, compileSuccess, resource.getFilename(), outputStyle);
        } catch (Exception e) {
            return resource;
        }

    }
}
