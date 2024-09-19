package de.larsgrefer.sass.embedded.util;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import com.sass_lang.embedded_protocol.Value.Color;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ColorUtilTest {

    private static final Color rgbRed = Color.newBuilder()
            .setSpace("rgb")
            .setChannel1(255)
            .setAlpha(1d)
            .build();

    private static final Color rgbBlack = Color.newBuilder()
            .setSpace("rgb")
            .setAlpha(1d)
            .build();

    private static final Color rgbGrey = Color.newBuilder()
            .setSpace("rgb")
            .setChannel1(128)
            .setChannel2(128)
            .setChannel3(128)
            .setAlpha(1d)
            .build();

    @Test
    void toJavaColor_hwb_red() {
        Color red = Color.newBuilder()
                .setSpace("hwb")
                .setChannel1(0d)
                .setChannel2(0d)
                .setChannel3(0d)
                .setAlpha(1d)
                .build();

        Color javaRed = ColorUtil.toRgbColor(red);
        assertThat(javaRed).isEqualTo(rgbRed);
    }

    @Test
    void toJavaColor_hwb_black() {
        Color black = Color.newBuilder()
                .setSpace("hwb")
                .setChannel1(120d)
                .setChannel2(0d)
                .setChannel3(100d)
                .setAlpha(1d)
                .build();

        Color javaRed = ColorUtil.toRgbColor(black);
        assertThat(javaRed).isEqualTo(rgbBlack);
    }

    @Test
    void toJavaColor_hwb_grey() {
        Color grey = Color.newBuilder()
                .setSpace("hwb")
                .setChannel1(120d)
                .setChannel2(50d)
                .setChannel3(50d)
                .setAlpha(1d)
                .build();

        Color javaRed = ColorUtil.toRgbColor(grey);
        assertThat(javaRed).isEqualTo(rgbGrey);
    }

    @TestFactory
    Stream<DynamicTest> circle() {

        return Stream.of(rgbRed, rgbBlack, rgbGrey)
                .map(rgbColor -> DynamicTest.dynamicTest(rgbColor.toString(), () -> {
                    Color hwbColor = ColorUtil.toHwbColor(rgbColor);
                    Color hslColor = ColorUtil.toHslColor(hwbColor);

                    assertThat(ColorUtil.toRgbColor(hslColor)).isEqualTo(rgbColor);
                }));
    }
}