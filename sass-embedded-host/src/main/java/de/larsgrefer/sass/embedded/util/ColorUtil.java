package de.larsgrefer.sass.embedded.util;

import lombok.experimental.UtilityClass;
import sass.embedded_protocol.EmbeddedSass.Value.*;

import javax.annotation.Nonnull;

import static de.larsgrefer.sass.embedded.util.ColorValidator.assertValid;

/**
 * @author Lars Grefer
 */
@UtilityClass
@Nonnull
public class ColorUtil {

    public static RgbColor toRgbColor(HwbColorOrBuilder hwbColor) {
        assertValid(hwbColor);

        double[] rgb = CssColorSpecUtil.hwbToRgb(hwbColor.getHue(), hwbColor.getWhiteness(), hwbColor.getBlackness());

        return RgbColor.newBuilder()
                .setRed((int) Math.round(rgb[0] * 255d))
                .setGreen((int) Math.round(rgb[1] * 255d))
                .setBlue((int) Math.round(rgb[2] * 255d))
                .setAlpha(hwbColor.getAlpha())
                .build();
    }

    public static RgbColor toRgbColor(HslColorOrBuilder hslColor) {
        assertValid(hslColor);

        double[] rgb = CssColorSpecUtil.hslToRgb((int) hslColor.getHue(), hslColor.getSaturation(), hslColor.getLightness());

        return RgbColor.newBuilder()
                .setRed((int) Math.round(rgb[0] * 255d))
                .setGreen((int) Math.round(rgb[1] * 255d))
                .setBlue((int) Math.round(rgb[2] * 255d))
                .setAlpha(hslColor.getAlpha())
                .build();
    }

    public static HslColor toHslColor(HwbColorOrBuilder hwbColor) {
        assertValid(hwbColor);
        return toHslColor(toRgbColor(hwbColor));
    }

    public static HslColor toHslColor(RgbColorOrBuilder rgbColor) {
        assertValid(rgbColor);

        double red = rgbColor.getRed() / 255d;
        double green = rgbColor.getGreen() / 255d;
        double blue = rgbColor.getBlue() / 255d;

        double[] hsl = CssColorSpecUtil.rgbToHsl(red, green, blue);

        return HslColor.newBuilder()
                .setHue(hsl[0])
                .setSaturation(hsl[1])
                .setLightness(hsl[2])
                .setAlpha(rgbColor.getAlpha())
                .build();
    }

    public static HwbColor toHwbColor(HslColorOrBuilder hslColor) {
        assertValid(hslColor);
        return toHwbColor(toRgbColor(hslColor));
    }

    public static HwbColor toHwbColor(RgbColorOrBuilder rgbColor) {
        assertValid(rgbColor);

        double red = rgbColor.getRed() / 255d;
        double green = rgbColor.getGreen() / 255d;
        double blue = rgbColor.getBlue() / 255d;

        double[] hwb = CssColorSpecUtil.rgbToHwb(red, green, blue);

        return HwbColor.newBuilder()
                .setHue(hwb[0])
                .setWhiteness(hwb[1])
                .setBlackness(hwb[2])
                .setAlpha(rgbColor.getAlpha())
                .build();
    }

}
