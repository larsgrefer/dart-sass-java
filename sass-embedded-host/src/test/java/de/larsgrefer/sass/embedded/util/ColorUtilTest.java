package de.larsgrefer.sass.embedded.util;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import sass.embedded_protocol.EmbeddedSass.Value.HslColor;
import sass.embedded_protocol.EmbeddedSass.Value.HwbColor;
import sass.embedded_protocol.EmbeddedSass.Value.RgbColor;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ColorUtilTest {

    private static final RgbColor rgbRed = RgbColor.newBuilder()
            .setRed(255)
            .setAlpha(1d)
            .build();

    private static final RgbColor rgbBlack = RgbColor.newBuilder()
            .setAlpha(1d)
            .build();

    private static final RgbColor rgbGrey = RgbColor.newBuilder()
            .setRed(128)
            .setGreen(128)
            .setBlue(128)
            .setAlpha(1d)
            .build();

    @Test
    void toJavaColor_hwb_red() {
        HwbColor red = HwbColor.newBuilder()
                .setHue(0d)
                .setWhiteness(0d)
                .setBlackness(0d)
                .setAlpha(1d)
                .build();

        RgbColor javaRed = ColorUtil.toRgbColor(red);
        assertThat(javaRed).isEqualTo(rgbRed);
    }

    @Test
    void toJavaColor_hwb_black() {
        HwbColor black = HwbColor.newBuilder()
                .setHue(120d)
                .setWhiteness(0d)
                .setBlackness(100d)
                .setAlpha(1d)
                .build();

        RgbColor javaRed = ColorUtil.toRgbColor(black);
        assertThat(javaRed).isEqualTo(rgbBlack);
    }

    @Test
    void toJavaColor_hwb_grey() {
        HwbColor grey = HwbColor.newBuilder()
                .setHue(120d)
                .setWhiteness(50d)
                .setBlackness(50d)
                .setAlpha(1d)
                .build();

        RgbColor javaRed = ColorUtil.toRgbColor(grey);
        assertThat(javaRed).isEqualTo(rgbGrey);
    }

    @TestFactory
    Stream<DynamicTest> circle() {

        return Stream.of(rgbRed, rgbBlack, rgbGrey)
                .map(rgbColor -> DynamicTest.dynamicTest(rgbColor.toString(), () -> {
                    HwbColor hwbColor = ColorUtil.toHwbColor(rgbColor);
                    HslColor hslColor = ColorUtil.toHslColor(hwbColor);

                    assertThat(ColorUtil.toRgbColor(hslColor)).isEqualTo(rgbColor);
                }));
    }
}