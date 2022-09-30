package de.larsgrefer.sass.embedded.functions;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.core.ParameterizedTypeReference;
import sass.embedded_protocol.EmbeddedSass;
import sass.embedded_protocol.EmbeddedSass.Value;
import sass.embedded_protocol.EmbeddedSass.Value.ValueCase;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static de.larsgrefer.sass.embedded.functions.ConversionService.toJavaValue;
import static de.larsgrefer.sass.embedded.functions.ConversionService.toSassValue;
import static org.assertj.core.api.Assertions.assertThat;


class ConversionServiceTest {

    @Test
    void nullConversion() {
        Value nullValue = toSassValue(null);

        assertThat(nullValue).isNotNull();
        assertThat(nullValue.getValueCase()).isEqualTo(ValueCase.SINGLETON);
        assertThat(nullValue.getSingleton()).isEqualTo(EmbeddedSass.SingletonValue.NULL);

        assertThat(toJavaValue(nullValue, String.class, null)).isNull();
    }

    @Test
    void booleanConversion() {
        Value trueValue = toSassValue(true);
        Value falseValue = toSassValue(false);

        assertThat(trueValue.getValueCase()).isEqualTo(ValueCase.SINGLETON);
        assertThat(falseValue.getValueCase()).isEqualTo(ValueCase.SINGLETON);

        assertThat(trueValue.getSingleton()).isEqualTo(EmbeddedSass.SingletonValue.TRUE);
        assertThat(falseValue.getSingleton()).isEqualTo(EmbeddedSass.SingletonValue.FALSE);

        assertThat(toJavaValue(trueValue, Boolean.class, null)).isTrue();
        assertThat(toJavaValue(falseValue, Boolean.class, null)).isFalse();

        assertThat(toJavaValue(trueValue, String.class, null)).isEqualTo("true");
        assertThat(toJavaValue(falseValue, String.class, null)).isEqualTo("false");
    }

    @Test
    void stringConversion() {
        Value foo = toSassValue("foo");
        Value bar = toSassValue("bar");

        assertThat(foo).isNotNull();
        assertThat(bar).isNotNull();

        assertThat(foo.getValueCase()).isEqualTo(ValueCase.STRING);
        assertThat(bar.getValueCase()).isEqualTo(ValueCase.STRING);

        assertThat(foo.getString().getText()).isEqualTo("foo");
        assertThat(bar.getString().getText()).isEqualTo("bar");

        String javaValue = toJavaValue(foo, String.class, null);

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

        Value sassByte = toSassValue(b);
        assertThat(sassByte.getValueCase()).isEqualTo(ValueCase.NUMBER);

        Value sassShort = toSassValue(s);
        assertThat(sassShort.getValueCase()).isEqualTo(ValueCase.NUMBER);

        Value sassInt = toSassValue(i);
        assertThat(sassInt.getValueCase()).isEqualTo(ValueCase.NUMBER);

        Value sassLong = toSassValue(l);
        assertThat(sassLong.getValueCase()).isEqualTo(ValueCase.NUMBER);

        Value sassFloat = toSassValue(f);
        assertThat(sassFloat.getValueCase()).isEqualTo(ValueCase.NUMBER);

        Value sassDouble = toSassValue(d);
        assertThat(sassDouble.getValueCase()).isEqualTo(ValueCase.NUMBER);

        Byte javaByte = toJavaValue(sassByte, Byte.class, null);
        assertThat(javaByte).isEqualTo(b);

        Short javaShort = toJavaValue(sassShort, Short.class, null);
        assertThat(javaShort).isEqualTo(s);

        Integer javaInt = toJavaValue(sassInt, Integer.class, null);
        assertThat(javaInt).isEqualTo(i);

        Long javaLong = toJavaValue(sassLong, Long.class, null);
        assertThat(javaLong).isEqualTo(l);

        Float javaFloat = toJavaValue(sassFloat, Float.class, null);
        assertThat(javaFloat).isEqualTo(f);

        Double javaDouble = toJavaValue(sassDouble, Double.class, null);
        assertThat(javaDouble).isEqualTo(d);
    }

    @Test
    void numberConversion_extended() {
        BigInteger i = BigInteger.valueOf(42);
        BigDecimal d = BigDecimal.valueOf(47.11);

        Value sassBigInt = toSassValue(i);
        Value sassBigDec = toSassValue(d);

        assertThat(sassBigInt.getValueCase()).isEqualTo(ValueCase.NUMBER);
        assertThat(sassBigDec.getValueCase()).isEqualTo(ValueCase.NUMBER);

        BigInteger bigInteger = toJavaValue(sassBigInt, BigInteger.class, null);
        BigDecimal bigDecimal = toJavaValue(sassBigDec, BigDecimal.class, null);

        assertThat(bigInteger).isEqualTo(i);
        assertThat(bigDecimal).isEqualTo(d);
    }

    @Test
    void listConversion() {
        List<String> stringList = Arrays.asList("foo", "bar");

        Value value = toSassValue(stringList);

        assertThat(value.getValueCase()).isEqualTo(ValueCase.LIST);

        Value.List sassList = value.getList();

        assertThat(sassList.getContentsCount()).isEqualTo(stringList.size());
        assertThat(sassList.getContentsList()).allMatch(v -> v.getValueCase().equals(ValueCase.STRING));

        Type listType = new ParameterizedTypeReference<List<String>>() {
        }.getType();
        List<String> list = toJavaValue(value, List.class, listType);

        assertThat(list).containsExactly("foo", "bar");
    }

    @Test
    void mapConverstion() {
        Map<String, Integer> intMap = new HashMap<>();
        intMap.put("foo", 1);
        intMap.put("bar", 2);

        Value value = toSassValue(intMap);

        assertThat(value.getValueCase()).isEqualTo(ValueCase.MAP);

        Value.Map sassMap = value.getMap();

        assertThat(sassMap.getEntriesCount()).isEqualTo(2);

        Type mapType = new ParameterizedTypeReference<Map<String, Integer>>() {
        }.getType();
        Map<String, Integer> map = toJavaValue(value, Map.class, mapType);

        assertThat(map).containsEntry("foo", 1);
        assertThat(map).containsEntry("bar", 2);
    }

    @Test
    void sassValueConversion() {
        assertThat(toSassValue(Value.String.getDefaultInstance()).getValueCase()).isEqualTo(ValueCase.STRING);
        assertThat(toSassValue(Value.Number.getDefaultInstance()).getValueCase()).isEqualTo(ValueCase.NUMBER);
        assertThat(toSassValue(Value.RgbColor.getDefaultInstance()).getValueCase()).isEqualTo(ValueCase.RGB_COLOR);
        assertThat(toSassValue(Value.HslColor.getDefaultInstance()).getValueCase()).isEqualTo(ValueCase.HSL_COLOR);
        assertThat(toSassValue(Value.List.getDefaultInstance()).getValueCase()).isEqualTo(ValueCase.LIST);
        assertThat(toSassValue(Value.Map.getDefaultInstance()).getValueCase()).isEqualTo(ValueCase.MAP);
        assertThat(toSassValue(EmbeddedSass.SingletonValue.NULL).getValueCase()).isEqualTo(ValueCase.SINGLETON);
    }

    @TestFactory
    Stream<DynamicTest> sassValueConversion2() {
        return Stream.of(
                        Value.String.getDefaultInstance(),
                        Value.Number.getDefaultInstance(),
                        Value.RgbColor.getDefaultInstance(),
                        Value.HslColor.getDefaultInstance(),
                        Value.List.getDefaultInstance(),
                        Value.Map.getDefaultInstance(),
                        EmbeddedSass.SingletonValue.NULL
                )
                .map(val -> DynamicTest.dynamicTest("foo-" + val.toString(), () -> {
                    Value value = toSassValue(val);
                    Object javaValue = toJavaValue(value, val.getClass(), null);

                    assertThat(javaValue).isEqualTo(val);
                }));
    }
}