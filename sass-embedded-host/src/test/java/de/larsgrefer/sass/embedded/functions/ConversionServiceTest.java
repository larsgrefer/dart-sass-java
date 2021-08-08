package de.larsgrefer.sass.embedded.functions;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.core.ParameterizedTypeReference;
import sass.embedded_protocol.EmbeddedSass;
import sass.embedded_protocol.EmbeddedSass.Value;
import sass.embedded_protocol.EmbeddedSass.Value.ValueCase;

import java.awt.*;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;


class ConversionServiceTest {

    @Test
    void nullConversion() {
        Value nullValue = ConversionService.toSassValue(null);

        assertThat(nullValue).isNotNull();
        assertThat(nullValue.getValueCase()).isEqualTo(ValueCase.SINGLETON);
        assertThat(nullValue.getSingleton()).isEqualTo(EmbeddedSass.SingletonValue.NULL);

        assertThat(ConversionService.toJavaValue(nullValue, String.class, null)).isNull();
    }

    @Test
    void booleanConversion() {
        Value trueValue = ConversionService.toSassValue(true);
        Value falseValue = ConversionService.toSassValue(false);

        assertThat(trueValue.getValueCase()).isEqualTo(ValueCase.SINGLETON);
        assertThat(falseValue.getValueCase()).isEqualTo(ValueCase.SINGLETON);

        assertThat(trueValue.getSingleton()).isEqualTo(EmbeddedSass.SingletonValue.TRUE);
        assertThat(falseValue.getSingleton()).isEqualTo(EmbeddedSass.SingletonValue.FALSE);

        assertThat(ConversionService.toJavaValue(trueValue, Boolean.class, null)).isTrue();
        assertThat(ConversionService.toJavaValue(falseValue, Boolean.class, null)).isFalse();

        assertThat(ConversionService.toJavaValue(trueValue, String.class, null)).isEqualTo("true");
        assertThat(ConversionService.toJavaValue(falseValue, String.class, null)).isEqualTo("false");
    }

    private static final List<Color> colors = Arrays.asList(Color.RED, Color.CYAN, Color.WHITE, Color.BLACK, Color.ORANGE);

    @TestFactory
    Stream<DynamicTest> colorConversion_rgb() {
        return colors.stream()
                .map(color -> DynamicTest.dynamicTest(color.toString(), () -> testColor_rgb(color)));
    }

    private void testColor_rgb(Color color) {
        Value sassColor = ConversionService.toSassValue(color);
        assertThat(sassColor).isNotNull();
        assertThat(ConversionService.toJavaValue(sassColor, Color.class, null)).isEqualTo(color);
    }

    @TestFactory
    Stream<DynamicTest> colorConversion_hsl() {
        return colors.stream()
                .map(col -> DynamicTest.dynamicTest(col.toString(), () -> testColor_hsl(col)));
    }

    private void testColor_hsl(Color col) {
        float[] floats = Color.RGBtoHSB(col.getRed(), col.getGreen(), col.getBlue(), null);

        Value.HslColor hslColor = Value.HslColor.newBuilder()
                .setHue(floats[0])
                .setSaturation(floats[1])
                .setLightness(floats[2])
                .setAlpha(1d)
                .build();

        assertThat(hslColor).isNotNull();

        Value sassValue = Value.newBuilder()
                .setHslColor(hslColor)
                .build();
        Color javaValue = ConversionService.toJavaValue(sassValue, Color.class, null);

        assertThat(javaValue).isEqualTo(col);
    }

    @Test
    void stringConversion() {
        Value foo = ConversionService.toSassValue("foo");
        Value bar = ConversionService.toSassValue("bar");

        assertThat(foo).isNotNull();
        assertThat(bar).isNotNull();

        assertThat(foo.getValueCase()).isEqualTo(ValueCase.STRING);
        assertThat(bar.getValueCase()).isEqualTo(ValueCase.STRING);

        assertThat(foo.getString().getText()).isEqualTo("foo");
        assertThat(bar.getString().getText()).isEqualTo("bar");

        String javaValue = ConversionService.toJavaValue(foo, String.class, null);

        assertThat(javaValue).isEqualTo("foo");
    }

    @Test
    void numberConversion_simple() {
        byte b = 1;
        short s = 2;
        int i = 3;
        long l = 4;
        float f = 5.1f;
        double d = 6.2;

        Value sassByte = ConversionService.toSassValue(b);
        assertThat(sassByte.getValueCase()).isEqualTo(ValueCase.NUMBER);

        Value sassShort = ConversionService.toSassValue(s);
        assertThat(sassShort.getValueCase()).isEqualTo(ValueCase.NUMBER);

        Value sassInt = ConversionService.toSassValue(i);
        assertThat(sassInt.getValueCase()).isEqualTo(ValueCase.NUMBER);

        Value sassLong = ConversionService.toSassValue(l);
        assertThat(sassLong.getValueCase()).isEqualTo(ValueCase.NUMBER);

        Value sassFloat = ConversionService.toSassValue(f);
        assertThat(sassFloat.getValueCase()).isEqualTo(ValueCase.NUMBER);

        Value sassDouble = ConversionService.toSassValue(d);
        assertThat(sassDouble.getValueCase()).isEqualTo(ValueCase.NUMBER);

        Byte javaByte = ConversionService.toJavaValue(sassByte, Byte.class, null);
        assertThat(javaByte).isEqualTo(b);

        Short javaShort = ConversionService.toJavaValue(sassShort, Short.class, null);
        assertThat(javaShort).isEqualTo(s);

        Integer javaInt = ConversionService.toJavaValue(sassInt, Integer.class, null);
        assertThat(javaInt).isEqualTo(i);

        Long javaLong = ConversionService.toJavaValue(sassLong, Long.class, null);
        assertThat(javaLong).isEqualTo(l);

        Float javaFloat = ConversionService.toJavaValue(sassFloat, Float.class, null);
        assertThat(javaFloat).isEqualTo(f);

        Double javaDouble = ConversionService.toJavaValue(sassDouble, Double.class, null);
        assertThat(javaDouble).isEqualTo(d);
    }

    @Test
    void numberConversion_extended() {
        BigInteger i = BigInteger.valueOf(42);
        BigDecimal d = BigDecimal.valueOf(47.11);

        Value sassBigInt = ConversionService.toSassValue(i);
        Value sassBigDec = ConversionService.toSassValue(d);

        assertThat(sassBigInt.getValueCase()).isEqualTo(ValueCase.NUMBER);
        assertThat(sassBigDec.getValueCase()).isEqualTo(ValueCase.NUMBER);

        BigInteger bigInteger = ConversionService.toJavaValue(sassBigInt, BigInteger.class, null);
        BigDecimal bigDecimal = ConversionService.toJavaValue(sassBigDec, BigDecimal.class, null);

        assertThat(bigInteger).isEqualTo(i);
        assertThat(bigDecimal).isEqualTo(d);
    }

    @Test
    void listConversion() {
        List<String> stringList = Arrays.asList("foo", "bar");

        Value value = ConversionService.toSassValue(stringList);

        assertThat(value.getValueCase()).isEqualTo(ValueCase.LIST);

        Value.List sassList = value.getList();

        assertThat(sassList.getContentsCount()).isEqualTo(stringList.size());
        assertThat(sassList.getContentsList()).allMatch(v -> v.getValueCase().equals(ValueCase.STRING));

        Type listType = new ParameterizedTypeReference<List<String>>() {
        }.getType();
        List<String> list = ConversionService.toJavaValue(value, List.class, listType);

        assertThat(list).containsExactly("foo", "bar");
    }

    @Test
    void mapConverstion() {
        Map<String, Integer> intMap = new HashMap<>();
        intMap.put("foo", 1);
        intMap.put("bar", 2);

        Value value = ConversionService.toSassValue(intMap);

        assertThat(value.getValueCase()).isEqualTo(ValueCase.MAP);

        Value.Map sassMap = value.getMap();

        assertThat(sassMap.getEntriesCount()).isEqualTo(2);

        Type mapType = new ParameterizedTypeReference<Map<String, Integer>>() {
        }.getType();
        Map<String, Integer> map = ConversionService.toJavaValue(value, Map.class, mapType);

        assertThat(map).containsEntry("foo", 1);
        assertThat(map).containsEntry("bar", 2);
    }

    @Test
    void sassValueConversion() {
        assertThat(ConversionService.toSassValue(Value.String.getDefaultInstance()).getValueCase()).isEqualTo(ValueCase.STRING);
        assertThat(ConversionService.toSassValue(Value.Number.getDefaultInstance()).getValueCase()).isEqualTo(ValueCase.NUMBER);
        assertThat(ConversionService.toSassValue(Value.RgbColor.getDefaultInstance()).getValueCase()).isEqualTo(ValueCase.RGB_COLOR);
        assertThat(ConversionService.toSassValue(Value.HslColor.getDefaultInstance()).getValueCase()).isEqualTo(ValueCase.HSL_COLOR);
        assertThat(ConversionService.toSassValue(Value.List.getDefaultInstance()).getValueCase()).isEqualTo(ValueCase.LIST);
        assertThat(ConversionService.toSassValue(Value.Map.getDefaultInstance()).getValueCase()).isEqualTo(ValueCase.MAP);
        assertThat(ConversionService.toSassValue(EmbeddedSass.SingletonValue.NULL).getValueCase()).isEqualTo(ValueCase.SINGLETON);
    }
}