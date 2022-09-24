package de.larsgrefer.sass.embedded.functions;

import de.larsgrefer.sass.embedded.util.ColorUtil;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import sass.embedded_protocol.EmbeddedSass.SingletonValue;
import sass.embedded_protocol.EmbeddedSass.Value;
import sass.embedded_protocol.EmbeddedSass.Value.Calculation;
import sass.embedded_protocol.EmbeddedSass.Value.HslColor;
import sass.embedded_protocol.EmbeddedSass.Value.HwbColor;
import sass.embedded_protocol.EmbeddedSass.Value.RgbColor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.Color;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static de.larsgrefer.sass.embedded.util.ProtocolUtil.*;

/**
 * @author Lars Grefer
 */
@UtilityClass
class ConversionService {

    @Nonnull
    static Value toSassValue(@Nullable Object object) {
        if (object == null) {
            return value(SingletonValue.NULL);
        }

        if (object instanceof Boolean) {
            return value((Boolean) object ? SingletonValue.TRUE : SingletonValue.FALSE);
        }

        if (object instanceof CharSequence) {
            Value.String sassString = Value.String.newBuilder()
                    .setQuoted(true)
                    .setText(object.toString())
                    .build();
            return value(sassString);
        }

        if (object instanceof Number) {
            Value.Number sassNumber = Value.Number.newBuilder()
                    .setValue(((Number) object).doubleValue())
                    .build();
            return value(sassNumber);
        }

        if (object instanceof Color) {
            Color color = (Color) object;

            RgbColor sassColor = ColorUtil.toRgbColor(color);
            return value(sassColor);
        }

        if (object instanceof Collection) {
            java.util.List<Value> sassValues = ((Collection<?>) object)
                    .stream()
                    .map(ConversionService::toSassValue)
                    .collect(Collectors.toList());

            return value(list(sassValues));
        }

        if (object instanceof Map) {
            List<Value.Map.Entry> sassEntries = ((Map<?, ?>) object).entrySet()
                    .stream()
                    .map(entry -> Value.Map.Entry.newBuilder()
                            .setKey(toSassValue(entry.getKey()))
                            .setValue(toSassValue(entry.getValue()))
                            .build())
                    .collect(Collectors.toList());

            return value(map(sassEntries));
        }

        if (object instanceof Value) {
            return (Value) object;
        }

        if (object instanceof Value.String) {
            return value((Value.String) object);
        }

        if (object instanceof Value.Number) {
            return value((Value.Number) object);
        }

        if (object instanceof RgbColor) {
            return value((RgbColor) object);
        }

        if (object instanceof HslColor) {
            return value((HslColor) object);
        }

        if (object instanceof Value.List) {
            return value((Value.List) object);
        }

        if (object instanceof Value.Map) {
            return value((Value.Map) object);
        }

        if (object instanceof SingletonValue) {
            return value((SingletonValue) object);
        }

        if (object instanceof Value.CompilerFunction) {
            return value((Value.CompilerFunction) object);
        }

        if (object instanceof Value.HostFunction) {
            return value((Value.HostFunction) object);
        }

        if (object instanceof Value.ArgumentList) {
            return value((Value.ArgumentList) object);
        }

        if (object instanceof HwbColor) {
            return value((HwbColor) object);
        }

        if (object instanceof Calculation) {
            return value((Calculation) object);
        }

        throw new RuntimeException("Cant convert to Sass value");
    }

    @SuppressWarnings("unchecked")
    @Nullable
    static <T> T toJavaValue(@NonNull Value value, Class<T> targetType, Type parameterizedType) {
        if (targetType.equals(Value.class)) {
            return (T) value;
        }

        switch (value.getValueCase()) {
            case STRING:
                Value.String sassString = value.getString();
                if (targetType.isAssignableFrom(Value.String.class)) {
                    return (T) sassString;
                } else if (targetType.isAssignableFrom(String.class)) {
                    return (T) sassString.getText();
                } else {
                    throw new IllegalArgumentException("Cant convert sass String to " + targetType);
                }
            case NUMBER:
                Value.Number sassNumber = value.getNumber();
                double javaNumber = sassNumber.getValue();
                if (targetType.isAssignableFrom(Value.Number.class)) {
                    return (T) sassNumber;
                } else if (targetType.isAssignableFrom(Double.class) || targetType.isAssignableFrom(Double.TYPE)) {
                    return (T) Double.valueOf(javaNumber);
                } else if (targetType.isAssignableFrom(Float.class) || targetType.isAssignableFrom(Float.TYPE)) {
                    return (T) Float.valueOf((float) javaNumber);
                } else if (targetType.isAssignableFrom(Long.class) || targetType.isAssignableFrom(Long.TYPE)) {
                    return (T) Long.valueOf((long) javaNumber);
                } else if (targetType.isAssignableFrom(Integer.class) || targetType.isAssignableFrom(Integer.TYPE)) {
                    return (T) Integer.valueOf((int) javaNumber);
                } else if (targetType.isAssignableFrom(Short.class) || targetType.isAssignableFrom(Short.TYPE)) {
                    return (T) Short.valueOf((short) javaNumber);
                } else if (targetType.isAssignableFrom(Byte.class) || targetType.isAssignableFrom(Byte.TYPE)) {
                    return (T) Byte.valueOf((byte) javaNumber);
                } else if (targetType.isAssignableFrom(BigInteger.class)) {
                    return (T) BigInteger.valueOf((long) javaNumber);
                } else if (targetType.isAssignableFrom(BigDecimal.class)) {
                    return (T) BigDecimal.valueOf(javaNumber);
                } else if (targetType.isAssignableFrom(String.class)) {
                    return (T) Double.toString(javaNumber);
                } else {
                    throw new IllegalArgumentException("Cant convert sass Number to " + targetType);
                }
            case RGB_COLOR:
                RgbColor rgbColor = value.getRgbColor();
                if (targetType.isAssignableFrom(RgbColor.class)) {
                    return (T) rgbColor;
                } else if (targetType.isAssignableFrom(Color.class)) {
                    return (T) ColorUtil.toJavaColor(rgbColor);
                } else {
                    throw new IllegalArgumentException("Cant convert sass RgbColor to " + targetType);
                }
            case HSL_COLOR:
                HslColor hslColor = value.getHslColor();
                if (targetType.isAssignableFrom(HslColor.class)) {
                    return (T) hslColor;
                } else if (targetType.isAssignableFrom(Color.class)) {
                    return (T) ColorUtil.toJavaColor(hslColor);
                } else {
                    throw new IllegalArgumentException("Cant convert sass HslColor to " + targetType);
                }
            case HWB_COLOR:
                HwbColor hwbColor = value.getHwbColor();
                if (targetType.isAssignableFrom(HwbColor.class)) {
                    return (T) hwbColor;
                } else if (targetType.isAssignableFrom(Color.class)) {
                    return (T) ColorUtil.toJavaColor(hwbColor);
                } else {
                    throw new IllegalArgumentException("Cant convert sass HwbColor to " + targetType);
                }
            case LIST:
                Value.List sassList = value.getList();
                if (targetType.isAssignableFrom(Value.List.class)) {
                    return (T) sassList;
                } else if (targetType.isAssignableFrom(List.class)) {
                    Type elementType = ((ParameterizedType) parameterizedType).getActualTypeArguments()[0];

                    Class<?> elementClass = elementType instanceof Class<?> ? (Class<?>) elementType : (Class<?>) ((ParameterizedType) elementType).getRawType();

                    List<?> collect = sassList.getContentsList().stream()
                            .map(elementValue -> toJavaValue(elementValue, elementClass, elementType))
                            .collect(Collectors.toList());

                    return (T) Collections.unmodifiableList(collect);
                } else {
                    throw new IllegalArgumentException("Cant convert sass List to " + targetType);
                }
            case MAP:
                Value.Map sassMap = value.getMap();
                if (targetType.isAssignableFrom(Value.Map.class)) {
                    return (T) sassMap;
                } else if (targetType.isAssignableFrom(Map.class)) {
                    Type keyType = ((ParameterizedType) parameterizedType).getActualTypeArguments()[0];
                    Type valueType = ((ParameterizedType) parameterizedType).getActualTypeArguments()[1];

                    Class<?> keyClass = keyType instanceof Class<?> ? (Class<?>) keyType : (Class<?>) ((ParameterizedType) keyType).getRawType();
                    Class<?> valueClass = valueType instanceof Class<?> ? (Class<?>) valueType : (Class<?>) ((ParameterizedType) valueType).getRawType();

                    Map<?, ?> collect = sassMap.getEntriesList()
                            .stream()
                            .collect(Collectors.toMap(
                                    sassEntry -> toJavaValue(sassEntry.getKey(), keyClass, keyType),
                                    sassEntry -> toJavaValue(sassEntry.getValue(), valueClass, valueType)
                            ));

                    return (T) Collections.unmodifiableMap(collect);
                } else {
                    throw new IllegalArgumentException("Cant convert sass Map to " + targetType);
                }
            case SINGLETON:
                SingletonValue singleton = value.getSingleton();
                switch (value.getSingleton()) {
                    case TRUE:
                    case FALSE:
                        Boolean boolValue = singleton == SingletonValue.TRUE;

                        if (targetType.equals(Boolean.class) || targetType.equals(boolean.class)) {
                            return (T) boolValue;
                        } else if (targetType.equals(String.class)) {
                            return (T) Boolean.toString(boolValue);
                        }

                        throw new IllegalArgumentException("Cant convert sass boolean to " + targetType);
                    case NULL:
                        if (targetType.equals(SingletonValue.class)) {
                            return (T) SingletonValue.NULL;
                        }
                        return null;
                    case UNRECOGNIZED:
                        throw new IllegalArgumentException("Unknown sass singleton: " + value.getSingleton());
                    default:
                        throw new IllegalStateException("Unknown sass singleton: " + value.getSingleton());
                }

            case CALCULATION:
                Calculation calculation = value.getCalculation();
                if (targetType.isAssignableFrom(Calculation.class)) {
                    return (T) calculation;
                } else {
                    throw new IllegalArgumentException("Cant convert sass Calculation to " + targetType);
                }

            case COMPILER_FUNCTION:
                Value.CompilerFunction compilerFunction = value.getCompilerFunction();
                if (targetType.isAssignableFrom(Value.CompilerFunction.class)) {
                    return (T) compilerFunction;
                } else {
                    throw new IllegalArgumentException("Cant convert sass CompilerFunction to " + targetType);
                }
            case HOST_FUNCTION:
                Value.HostFunction hostFunction = value.getHostFunction();
                if (targetType.isAssignableFrom(Value.HostFunction.class)) {
                    return (T) hostFunction;
                } else {
                    throw new IllegalArgumentException("Cant convert sass HostFunction to " + targetType);
                }
            case VALUE_NOT_SET:
                return null;
            default:
                throw new IllegalStateException("Unknown value: " + value.getValueCase());
        }
    }

}
