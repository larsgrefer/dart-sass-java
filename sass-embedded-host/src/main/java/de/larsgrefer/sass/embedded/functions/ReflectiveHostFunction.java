package de.larsgrefer.sass.embedded.functions;

import com.sass_lang.embedded_protocol.Value;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Lars Grefer
 */
class ReflectiveHostFunction extends HostFunction {

    private final Method method;
    private final Object targetObject;

    public ReflectiveHostFunction(Method method) {
        this(method, null);
    }

    public ReflectiveHostFunction(Method method, Object targetObject) {
        super(resolveName(method), resolveArguments(method));
        this.method = method;

        if (Modifier.isStatic(method.getModifiers())) {
            this.targetObject = null;
        }
        else {
            if (targetObject == null) {
                throw new IllegalArgumentException("Calling the non-static method " + method + " requries a targetObject");
            }
            else {
                this.targetObject = Objects.requireNonNull(targetObject, () -> "Calling the non-static method " + method + " requires a targetObject");
            }
        }
    }

    @Override
    @Nonnull
    public Value invoke(List<Value> arguments) throws Throwable {
        Object[] javaArgs = resolveArguments(arguments);
        Object result;
        try {
            result = method.invoke(targetObject, javaArgs);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
        return ConversionService.toSassValue(result);
    }

    private Object[] resolveArguments(List<Value> arguments) {
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

    public static List<Argument> resolveArguments(Method method) {
        return Arrays.stream(method.getParameters())
                .map(ReflectiveHostFunction::toSassParam)
                .collect(Collectors.toList());
    }

    private static Argument toSassParam(Parameter parameter) {
        Argument argument = new Argument(parameter.getName(), null);

        SassArgument sassArgument = parameter.getAnnotation(SassArgument.class);

        if (sassArgument != null) {
            if (!sassArgument.name().isEmpty()) {
                argument = argument.withName(sassArgument.name());
            }

            if (!sassArgument.defaultValue().isEmpty()) {
                argument.withDefaultValue(sassArgument.defaultValue());
            }
        }

        return argument;
    }

    private static String resolveName(Method method) {
        SassFunction sassFunction = method.getAnnotation(SassFunction.class);

        if (sassFunction != null) {
            String name = sassFunction.name();

            if (!name.isEmpty()) {
                return name;
            }
        }

        return method.getName();
    }
}
