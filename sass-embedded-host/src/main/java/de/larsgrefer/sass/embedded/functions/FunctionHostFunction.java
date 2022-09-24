package de.larsgrefer.sass.embedded.functions;

import sass.embedded_protocol.EmbeddedSass.Value;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * @author Lars Grefer
 */
class FunctionHostFunction<T> extends HostFunction {

    private static final List<Argument> args = Collections.singletonList(
            new Argument("arg0", null)
    );

    private final Function<T, ?> lambda;

    private final Class<T> argType;

    protected FunctionHostFunction(String name, Class<T> argType, Function<T, ?> lambda) {
        super(name, args);
        this.lambda = lambda;
        this.argType = argType;
    }

    @Override
    @Nonnull
    public Value invoke(List<Value> arguments) {
        if (arguments.size() != 1) {
            throw new IllegalArgumentException("Invalid argument count: Expected 1 instead of " + arguments.size());
        }

        T arg0 = ConversionService.toJavaValue(arguments.get(0), argType, argType);

        Object call = lambda.apply(arg0);
        return ConversionService.toSassValue(call);
    }

}
