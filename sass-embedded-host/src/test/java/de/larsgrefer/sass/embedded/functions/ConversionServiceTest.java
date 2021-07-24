package de.larsgrefer.sass.embedded.functions;

import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import sass.embedded_protocol.EmbeddedSass;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


class ConversionServiceTest {

    @Test
    void nullConversion() {
        EmbeddedSass.Value nullValue = ConversionService.toSassValue(null);

        assertThat(nullValue).isNotNull();
        assertThat(nullValue.getValueCase()).isEqualTo(EmbeddedSass.Value.ValueCase.SINGLETON);
        assertThat(nullValue.getSingleton()).isEqualTo(EmbeddedSass.Value.Singleton.NULL);

        assertThat(ConversionService.toJavaValue(nullValue, String.class, null)).isNull();
    }

    @Test
    void booleanConversion() {
        EmbeddedSass.Value trueValue = ConversionService.toSassValue(true);
        EmbeddedSass.Value falseValue = ConversionService.toSassValue(false);

        assertThat(trueValue.getValueCase()).isEqualTo(EmbeddedSass.Value.ValueCase.SINGLETON);
        assertThat(falseValue.getValueCase()).isEqualTo(EmbeddedSass.Value.ValueCase.SINGLETON);

        assertThat(trueValue.getSingleton()).isEqualTo(EmbeddedSass.Value.Singleton.TRUE);
        assertThat(falseValue.getSingleton()).isEqualTo(EmbeddedSass.Value.Singleton.FALSE);

        assertThat(ConversionService.toJavaValue(trueValue, Boolean.class, null)).isTrue();
        assertThat(ConversionService.toJavaValue(falseValue, Boolean.class, null)).isFalse();

        assertThat(ConversionService.toJavaValue(trueValue, String.class, null)).isEqualTo("true");
        assertThat(ConversionService.toJavaValue(falseValue, String.class, null)).isEqualTo("false");
    }

    @Test
    void stringConversion() {
        EmbeddedSass.Value foo = ConversionService.toSassValue("foo");
        EmbeddedSass.Value bar = ConversionService.toSassValue("bar");

        assertThat(foo).isNotNull();
        assertThat(bar).isNotNull();

        assertThat(foo.getValueCase()).isEqualTo(EmbeddedSass.Value.ValueCase.STRING);
        assertThat(bar.getValueCase()).isEqualTo(EmbeddedSass.Value.ValueCase.STRING);

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

        EmbeddedSass.Value sassByte = ConversionService.toSassValue(b);
        assertThat(sassByte.getValueCase()).isEqualTo(EmbeddedSass.Value.ValueCase.NUMBER);

        EmbeddedSass.Value sassShort = ConversionService.toSassValue(s);
        assertThat(sassShort.getValueCase()).isEqualTo(EmbeddedSass.Value.ValueCase.NUMBER);

        EmbeddedSass.Value sassInt = ConversionService.toSassValue(i);
        assertThat(sassInt.getValueCase()).isEqualTo(EmbeddedSass.Value.ValueCase.NUMBER);

        EmbeddedSass.Value sassLong = ConversionService.toSassValue(l);
        assertThat(sassLong.getValueCase()).isEqualTo(EmbeddedSass.Value.ValueCase.NUMBER);

        EmbeddedSass.Value sassFloat = ConversionService.toSassValue(f);
        assertThat(sassFloat.getValueCase()).isEqualTo(EmbeddedSass.Value.ValueCase.NUMBER);

        EmbeddedSass.Value sassDouble = ConversionService.toSassValue(d);
        assertThat(sassDouble.getValueCase()).isEqualTo(EmbeddedSass.Value.ValueCase.NUMBER);

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

        EmbeddedSass.Value sassBigInt = ConversionService.toSassValue(i);
        EmbeddedSass.Value sassBigDec = ConversionService.toSassValue(d);

        assertThat(sassBigInt.getValueCase()).isEqualTo(EmbeddedSass.Value.ValueCase.NUMBER);
        assertThat(sassBigDec.getValueCase()).isEqualTo(EmbeddedSass.Value.ValueCase.NUMBER);

        BigInteger bigInteger = ConversionService.toJavaValue(sassBigInt, BigInteger.class, null);
        BigDecimal bigDecimal = ConversionService.toJavaValue(sassBigDec, BigDecimal.class, null);

        assertThat(bigInteger).isEqualTo(i);
        assertThat(bigDecimal).isEqualTo(d);
    }

    @Test
    void listConversion() {
        List<String> stringList = Arrays.asList("foo", "bar");

        EmbeddedSass.Value value = ConversionService.toSassValue(stringList);

        assertThat(value.getValueCase()).isEqualTo(EmbeddedSass.Value.ValueCase.LIST);

        EmbeddedSass.Value.List sassList = value.getList();

        assertThat(sassList.getContentsCount()).isEqualTo(stringList.size());
        assertThat(sassList.getContentsList()).allMatch(v -> v.getValueCase().equals(EmbeddedSass.Value.ValueCase.STRING));

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

        EmbeddedSass.Value value = ConversionService.toSassValue(intMap);

        assertThat(value.getValueCase()).isEqualTo(EmbeddedSass.Value.ValueCase.MAP);

        EmbeddedSass.Value.Map sassMap = value.getMap();

        assertThat(sassMap.getEntriesCount()).isEqualTo(2);

        Type mapType = new ParameterizedTypeReference<Map<String, Integer>>() {
        }.getType();
        Map<String, Integer> map = ConversionService.toJavaValue(value, Map.class, mapType);

        assertThat(map).containsEntry("foo", 1);
        assertThat(map).containsEntry("bar", 2);
    }
}