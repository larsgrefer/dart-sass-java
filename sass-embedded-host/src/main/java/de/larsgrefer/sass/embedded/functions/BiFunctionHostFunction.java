package de.larsgrefer.sass.embedded.functions;

import sass.embedded_protocol.EmbeddedSass.Value;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

/**
 * @author Lars Grefer
 */
class BiFunctionHostFunction<T, U> extends HostFunction {

    private static final List<Argument> args = List.of(
            new Argument("arg0", null),
            new Argument("arg1", null)
    );

    private final BiFunction<T, U, ?> lambda;

    private final Class<T> arg0Type;
    private final Class<U> arg1Type;

    protected BiFunctionHostFunction(String name, Class<T> arg0Type, Class<U> arg1Type, BiFunction<T, U, ?> lambda) {
        super(name, args);
        this.lambda = lambda;
        this.arg0Type = arg0Type;
        this.arg1Type = arg1Type;
    }

    @Override
    @Nonnull
    public Value invoke(List<Value> arguments) {
        if (arguments.size() != 2) {
            throw new IllegalArgumentException("Invalid argument count: Expected 2 instead of " + arguments.size());
        }

        T arg0 = ConversionService.toJavaValue(arguments.get(0), arg0Type, arg0Type);
        U arg1 = ConversionService.toJavaValue(arguments.get(1), arg1Type, arg1Type);

        Object call = lambda.apply(arg0, arg1);
        return ConversionService.toSassValue(call);
    }

}
