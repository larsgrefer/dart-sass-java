package de.larsgrefer.sass.embedded.util;

import com.sass_lang.embedded_protocol.Value.*;
import lombok.experimental.UtilityClass;

import javax.annotation.Nonnull;

import static de.larsgrefer.sass.embedded.util.ColorValidator.assertValid;

/**
 * @author Lars Grefer
 * @see de.larsgrefer.sass.embedded.functions.SassColor
 */
@UtilityClass
@Nonnull
public class ColorUtil {

    public static final Color white = rgba(255, 255, 255, 1);
    public static final Color black = rgba(0, 0, 0, 1);

    public static Color toRgbColor(Color color) {
        if ("rgb".equals(color.getSpace())) {
            return color;
        }

        if ("hwb".equals(color.getSpace())) {
            return toRgbColorFromHwb(color);
        }

        if ("hsl".equals(color.getSpace())) {
            return toRgbColorFromHsl(color);
        }

        throw new IllegalArgumentException("Unsupported color space: " + color.getSpace());
    }

    public static Color toRgbColorFromHwb(ColorOrBuilder hwbColor) {
        if (!"hwb".equals(hwbColor.getSpace())) {
            throw new IllegalArgumentException("HWB color should be hwb");
        }
        assertValid(hwbColor);

        double[] rgb = CssColorSpecUtil.hwbToRgb(hwbColor.getChannel1(), hwbColor.getChannel2(), hwbColor.getChannel3());

        return Color.newBuilder()
                .setSpace("rgb")
                .setChannel1((int) Math.round(rgb[0] * 255d))
                .setChannel2((int) Math.round(rgb[1] * 255d))
                .setChannel3((int) Math.round(rgb[2] * 255d))
                .setAlpha(hwbColor.getAlpha())
                .build();
    }

    public static Color toRgbColorFromHsl(ColorOrBuilder hslColor) {
        if (!"hsl".equals(hslColor.getSpace())) {
            throw new IllegalArgumentException("HSL color should be hsl");
        }

        assertValid(hslColor);

        double[] rgb = CssColorSpecUtil.hslToRgb((int) hslColor.getChannel1(), hslColor.getChannel2(), hslColor.getChannel3());

        return Color.newBuilder()
                .setSpace("rgb")
                .setChannel1((int) Math.round(rgb[0] * 255d))
                .setChannel2((int) Math.round(rgb[1] * 255d))
                .setChannel3((int) Math.round(rgb[2] * 255d))
                .setAlpha(hslColor.getAlpha())
                .build();
    }

    public static Color toHslColor(Color color) {
        if ("hsl".equals(color.getSpace())) {
            return color;
        }

        if ("hwb".equals(color.getSpace())) {
            color = toRgbColor(color);
        }

        if (!"rgb".equals(color.getSpace())) {
            throw new IllegalArgumentException("color space is not supported: " + color.getSpace());
        }

        assertValid(color);

        double red = color.getChannel1() / 255d;
        double green = color.getChannel2() / 255d;
        double blue = color.getChannel3() / 255d;

        double[] hsl = CssColorSpecUtil.rgbToHsl(red, green, blue);

        return Color.newBuilder()
                .setSpace("hsl")
                .setChannel1(hsl[0])
                .setChannel2(hsl[1])
                .setChannel3(hsl[2])
                .setAlpha(color.getAlpha())
                .build();
    }

    public static Color toHwbColor(Color color) {
        if ("hwb".equals(color.getSpace())) {
            return color;
        }

        if ("hsl".equals(color.getSpace())) {
            color = toRgbColorFromHsl(color);
        }

        if (!"rgb".equals(color.getSpace())) {
            throw new IllegalArgumentException("color space is not supported: " + color.getSpace());
        }

        assertValid(color);

        double red = color.getChannel1() / 255d;
        double green = color.getChannel2() / 255d;
        double blue = color.getChannel3() / 255d;

        double[] hwb = CssColorSpecUtil.rgbToHwb(red, green, blue);

        return Color.newBuilder()
                .setSpace("hwb")
                .setChannel1(hwb[0])
                .setChannel2(hwb[1])
                .setChannel3(hwb[2])
                .setAlpha(color.getAlpha())
                .build();
    }

    public Color rgb(int rgb) {
        return rgba(rgb, 1);
    }

    @SuppressWarnings("PointlessBitwiseExpression")
    public Color rgba(int rgb, double alpha) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = (rgb >> 0) & 0xFF;

        return rgba(r, g, b, alpha);
    }

    public Color rgba(int red, int green, int blue, double alpha) {
        return Color.newBuilder()
                .setSpace("rgb")
                .setChannel1(red)
                .setChannel2(green)
                .setChannel3(blue)
                .setAlpha(alpha)
                .build();
    }

}
