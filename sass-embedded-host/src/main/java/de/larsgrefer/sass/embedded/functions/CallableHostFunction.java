package de.larsgrefer.sass.embedded.functions;

import com.sass_lang.embedded_protocol.Value;

import javax.annotation.Nonnull;
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
    @Nonnull
    public Value invoke(List<Value> arguments) throws Throwable {
        Object call = callable.call();
        return ConversionService.toSassValue(call);
    }

}
