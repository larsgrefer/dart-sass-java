package de.larsgrefer.sass.embedded.functions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sass.embedded_protocol.EmbeddedSass;

import java.util.List;
import java.util.stream.Collectors;

public abstract class HostFunction {

    public abstract EmbeddedSass.Value invoke(List<EmbeddedSass.Value> arguments) throws Throwable;

    public abstract String getName();

    public abstract List<Argument> getParameters();

    public String getSignature() {
        String functionName = getName();

        return functionName + getParameters().stream()
                .map(Argument::getSassSignature)
                .collect(Collectors.joining(", ", "(", ")"));
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Argument {
        String name;
        String defaultValue;

        String getSassSignature() {
            String signature = "$" + name;

            if (defaultValue != null) {
                signature += ": " + defaultValue;
            }
            return signature;
        }
    }
}
