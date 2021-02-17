package de.larsgrefer.sass.embedded.functions;

import lombok.Getter;
import sass.embedded_protocol.EmbeddedSass;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SassFunctionAdapter<T> extends HostFunction {

    private final Method method;
    private final Object targetObject;

    @Getter
    private final String name;

    public SassFunctionAdapter(Method method) {
        this.method = method;

        if (Modifier.isStatic(method.getModifiers())) {
            this.targetObject = null;
        }
        else {
            throw new IllegalArgumentException();
        }

        this.name = resolveName();
    }

    public SassFunctionAdapter(Method method, Object targetObject) {
        this.method = method;
        this.targetObject = targetObject;

        if (targetObject == null && !Modifier.isStatic(method.getModifiers())) {
            throw new IllegalArgumentException();
        }

        this.name = resolveName();
    }

    @Override
    public EmbeddedSass.Value invoke(List<EmbeddedSass.Value> arguments) throws Throwable {
        Object[] javaArgs = resolveArguments(arguments);
        try {
            Object result = method.invoke(targetObject, javaArgs);
            return ConversionService.toSassValue(result);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

    private Object[] resolveArguments(List<EmbeddedSass.Value> arguments) {
        if (method.getParameterCount() != arguments.size()) {
            throw new IllegalArgumentException("Invalid argument size");
        }

        Object[] result = new Object[arguments.size()];
        java.lang.reflect.Parameter[] parameters = method.getParameters();

        for (int i = 0; i < arguments.size(); i++) {
            java.lang.reflect.Parameter parameter = parameters[i];
            Object o = ConversionService.toJavaValue(arguments.get(i), parameter.getType(), parameter.getParameterizedType());
            result[i] = o;
        }

        return result;

    }

    private String resolveName() {
        SassFunction sassFunction = method.getAnnotation(SassFunction.class);

        if (sassFunction != null) {
            String name = sassFunction.name();

            if (!name.isEmpty()) {
                return name;
            }
        }

        return method.getName();
    }

    @Override
    public List<Argument> getParameters() {
        return Arrays.stream(method.getParameters())
                .map(this::toSassParam)
                .collect(Collectors.toList());
    }

    private Argument toSassParam(Parameter parameter) {
        Argument argument = new Argument();

        argument.setName(parameter.getName());
        argument.setDefaultValue(null);

        SassArgument sassArgument = parameter.getAnnotation(SassArgument.class);

        if (sassArgument != null) {
            if (!sassArgument.name().isEmpty()) {
                argument.setName(sassArgument.name());
            }

            if (!sassArgument.defaultValue().isEmpty()) {
                argument.setDefaultValue(sassArgument.defaultValue());
            }
        }

        return argument;
    }
}
