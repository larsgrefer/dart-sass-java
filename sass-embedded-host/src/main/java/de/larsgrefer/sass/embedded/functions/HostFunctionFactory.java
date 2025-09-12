package de.larsgrefer.sass.embedded.functions;

import androidx.annotation.RequiresApi;
import lombok.experimental.UtilityClass;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author Lars Grefer
 */
@UtilityClass
public class HostFunctionFactory {

    @RequiresApi(26)
    public <T> List<HostFunction> allSassFunctions(@Nonnull T object) {
        return allSassFunctions((Class<T>) object.getClass(), object);
    }

    @RequiresApi(26)
    public List<HostFunction> allSassFunctions(@Nonnull Class<?> clazz) {
        return allSassFunctions(clazz, null);
    }

    @RequiresApi(26)
    public <T> List<HostFunction> allSassFunctions(@Nonnull Class<T> clazz, @Nullable T object) {
        List<HostFunction> result = new ArrayList<>();

        Method[] methods = clazz.getMethods();

        for (Method method : methods) {
            if (method.isAnnotationPresent(SassFunction.class)) {
                result.add(ofMethod(method, object));
            }
        }

        return result;
    }

    @RequiresApi(26)
    public HostFunction ofMethod(Method method) {
        return ofMethod(method, null);
    }

    @RequiresApi(26)
    public HostFunction ofMethod(@Nonnull Method method, @Nullable Object targetObject) {
        return new ReflectiveHostFunction(method, targetObject);
    }

    public HostFunction ofLambda(String name, Callable<?> lambda) {
        return new CallableHostFunction(name, lambda);
    }

    @RequiresApi(24)
    public <T> HostFunction ofLambda(String name, Class<T> argType, Function<T, ?> lambda) {
        return new FunctionHostFunction<>(name, argType, lambda);
    }

    @RequiresApi(24)
    public <T, U> HostFunction ofLambda(String name, Class<T> arg0Type, Class<U> arg1Type, BiFunction<T, U, ?> lambda) {
        return new BiFunctionHostFunction<>(name, arg0Type, arg1Type, lambda);
    }
}
