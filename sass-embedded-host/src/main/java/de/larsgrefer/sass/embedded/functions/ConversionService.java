package de.larsgrefer.sass.embedded.functions;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import sass.embedded_protocol.EmbeddedSass;

import java.awt.*;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Lars Grefer
 */
@UtilityClass
class ConversionService {

    static EmbeddedSass.Value toSassValue(Object object) {
        if (object == null) {
            return EmbeddedSass.Value.newBuilder()
                    .setSingleton(EmbeddedSass.SingletonValue.NULL)
                    .build();
        }

        if (object instanceof Boolean) {
            return EmbeddedSass.Value.newBuilder()
                    .setSingleton((Boolean) object ? EmbeddedSass.SingletonValue.TRUE : EmbeddedSass.SingletonValue.FALSE)
                    .build();
        }

        if (object instanceof CharSequence) {
            EmbeddedSass.Value.String sassString = EmbeddedSass.Value.String.newBuilder()
                    .setQuoted(true)
                    .setText(object.toString())
                    .build();
            return EmbeddedSass.Value.newBuilder()
                    .setString(sassString)
                    .build();
        }

        if (object instanceof Number) {
            EmbeddedSass.Value.Number sassNumber = EmbeddedSass.Value.Number.newBuilder()
                    .setValue(((Number) object).doubleValue())
                    .build();
            return EmbeddedSass.Value.newBuilder()
                    .setNumber(sassNumber)
                    .build();
        }

        if (object instanceof Color) {
            Color color = (Color) object;

            EmbeddedSass.Value.RgbColor sassColor = EmbeddedSass.Value.RgbColor.newBuilder()
                    .setRed(color.getRed())
                    .setGreen(color.getGreen())
                    .setBlue(color.getBlue())
                    .setAlpha(color.getAlpha() / 255d)
                    .build();
            return EmbeddedSass.Value.newBuilder()
                    .setRgbColor(sassColor)
                    .build();
        }

        if (object instanceof Collection) {
            java.util.List<EmbeddedSass.Value> sassValues = ((Collection<?>) object)
                    .stream()
                    .map(ConversionService::toSassValue)
                    .collect(Collectors.toList());

            return EmbeddedSass.Value.newBuilder()
                    .setList(EmbeddedSass.Value.List.newBuilder()
                            .addAllContents(sassValues)
                            .build())
                    .build();
        }

        if (object instanceof Map) {
            List<EmbeddedSass.Value.Map.Entry> sassEntries = ((Map<?, ?>) object).entrySet()
                    .stream()
                    .map(entry -> EmbeddedSass.Value.Map.Entry.newBuilder()
                            .setKey(toSassValue(entry.getKey()))
                            .setValue(toSassValue(entry.getValue()))
                            .build())
                    .collect(Collectors.toList());

            return EmbeddedSass.Value.newBuilder()
                    .setMap(EmbeddedSass.Value.Map.newBuilder()
                            .addAllEntries(sassEntries)
                            .build())
                    .build();
        }


        if (object instanceof EmbeddedSass.Value) {
            return (EmbeddedSass.Value) object;
        }

        if (object instanceof EmbeddedSass.Value.String) {
            return EmbeddedSass.Value.newBuilder()
                    .setString((EmbeddedSass.Value.String) object)
                    .build();
        }

        if (object instanceof EmbeddedSass.Value.Number) {
            return EmbeddedSass.Value.newBuilder()
                    .setNumber((EmbeddedSass.Value.Number) object)
                    .build();
        }

        if (object instanceof EmbeddedSass.Value.RgbColor) {
            return EmbeddedSass.Value.newBuilder()
                    .setRgbColor((EmbeddedSass.Value.RgbColor) object)
                    .build();
        }

        if (object instanceof EmbeddedSass.Value.HslColor) {
            return EmbeddedSass.Value.newBuilder()
                    .setHslColor((EmbeddedSass.Value.HslColor) object)
                    .build();
        }

        if (object instanceof EmbeddedSass.Value.List) {
            return EmbeddedSass.Value.newBuilder()
                    .setList((EmbeddedSass.Value.List) object)
                    .build();
        }

        if (object instanceof EmbeddedSass.Value.Map) {
            return EmbeddedSass.Value.newBuilder()
                    .setMap((EmbeddedSass.Value.Map) object)
                    .build();
        }

        if (object instanceof EmbeddedSass.SingletonValue) {
            return EmbeddedSass.Value.newBuilder()
                    .setSingleton((EmbeddedSass.SingletonValue) object)
                    .build();
        }

        throw new RuntimeException("Cant convert to Sass value");
    }

    static <T> T toJavaValue(@NonNull EmbeddedSass.Value value, Class<T> targetType, Type parameterizedType) {
        if (targetType.equals(EmbeddedSass.Value.class)) {
            return (T) value;
        }

        switch (value.getValueCase()) {
            case STRING:
                EmbeddedSass.Value.String sassString = value.getString();
                if (targetType.isAssignableFrom(EmbeddedSass.Value.String.class)) {
                    return (T) sassString;
                }
                else if (targetType.isAssignableFrom(String.class)) {
                    return (T) sassString.getText();
                }
                else {
                    throw new IllegalArgumentException("Cant convert sass String to " + targetType);
                }
            case NUMBER:
                EmbeddedSass.Value.Number sassNumber = value.getNumber();
                double javaNumber = sassNumber.getValue();
                if (targetType.isAssignableFrom(EmbeddedSass.Value.Number.class)) {
                    return (T) sassNumber;
                }
                else if (targetType.isAssignableFrom(Double.class) || targetType.isAssignableFrom(Double.TYPE)) {
                    return (T) Double.valueOf(javaNumber);
                }
                else if (targetType.isAssignableFrom(Float.class) || targetType.isAssignableFrom(Float.TYPE)) {
                    return (T) Float.valueOf((float) javaNumber);
                }
                else if (targetType.isAssignableFrom(Long.class) || targetType.isAssignableFrom(Long.TYPE)) {
                    return (T) Long.valueOf((long) javaNumber);
                }
                else if (targetType.isAssignableFrom(Integer.class) || targetType.isAssignableFrom(Integer.TYPE)) {
                    return (T) Integer.valueOf((int) javaNumber);
                }
                else if (targetType.isAssignableFrom(Short.class) || targetType.isAssignableFrom(Short.TYPE)) {
                    return (T) Short.valueOf((short) javaNumber);
                }
                else if (targetType.isAssignableFrom(Byte.class) || targetType.isAssignableFrom(Byte.TYPE)) {
                    return (T) Byte.valueOf((byte) javaNumber);
                }
                else if (targetType.isAssignableFrom(BigInteger.class)) {
                    return (T) BigInteger.valueOf((long) javaNumber);
                }
                else if (targetType.isAssignableFrom(BigDecimal.class)) {
                    return (T) BigDecimal.valueOf(javaNumber);
                }
                else if (targetType.isAssignableFrom(String.class)) {
                    return (T) Double.toString(javaNumber);
                }
                else {
                    throw new IllegalArgumentException("Cant convert sass Number to " + targetType);
                }
            case RGB_COLOR:
                EmbeddedSass.Value.RgbColor rgbColor = value.getRgbColor();
                if (targetType.isAssignableFrom(EmbeddedSass.Value.RgbColor.class)) {
                    return (T) rgbColor;
                }
                else if (targetType.isAssignableFrom(Color.class)) {
                    return (T) new Color(rgbColor.getRed(), rgbColor.getGreen(), rgbColor.getBlue(), (int) (rgbColor.getAlpha() * 255));
                }
                else {
                    throw new IllegalArgumentException("Cant convert sass RgbColor to " + targetType);
                }
            case HSL_COLOR:
                EmbeddedSass.Value.HslColor hslColor = value.getHslColor();
                if (targetType.isAssignableFrom(EmbeddedSass.Value.HslColor.class)) {
                    return (T) hslColor;
                }
                else if (targetType.isAssignableFrom(Color.class)) {
                    Color color = Color.getHSBColor(
                            (float) hslColor.getHue(),
                            (float) hslColor.getSaturation(),
                            (float) hslColor.getLightness()
                    );
                    return (T) new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (hslColor.getAlpha() * 255));
                }
                else {
                    throw new IllegalArgumentException("Cant convert sass RgbColor to " + targetType);
                }
            case LIST:
                EmbeddedSass.Value.List sassList = value.getList();
                if (targetType.isAssignableFrom(EmbeddedSass.Value.List.class)) {
                    return (T) sassList;
                }
                else if (targetType.isAssignableFrom(List.class)) {
                    Type elementType = ((ParameterizedType) parameterizedType).getActualTypeArguments()[0];

                    Class<?> elementClass = elementType instanceof Class<?> ? (Class<?>) elementType : (Class<?>) ((ParameterizedType) elementType).getRawType();

                    List<?> collect = sassList.getContentsList().stream()
                            .map(elementValue -> toJavaValue(elementValue, elementClass, elementType))
                            .collect(Collectors.toList());

                    return (T) Collections.unmodifiableList(collect);
                }
                else {
                    throw new IllegalArgumentException("Cant convert sass List to " + targetType);
                }
            case MAP:
                EmbeddedSass.Value.Map sassMap = value.getMap();
                if (targetType.isAssignableFrom(EmbeddedSass.Value.Map.class)) {
                    return (T) sassMap;
                }
                else if (targetType.isAssignableFrom(Map.class)) {
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
                }
                else {
                    throw new IllegalArgumentException("Cant convert sass Map to " + targetType);
                }
            case SINGLETON:
                EmbeddedSass.SingletonValue singleton = value.getSingleton();
                switch (value.getSingleton()) {
                    case TRUE:
                    case FALSE:
                        Boolean boolValue = singleton == EmbeddedSass.SingletonValue.TRUE;

                        if (targetType.equals(Boolean.class) || targetType.equals(boolean.class)) {
                            return (T) boolValue;
                        }
                        else if (targetType.equals(String.class)) {
                            return (T) Boolean.toString(boolValue);
                        }

                        throw new IllegalArgumentException("Cant convert sass boolean to " + targetType);
                    case NULL:
                        return null;
                    case UNRECOGNIZED:
                        throw new IllegalArgumentException("Unknown sass singleton: " + value.getSingleton());
                    default:
                        throw new IllegalStateException("Unknown sass singleton: " + value.getSingleton());
                }


            case COMPILER_FUNCTION:
                throw new IllegalArgumentException("Cant convert sass CompilerFunction to " + targetType);
            case HOST_FUNCTION:
                throw new IllegalArgumentException("Cant convert sass HostFunction to " + targetType);
            case VALUE_NOT_SET:
                return null;
            default:
                throw new IllegalStateException("Unknown value: " + value.getValueCase());
        }
    }
}
