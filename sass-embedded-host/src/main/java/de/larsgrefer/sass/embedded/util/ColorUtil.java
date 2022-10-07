package de.larsgrefer.sass.embedded.util;

import lombok.experimental.UtilityClass;
import sass.embedded_protocol.EmbeddedSass.Value.*;

import javax.annotation.Nonnull;
import java.awt.*;

import static de.larsgrefer.sass.embedded.util.ColorValidator.assertValid;

/**
 * @author Lars Grefer
 * @see de.larsgrefer.sass.embedded.functions.SassColor
 */
@UtilityClass
@Nonnull
public class ColorUtil {

    public static final RgbColor white = rgba(255, 255, 255, 1);
    public static final RgbColor black = rgba(0, 0, 0, 1);

    /**
     * @see <a href="https://www.w3.org/TR/css-color-4/#hwb-to-rgb">https://www.w3.org/TR/css-color-4/#hwb-to-rgb</a>
     */
    @Deprecated
    public static Color toJavaColor(HwbColorOrBuilder hwbColor) {
        assertValid(hwbColor);
        return toJavaColor(toRgbColor(hwbColor));
    }

    @Deprecated
    public static Color toJavaColor(HslColorOrBuilder hslColor) {
        assertValid(hslColor);
        return toJavaColor(toRgbColor(hslColor));
    }

    @Deprecated
    public static Color toJavaColor(RgbColorOrBuilder rgbColor) {
        assertValid(rgbColor);
        return new Color(rgbColor.getRed(), rgbColor.getGreen(), rgbColor.getBlue(), (int) (rgbColor.getAlpha() * 255));
    }

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

    @Deprecated
    public static RgbColor toRgbColor(Color color) {
        return RgbColor.newBuilder()
                .setRed(color.getRed())
                .setGreen(color.getGreen())
                .setBlue(color.getBlue())
                .setAlpha(color.getAlpha() / 255d)
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

    public RgbColor rgb(int rgb) {
        return rgba(rgb, 1);
    }

    @SuppressWarnings("PointlessBitwiseExpression")
    public RgbColor rgba(int rgb, double alpha) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = (rgb >> 0) & 0xFF;

        return rgba(r, g, b, alpha);
    }

    public RgbColor rgba(int red, int green, int blue, double alpha) {
        return RgbColor.newBuilder()
                .setRed(red)
                .setGreen(green)
                .setBlue(blue)
                .setAlpha(alpha)
                .build();
    }

}
