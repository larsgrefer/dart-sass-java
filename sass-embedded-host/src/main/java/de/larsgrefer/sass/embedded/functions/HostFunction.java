package de.larsgrefer.sass.embedded.functions;

import lombok.Getter;
import lombok.Value;
import lombok.With;
import sass.embedded_protocol.EmbeddedSass;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Lars Grefer
 * @see de.larsgrefer.sass.embedded.SassCompiler#registerFunction(HostFunction)
 * @see HostFunctionFactory
 */
@Getter
public abstract class HostFunction {

    private final String name;

    private final List<Argument> arguments;

    private final String signature;

    protected HostFunction(String name, List<Argument> arguments) {
        this.name = name;
        this.arguments = Collections.unmodifiableList(arguments);
        this.signature = prepareSignature();
    }

    public abstract EmbeddedSass.Value invoke(List<EmbeddedSass.Value> arguments) throws Throwable;

    private String prepareSignature() {
        String functionName = getName();

        return functionName + getArguments().stream()
                .map(Argument::getSassSignature)
                .collect(Collectors.joining(", ", "(", ")"));
    }

    @Value
    @With
    public static class Argument {
        String name;

        @Nullable
        String defaultValue;

        public String getSassSignature() {
            String signature = "$" + name;

            if (defaultValue != null) {
                signature += ": " + defaultValue;
            }
            return signature;
        }
    }
}
