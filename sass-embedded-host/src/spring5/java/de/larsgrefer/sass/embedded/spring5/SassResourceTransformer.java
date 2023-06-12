package de.larsgrefer.sass.embedded.spring5;

import org.springframework.core.io.Resource;
import org.springframework.web.servlet.resource.ResourceTransformer;
import org.springframework.web.servlet.resource.ResourceTransformerChain;

import java.io.IOException;

public class SassResourceTransformer implements ResourceTransformer {
    @Override
    public Resource transform(javax.servlet.http.HttpServletRequest request, Resource resource, ResourceTransformerChain transformerChain) throws IOException {
        return null;
    }
}
