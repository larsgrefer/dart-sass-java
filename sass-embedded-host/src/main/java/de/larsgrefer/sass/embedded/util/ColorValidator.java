package de.larsgrefer.sass.embedded.util;


import com.sass_lang.embedded_protocol.Value;
import com.sass_lang.embedded_protocol.Value.ColorOrBuilder;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

/**
 * @author Lars Grefer
 */

@UtilityClass
class ColorValidator {

    static void assertValid(@NonNull ColorOrBuilder color) {
        if ("rgb".equals(color.getSpace())) {
            assertValidRgb(color);
        } else if ("hsl".equals(color.getSpace())) {
            assertValidHsl(color);
        } else if ("hwb".equals(color.getSpace())) {
            assertValidHwb(color);
        } else {
            throw new IllegalArgumentException("Unsupported color space: " + color.getSpace());
        }
    }

    static void assertValidRgb(@NonNull ColorOrBuilder rgbColor) {
        int red = (int) rgbColor.getChannel1();
        if (red < 0 || red > 255) {
            throw new IllegalArgumentException("Red must be between 0 and 255.");
        }

        int green = (int) rgbColor.getChannel2();
        if (green < 0 || green > 255) {
            throw new IllegalArgumentException("Green must be between 0 and 255.");
        }

        int blue = (int) rgbColor.getChannel3();
        if (blue < 0 || blue > 255) {
            throw new IllegalArgumentException("Blue must be between 0 and 255.");
        }

        double alpha = rgbColor.getAlpha();
        if (alpha < 0 || alpha > 1) {
            throw new IllegalArgumentException("Alpha must be between 0 and 1.");
        }
    }

    static void assertValidHsl(@NonNull ColorOrBuilder hslColor) {
        double saturation = hslColor.getChannel2();
        if (saturation < 0 || saturation > 100) {
            throw new IllegalArgumentException("Saturation must be between 0 and 100.");
        }

        double lightness = hslColor.getChannel3();
        if (lightness < 0 || lightness > 100) {
            throw new IllegalArgumentException("Lightness must be between 0 and 100.");
        }

        double alpha = hslColor.getAlpha();
        if (alpha < 0 || alpha > 1) {
            throw new IllegalArgumentException("Alpha must be between 0 and 1.");
        }
    }

    static void assertValidHwb(@NonNull ColorOrBuilder hwbColor) {
        double whiteness = hwbColor.getChannel2();
        if (whiteness < 0 || whiteness > 100) {
            throw new IllegalArgumentException("Whiteness must be between 0 and 100.");
        }

        double blackness = hwbColor.getChannel3();
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
