package de.larsgrefer.sass.embedded.functions;

import org.junit.jupiter.api.Test;
import sass.embedded_protocol.EmbeddedSass;

import java.awt.*;

import static org.assertj.core.api.Assertions.assertThat;

class ColorConverterTest {

    @Test
    void toJavaColor_hwb_red() {
        EmbeddedSass.Value.HwbColor red = EmbeddedSass.Value.HwbColor.newBuilder()
                .setHue(0d)
                .setWhiteness(0d)
                .setBlackness(0d)
                .setAlpha(1d)
                .build();

        Color javaRed = ColorConverter.toJavaColor(red);
        assertThat(javaRed).isEqualTo(Color.RED);
    }

    @Test
    void toJavaColor_hwb_black() {
        EmbeddedSass.Value.HwbColor black = EmbeddedSass.Value.HwbColor.newBuilder()
                .setHue(.33d)
                .setWhiteness(0d)
                .setBlackness(1d)
                .setAlpha(1d)
                .build();

        Color javaRed = ColorConverter.toJavaColor(black);
        assertThat(javaRed).isEqualTo(Color.BLACK);
    }

    @Test
    void toJavaColor_hwb_grey() {
        EmbeddedSass.Value.HwbColor grey = EmbeddedSass.Value.HwbColor.newBuilder()
                .setHue(.33d)
                .setWhiteness(1d)
                .setBlackness(1d)
                .setAlpha(1d)
                .build();

        Color javaRed = ColorConverter.toJavaColor(grey);
        assertThat(javaRed).isEqualTo(Color.GRAY);
    }
}