package de.larsgrefer.sass.embedded.util;


import lombok.NonNull;
import lombok.experimental.UtilityClass;
import sass.embedded_protocol.EmbeddedSass.Value.HslColorOrBuilder;
import sass.embedded_protocol.EmbeddedSass.Value.HwbColorOrBuilder;
import sass.embedded_protocol.EmbeddedSass.Value.RgbColorOrBuilder;

/**
 * @author Lars Grefer
 */

@UtilityClass
class ColorValidator {

    static void assertValid(@NonNull RgbColorOrBuilder rgbColor) {
        int red = rgbColor.getRed();
        if (red < 0 || red > 255) {
            throw new IllegalArgumentException("Red must be between 0 and 255.");
        }

        int green = rgbColor.getGreen();
        if (green < 0 || green > 255) {
            throw new IllegalArgumentException("Green must be between 0 and 255.");
        }

        int blue = rgbColor.getBlue();
        if (blue < 0 || blue > 255) {
            throw new IllegalArgumentException("Blue must be between 0 and 255.");
        }

        double alpha = rgbColor.getAlpha();
        if (alpha < 0 || alpha > 1) {
            throw new IllegalArgumentException("Alpha must be between 0 and 1.");
        }
    }

    static void assertValid(@NonNull HslColorOrBuilder hslColor) {
        double saturation = hslColor.getSaturation();
        if (saturation < 0 || saturation > 100) {
            throw new IllegalArgumentException("Saturation must be between 0 and 100.");
        }

        double lightness = hslColor.getLightness();
        if (lightness < 0 || lightness > 100) {
            throw new IllegalArgumentException("Lightness must be between 0 and 100.");
        }

        double alpha = hslColor.getAlpha();
        if (alpha < 0 || alpha > 1) {
            throw new IllegalArgumentException("Alpha must be between 0 and 1.");
        }
    }

    static void assertValid(@NonNull HwbColorOrBuilder hwbColor) {
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
}
