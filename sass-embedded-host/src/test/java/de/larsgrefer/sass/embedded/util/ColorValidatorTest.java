package de.larsgrefer.sass.embedded.util;

import org.junit.jupiter.api.Test;
import com.sass_lang.embedded_protocol.Value.Color;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ColorValidatorTest {

    @Test
    void assertValid_rgb() {
        Color rgbColor = Color.newBuilder().setSpace("rgb").build();

        ColorValidator.assertValid(rgbColor);

        assertThrows(IllegalArgumentException.class, () -> ColorValidator.assertValid((Color) null));

        assertThrows(IllegalArgumentException.class, () -> ColorValidator.assertValid(rgbColor.toBuilder().setChannel1(500)));
        assertThrows(IllegalArgumentException.class, () -> ColorValidator.assertValid(rgbColor.toBuilder().setChannel1(-1)));
        assertThrows(IllegalArgumentException.class, () -> ColorValidator.assertValid(rgbColor.toBuilder().setChannel2(500)));
        assertThrows(IllegalArgumentException.class, () -> ColorValidator.assertValid(rgbColor.toBuilder().setChannel2(-1)));
        assertThrows(IllegalArgumentException.class, () -> ColorValidator.assertValid(rgbColor.toBuilder().setChannel3(500)));
        assertThrows(IllegalArgumentException.class, () -> ColorValidator.assertValid(rgbColor.toBuilder().setChannel3(-1)));
        assertThrows(IllegalArgumentException.class, () -> ColorValidator.assertValid(rgbColor.toBuilder().setAlpha(2.3d)));
        assertThrows(IllegalArgumentException.class, () -> ColorValidator.assertValid(rgbColor.toBuilder().setAlpha(-2.3d)));
    }

    @Test
    void assertValid_hsl() {
        Color hslColor = Color.newBuilder().setSpace("hsl").build();

        ColorValidator.assertValid(hslColor);

        assertThrows(IllegalArgumentException.class, () -> ColorValidator.assertValid((Color) null));

        assertThrows(IllegalArgumentException.class, () -> ColorValidator.assertValid(hslColor.toBuilder().setChannel3(101)));
        assertThrows(IllegalArgumentException.class, () -> ColorValidator.assertValid(hslColor.toBuilder().setChannel3(-1)));
        assertThrows(IllegalArgumentException.class, () -> ColorValidator.assertValid(hslColor.toBuilder().setChannel2(101)));
        assertThrows(IllegalArgumentException.class, () -> ColorValidator.assertValid(hslColor.toBuilder().setChannel2(-1)));
        assertThrows(IllegalArgumentException.class, () -> ColorValidator.assertValid(hslColor.toBuilder().setAlpha(2.3d)));
        assertThrows(IllegalArgumentException.class, () -> ColorValidator.assertValid(hslColor.toBuilder().setAlpha(-2.3d)));
    }

    @Test
    void assertValid_hwb() {
        Color hwbColor = Color.newBuilder().setSpace("hwb").build();

        ColorValidator.assertValid(hwbColor);

        assertThrows(IllegalArgumentException.class, () -> ColorValidator.assertValid((Color) null));

        assertThrows(IllegalArgumentException.class, () -> ColorValidator.assertValid(hwbColor.toBuilder().setChannel2(101)));
        assertThrows(IllegalArgumentException.class, () -> ColorValidator.assertValid(hwbColor.toBuilder().setChannel2(-1)));
        assertThrows(IllegalArgumentException.class, () -> ColorValidator.assertValid(hwbColor.toBuilder().setChannel3(101)));
        assertThrows(IllegalArgumentException.class, () -> ColorValidator.assertValid(hwbColor.toBuilder().setChannel3(-1)));
        assertThrows(IllegalArgumentException.class, () -> ColorValidator.assertValid(hwbColor.toBuilder().setChannel2(51).setChannel3(51)));
        assertThrows(IllegalArgumentException.class, () -> ColorValidator.assertValid(hwbColor.toBuilder().setAlpha(2.3d)));
        assertThrows(IllegalArgumentException.class, () -> ColorValidator.assertValid(hwbColor.toBuilder().setAlpha(-2.3d)));
    }
}