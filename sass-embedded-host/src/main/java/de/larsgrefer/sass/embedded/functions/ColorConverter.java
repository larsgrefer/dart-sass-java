package de.larsgrefer.sass.embedded.functions;

import lombok.experimental.UtilityClass;
import lombok.var;
import sass.embedded_protocol.EmbeddedSass;
import sass.embedded_protocol.EmbeddedSass.Value.*;

import javax.annotation.Nonnull;
import java.awt.*;

/**
 * @author Lars Grefer
 */
@UtilityClass
@Nonnull
public class ColorConverter {

    /**
     * @see <a href="https://www.w3.org/TR/css-color-4/#hwb-to-rgb">https://www.w3.org/TR/css-color-4/#hwb-to-rgb</a>
     */
    public static Color toJavaColor(HwbColorOrBuilder hwbColor) {
        validate(hwbColor);

        float white = (float) (hwbColor.getWhiteness() / 100f);
        float black = (float) (hwbColor.getBlackness() / 100f);

        double[] rgb = hslToRgb(hwbColor.getHue(), 1, .5);

        for (int i = 0; i < 3; i++) {
            rgb[i] *= (1 - white - black);
            rgb[i] += white;
        }

        return new Color((float) rgb[0], (float) rgb[1], (float) rgb[2], (float) hwbColor.getAlpha());
    }

    public static Color toJavaColor(HslColorOrBuilder hslColor) {
        double[] rgb = hslToRgb(hslColor.getHue(), hslColor.getSaturation(), hslColor.getLightness());
        return new Color((float) rgb[0], (float) rgb[1], (float) rgb[2], (int) (hslColor.getAlpha() * 255));
    }

    public static Color toJavaColor(RgbColorOrBuilder rgbColor) {
        return new Color(rgbColor.getRed(), rgbColor.getGreen(), rgbColor.getBlue(), (int) (rgbColor.getAlpha() * 255));
    }

    public static RgbColor toRgbColor(HwbColorOrBuilder hwbColor) {
        validate(hwbColor);
        return toRgbColor(toJavaColor(hwbColor));
    }

    public static RgbColor toRgbColor(HslColorOrBuilder hslColor) {
        return toRgbColor(toJavaColor(hslColor));
    }

    public static RgbColor toRgbColor(Color color) {
        return RgbColor.newBuilder()
                .setRed(color.getRed())
                .setGreen(color.getGreen())
                .setBlue(color.getBlue())
                .setAlpha(color.getAlpha() / 255d)
                .build();
    }

    public static HwbColor toHwbColor(HslColorOrBuilder hslColor) {
        return toHwbColor(toRgbColor(hslColor));
    }

    public static HwbColor toHwbColor(RgbColorOrBuilder rgbColor) {
        double[] hwb = rgbToHwb(rgbColor.getRed(), rgbColor.getGreen(), rgbColor.getBlue());

        return HwbColor.newBuilder()
                .setHue(hwb[0])
                .setWhiteness(hwb[1])
                .setBlackness(hwb[2])
                .setAlpha(rgbColor.getAlpha())
                .build();
    }

    private void validate(HwbColorOrBuilder hwbColor) {
        double whiteness = hwbColor.getWhiteness();
        if (whiteness < 0 || whiteness > 100) {
            throw new IllegalArgumentException("Whiteness must be between 0 and 100.");
        }

        double blackness = hwbColor.getBlackness();
        if (blackness < 0 || blackness > 100) {
            throw new IllegalArgumentException("Whiteness must be between 0 and 100.");
        }

        if (whiteness + blackness > 100) {
            throw new IllegalArgumentException("The sum of `whiteness` and `blackness` must not exceed 100.");
        }

        double alpha = hwbColor.getAlpha();
        if (alpha < 0 || alpha > 1) {
            throw new IllegalArgumentException("Alpha must be between 0 and 1.");
        }
    }

    /**
     * https://www.w3.org/TR/css-color-3/#hsl-color
     */
    private double[] hslToRgb(double hue, double sat, double light) {

        hue = hue * 6d;

        double t2;
        if (light <= .5) {
            t2 = light * (sat + 1);
        }
        else {
            t2 = light + sat - (light * sat);
        }
        double t1 = light * 2 - t2;
        double r = hueToRgb(t1, t2, hue + 2);
        double g = hueToRgb(t1, t2, hue);
        double b = hueToRgb(t1, t2, hue - 2);
        return new double[]{r, g, b};
    }

    /**
     * https://www.w3.org/TR/css-color-3/#hsl-color
     */
    private double hueToRgb(double t1, double t2, double hue) {
        if (hue < 0) hue += 6;
        if (hue >= 6) hue -= 6;

        if (hue < 1) return (t2 - t1) * hue + t1;
        else if (hue < 3) return t2;
        else if (hue < 4) return (t2 - t1) * (4 - hue) + t1;
        else return t1;
    }

    private double[] rgbToHwb(int red, int green, int blue) {
        float[] hsl = Color.RGBtoHSB(red, green, blue, null);
        double white = min(red, green, blue) / 255d;
        double black = 1 - (max(red, green, blue) / 255d);
        return new double[]{hsl[0], white * 100, black * 100};
    }

    private double min(double... vals) {
        double min = vals[0];
        for (double val : vals) {
            min = Math.min(min, val);
        }
        return min;
    }

    private double max(double... vals) {
        double max = vals[0];
        for (double val : vals) {
            max = Math.max(max, val);
        }
        return max;
    }
}
