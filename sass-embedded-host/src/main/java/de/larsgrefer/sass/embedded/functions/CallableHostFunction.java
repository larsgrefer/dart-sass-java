package de.larsgrefer.sass.embedded.functions;

import sass.embedded_protocol.EmbeddedSass;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author Lars Grefer
 */
class CallableHostFunction extends HostFunction {

    private final Callable<?> callable;

    protected CallableHostFunction(String name, Callable<?> callable) {
        super(name, Collections.emptyList());
        this.callable = callable;
    }

    @Override
    public EmbeddedSass.Value invoke(List<EmbeddedSass.Value> arguments) throws Throwable {
        Object call = callable.call();
        return ConversionService.toSassValue(call);
    }

}
