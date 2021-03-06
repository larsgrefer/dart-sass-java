package de.larsgrefer.sass.embedded.functions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Lars Grefer
 * @see HostFunctionFactory#allSassFunctions(Class, Object)
 * @see SassFunction
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface SassArgument {

    String name() default "";

    String defaultValue() default "";
}
