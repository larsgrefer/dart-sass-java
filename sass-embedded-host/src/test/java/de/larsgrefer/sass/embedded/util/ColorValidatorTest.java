package de.larsgrefer.sass.embedded.util;

import org.junit.jupiter.api.Test;
import com.sass_lang.embedded_protocol.Value.HslColor;
import com.sass_lang.embedded_protocol.Value.HwbColor;
import com.sass_lang.embedded_protocol.Value.RgbColor;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ColorValidatorTest {

    @Test
    void assertValid_rgb() {
        RgbColor rgbColor = RgbColor.newBuilder().build();

        ColorValidator.assertValid(rgbColor);

        assertThrows(IllegalArgumentException.class, () -> ColorValidator.assertValid((RgbColor) null));

        assertThrows(IllegalArgumentException.class, () -> ColorValidator.assertValid(rgbColor.toBuilder().setRed(500)));
        assertThrows(IllegalArgumentException.class, () -> ColorValidator.assertValid(rgbColor.toBuilder().setRed(-1)));
        assertThrows(IllegalArgumentException.class, () -> ColorValidator.assertValid(rgbColor.toBuilder().setGreen(500)));
        assertThrows(IllegalArgumentException.class, () -> ColorValidator.assertValid(rgbColor.toBuilder().setGreen(-1)));
        assertThrows(IllegalArgumentException.class, () -> ColorValidator.assertValid(rgbColor.toBuilder().setBlue(500)));
        assertThrows(IllegalArgumentException.class, () -> ColorValidator.assertValid(rgbColor.toBuilder().setBlue(-1)));
        assertThrows(IllegalArgumentException.class, () -> ColorValidator.assertValid(rgbColor.toBuilder().setAlpha(2.3d)));
        assertThrows(IllegalArgumentException.class, () -> ColorValidator.assertValid(rgbColor.toBuilder().setAlpha(-2.3d)));
    }

    @Test
    void assertValid_hsl() {
        HslColor hslColor = HslColor.newBuilder().build();

        ColorValidator.assertValid(hslColor);

        assertThrows(IllegalArgumentException.class, () -> ColorValidator.assertValid((HslColor) null));

        assertThrows(IllegalArgumentException.class, () -> ColorValidator.assertValid(hslColor.toBuilder().setLightness(101)));
        assertThrows(IllegalArgumentException.class, () -> ColorValidator.assertValid(hslColor.toBuilder().setLightness(-1)));
        assertThrows(IllegalArgumentException.class, () -> ColorValidator.assertValid(hslColor.toBuilder().setSaturation(101)));
        assertThrows(IllegalArgumentException.class, () -> ColorValidator.assertValid(hslColor.toBuilder().setSaturation(-1)));
        assertThrows(IllegalArgumentException.class, () -> ColorValidator.assertValid(hslColor.toBuilder().setAlpha(2.3d)));
        assertThrows(IllegalArgumentException.class, () -> ColorValidator.assertValid(hslColor.toBuilder().setAlpha(-2.3d)));
    }

    @Test
    void assertValid_hwb() {
        HwbColor hwbColor = HwbColor.newBuilder().build();

        ColorValidator.assertValid(hwbColor);

        assertThrows(IllegalArgumentException.class, () -> ColorValidator.assertValid((HwbColor) null));

        assertThrows(IllegalArgumentException.class, () -> ColorValidator.assertValid(hwbColor.toBuilder().setWhiteness(101)));
        assertThrows(IllegalArgumentException.class, () -> ColorValidator.assertValid(hwbColor.toBuilder().setWhiteness(-1)));
        assertThrows(IllegalArgumentException.class, () -> ColorValidator.assertValid(hwbColor.toBuilder().setBlackness(101)));
        assertThrows(IllegalArgumentException.class, () -> ColorValidator.assertValid(hwbColor.toBuilder().setBlackness(-1)));
        assertThrows(IllegalArgumentException.class, () -> ColorValidator.assertValid(hwbColor.toBuilder().setWhiteness(51).setBlackness(51)));
        assertThrows(IllegalArgumentException.class, () -> ColorValidator.assertValid(hwbColor.toBuilder().setAlpha(2.3d)));
        assertThrows(IllegalArgumentException.class, () -> ColorValidator.assertValid(hwbColor.toBuilder().setAlpha(-2.3d)));
    }
}