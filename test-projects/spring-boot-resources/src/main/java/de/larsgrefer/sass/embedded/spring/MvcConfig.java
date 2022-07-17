package de.larsgrefer.sass.embedded.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
public class MvcConfig implements WebMvcConfigurer {

    @Autowired
    private WebProperties webProperties;

    @Autowired
    private WebMvcProperties webMvcProperties;

    @Autowired
    private SassResourceTransformer sassResourceTransformer;

    @Autowired
    private SassResourceResolver sassResourceResolver;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(webMvcProperties.getStaticPathPattern())
                .addResourceLocations(webProperties.getResources().getStaticLocations())
                .resourceChain(webProperties.getResources().getChain().isCache())
                .addResolver(sassResourceResolver)
                .addTransformer(sassResourceTransformer);
    }
}
