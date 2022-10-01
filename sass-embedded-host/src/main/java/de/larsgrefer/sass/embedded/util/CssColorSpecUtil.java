package de.larsgrefer.sass.embedded.util;

import lombok.experimental.UtilityClass;

import java.util.function.Function;

/**
 * This class contains the color-conversion algorithms found in the CSS Color Specification.
 *
 * @author Lars Grefer
 * @see <a href="https://www.w3.org/TR/css-color-4">CSS Color Module Level 4</a>
 */
@UtilityClass
public class CssColorSpecUtil {

    /**
     * Converts the given HSL color to RGB.
     *
     * @param hue   Hue as degrees: 0..360
     * @param sat   Saturation as percentage: 0..100
     * @param light Lightness as percentage: 0..100
     * @return Array of RGB values: 0..1
     * @see <a href="https://www.w3.org/TR/css-color-4/#hsl-to-rgb">8.1. Converting HSL Colors to sRGB</a>
     */
    public static double[] hslToRgb(double hue, double sat, double light) {
        hue = hue % 360;

        if (hue < 0) {
            hue += 360;
        }

        final double normalizedHue = hue;
        final double scaledSaturation = sat / 100d;
        final double scaledLightness = light / 100d;

        Function<Integer, Double> f = (n) -> {
            double k = (n + normalizedHue / 30d) % 12;
            double a = scaledSaturation * Math.min(scaledLightness, 1 - scaledLightness);
            return scaledLightness - a * Math.max(-1, min(k - 3, 9 - k, 1));
        };

        return new double[]{f.apply(0), f.apply(8), f.apply(4)};
    }

    /**
     * Converts the given RGB color to HSL.
     *
     * @param red   Red component of the color, normalized to 0..1.
     * @param green Green component of the color, normalized to 0..1.
     * @param blue  Blue component of the color, normalized to 0..1.
     * @return Array of HSL values. Hue as degrees (0..360), Saturation and Lightness as percentages (0..100)
     * @see <a href="https://www.w3.org/TR/css-color-4/#rgb-to-hsl">8.2. Converting sRGB Colors to HSL</a>
     */
    public static double[] rgbToHsl(double red, double green, double blue) {
        double max = max(red, green, blue);
        double min = min(red, green, blue);
        double hue = 0;
        double sat = 0;
        double light = (min + max) / 2;
        double d = max - min;

        if (d != 0) {
            sat = (light == 0 || light == 1)
                    ? 0
                    : (max - light) / Math.min(light, 1 - light);

            if (max == red) {
                hue = (green - blue) / d + (green < blue ? 6 : 0);
            } else if (max == green) {
                hue = (blue - red) / d + 2;
            } else if (max == blue) {
                hue = (red - green) / d + 4;
            }

            hue = hue * 60;
        }

        return new double[]{hue, sat * 100, light * 100};
    }

    /**
     * Converts the given HWB color to RGB.
     *
     * @param hue   Hue as degrees: 0..360
     * @param white Whiteness as percentage: 0..100
     * @param black Blackness as percentage: 0..100
     * @return Array of RGB values: 0..1
     * @see <a href="https://www.w3.org/TR/css-color-4/#hwb-to-rgb">9.1. Converting HWB Colors to sRGB</a>
     */
    public static double[] hwbToRgb(double hue, double white, double black) {
        white /= 100;
        black /= 100;
        if (white + black >= 1) {
            double gray = white / (white + black);
            return new double[]{gray, gray, gray};
        }
        double[] rgb = hslToRgb(hue, 100, 50);
        for (int i = 0; i < 3; i++) {
            rgb[i] *= (1 - white - black);
            rgb[i] += white;
        }
        return rgb;
    }

    /**
     * Converts the given HWB color to RGB.
     *
     * @param red   Red component of the color, normalized to 0..1.
     * @param green Green component of the color, normalized to 0..1.
     * @param blue  Blue component of the color, normalized to 0..1.
     * @return Array of HWB values. Hue as degrees 0..360, Whiteness and Blackness as percentages (0..100).
     * @see <a href="https://www.w3.org/TR/css-color-4/#rgb-to-hwb">9.2. Converting sRGB Colors to HWB</a>
     */
    public static double[] rgbToHwb(double red, double green, double blue) {
        double[] hsl = rgbToHsl(red, green, blue);
        double white = min(red, green, blue);
        double black = 1 - max(red, green, blue);
        return new double[]{hsl[0], white * 100, black * 100};
    }

    private static double max(double d1, double d2, double d3) {
        return Math.max(Math.max(d1, d2), d3);
    }

    private static double min(double d1, double d2, double d3) {
        return Math.min(Math.min(d1, d2), d3);
    }
}
