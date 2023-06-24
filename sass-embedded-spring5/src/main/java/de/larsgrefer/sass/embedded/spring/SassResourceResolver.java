package de.larsgrefer.sass.embedded.spring;

import com.sass_lang.embedded_protocol.InboundMessage.CompileRequest.StringInput;
import com.sass_lang.embedded_protocol.OutputStyle;
import de.larsgrefer.sass.embedded.CompileSuccess;
import de.larsgrefer.sass.embedded.SassCompilationFailedException;
import de.larsgrefer.sass.embedded.SassCompiler;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.resource.AbstractResourceResolver;
import org.springframework.web.servlet.resource.ResourceResolverChain;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author Lars Grefer
 */
@RequiredArgsConstructor
@AllArgsConstructor
@Slf4j
public class SassResourceResolver extends AbstractResourceResolver {

    private final SassCompiler sassCompiler;

    @Setter
    private OutputStyle outputStyle = OutputStyle.COMPRESSED;

    @Override
    protected Resource resolveResourceInternal(HttpServletRequest request, String requestPath, List<? extends Resource> locations, ResourceResolverChain chain) {
        Resource resource = chain.resolveResource(request, requestPath, locations);
        if (resource != null && resource.exists()) {
            return resource;
        }

        String filename = StringUtils.getFilename(requestPath);

        if (filename.endsWith(".css") || (filename.endsWith(".css.map") && sassCompiler.isGenerateSourceMaps())) {
            boolean sourcemap = filename.endsWith(".css.map");
            String basePath = requestPath.substring(0, requestPath.length() - (sourcemap ? 8 : 4));

            for (String extension : Arrays.asList(".scss", ".sass")) {

                String scssPath = basePath + extension;

                Resource scssResource = chain.resolveResource(request, scssPath, locations);

                if (scssResource != null && scssResource.exists()) {
                    try {
                        StringInput si = SassResourceUtil.toStringInput(scssResource);
                        CompileSuccess compileSuccess = sassCompiler.compileString(si, outputStyle);
                        if (sourcemap) {
                            return new SourceMapResource(scssResource, compileSuccess, filename, outputStyle);
                        } else {
                            return new CompiledResource(scssResource, compileSuccess, filename, outputStyle);
                        }
                    } catch (SassCompilationFailedException | IOException e) {
                        log.info(e.getLocalizedMessage(), e);
                    }
                }
            }
        }

        return null;
    }

    @Override
    protected String resolveUrlPathInternal(String resourcePath, List<? extends Resource> locations, ResourceResolverChain chain) {
        return chain.resolveUrlPath(resourcePath, locations);
    }

}
