package de.larsgrefer.sass.embedded.functions;

import com.sass_lang.embedded_protocol.Value.String;
import com.sass_lang.embedded_protocol.Value.*;
import de.larsgrefer.sass.embedded.util.ColorUtil;
import lombok.experimental.UtilityClass;

import java.util.Locale;

import static de.larsgrefer.sass.embedded.util.ColorUtil.*;


/**
 * Java implementation of the {@code sass:color} Module
 *
 * @author Lars Grefer
 * @see ColorUtil
 * @see <a href="https://sass-lang.com/documentation/modules/color">sass:color</a>
 */
@UtilityClass
public class SassColor {

    /**
     * Increases or decreases color‘s hue.
     * <p>
     * The hue must be a number between -360deg and 360deg (inclusive) to add to $color’s hue.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#adjust-hue">adjust-hue</a>
     */
    public static Color adjustHue(Color color, double hue) {

        if ("hsl".equals(color.getSpace()) || "hwb".equals(color.getSpace())) {
            double newHue = color.getChannel1() + hue;
            return color.toBuilder().setChannel1(normalizeHue(newHue)).build();
        }

        return adjustHue(toHslColor(color), hue);
    }

    /**
     * Returns the alpha channel of $color as a number between 0 and 1.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#alpha">alpha</a>
     */
    public static double alpha(ColorOrBuilder color) {
        return color.getAlpha();
    }

    /**
     * Returns the HWB blackness of $color as a number between 0% and 100%.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#blackness">blackness</a>
     */
    public static double blackness(Color color) {
        return toHwbColor(color).getChannel3();
    }

    /**
     * Returns the blue channel of $color as a number between 0 and 255.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#blue">blue</a>
     */
    public static int blue(Color color) {
        return (int) toRgbColor(color).getChannel3();
    }

    /**
     * Returns the RGB complement of $color.
     * <p>
     * This is identical to color.adjust($color, $hue: 180deg).
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#complement">complement</a>
     */
    public static Color complement(Color color) {
        return adjustHue(color, 180);
    }

    /**
     * Makes $color darker.
     * <p>
     * The $amount must be a number between 0% and 100% (inclusive).
     * Decreases the HSL lightness of $color by that amount.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#darken">darken</a>
     */
    public static Color darken(Color color, double amount) {
        color = toHslColor(color);

        double newLightness = color.getChannel3() - amount;
        return color.toBuilder()
                .setChannel3(normalize100(newLightness))
                .build();
    }

    /**
     * Makes $color less saturated.
     * <p>
     * The $amount must be a number between 0% and 100% (inclusive).
     * Decreases the HSL saturation of $color by that amount.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#desaturate">desaturate</a>
     */
    public static Color desaturate(Color color, double amount) {
        color = toHslColor(color);

        double newSaturation = color.getChannel2() - amount;
        return color.toBuilder()
                .setChannel2(normalize100(newSaturation))
                .build();
    }

    /**
     * Returns a gray color with the same lightness as $color.
     * <p>
     * This is identical to color.change($color, $saturation: 0%).
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#grayscale">grayscale</a>
     */
    public static Color grayscale(Color color) {
        color = toHslColor(color);
        return color.toBuilder().setChannel2(0d).build();
    }

    /**
     * Returns the green channel of $color as a number between 0 and 255.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#green">green</a>
     */
    public static int green(Color color) {
        return (int) toRgbColor(color).getChannel2();
    }

    /**
     * Returns the hue of $color as a number between 0deg and 360deg.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#hue">hue</a>
     */
    public double hue(Color color) {
        if (color.getSpace().equals("hsl") || color.getSpace().equals("hwb")) {
            return color.getChannel1();
        }
        return toHslColor(color).getChannel1();
    }

    /**
     * Returns a color with the given hue, whiteness, and blackness and the given alpha channel.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#hwb">hwb</a>
     */
    public Color hwb(double hue, double whiteness, double blackness) {
        return hwb(hue, whiteness, blackness, 1d);
    }

    /**
     * Returns a color with the given hue, whiteness, and blackness and the given alpha channel.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#hwb">hwb</a>
     */
    public Color hwb(double hue, double whiteness, double blackness, double alpha) {
        return Color.newBuilder()
                .setSpace("hwb")
                .setChannel1(hue)
                .setChannel2(whiteness)
                .setChannel3(blackness)
                .setAlpha(alpha)
                .build();
    }

    /**
     * Returns an unquoted string that represents $color in the #AARRGGBB format expected by Internet Explorer’s -ms-filter property.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#ie-hex-str">ie-hex-str</a>
     */
    public String ieHexStr(Color color) {

        Color rgbColor = toRgbColor(color);

        int a = (int) Math.round(rgbColor.getAlpha() * 255d);

        int red = (int) rgbColor.getChannel1();
        int green = (int) rgbColor.getChannel2();
        int blue = (int) rgbColor.getChannel3();

        int value = ((a & 0xFF) << 24) |
                ((red & 0xFF) << 16) |
                ((green & 0xFF) << 8) |
                ((blue & 0xFF) << 0);

        return String.newBuilder()
                .setText("#" + Integer.toHexString(value).toUpperCase(Locale.ROOT))
                .setQuoted(false)
                .build();
    }

    /**
     * Returns the inverse or negative of $color.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#invert">invert</a>
     */
    public Color invert(Color color) {
        return invert(color, 1);
    }

    /**
     * Returns the inverse or negative of $color.
     * <p>
     * The $weight must be a number between 0% and 100% (inclusive).
     * A higher weight means the result will be closer to the negative, and a lower weight means it will be closer to $color.
     * Weight 50% will always produce #808080.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#invert">invert</a>
     */
    public Color invert(Color color, double weight) {
        if (weight < 0 || weight > 1) {
            throw new IllegalArgumentException("weight must be between 0 and 1");
        }

        color = toRgbColor(color);

        Color inverse = color.toBuilder()
                .setChannel1(255 - color.getChannel1())
                .setChannel2(255 - color.getChannel2())
                .setChannel3(255 - color.getChannel3())
                .build();

        return mix(inverse, color, weight);
    }

    /**
     * Makes $color lighter.
     * <p>
     * The $amount must be a number between 0% and 100% (inclusive). Increases the HSL lightness of $color by that amount.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#lighten">lighten</a>
     */
    public Color lighten(Color color, double amount) {
        if (amount < 0 || amount > 100) throw new IllegalArgumentException("amount");

        color = toHslColor(color);

        return color.toBuilder()
                .setChannel3(normalize100(color.getChannel3() + amount))
                .build();
    }

    /**
     * Returns the HSL lightness of $color as a number between 0% and 100%.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#lightness">lightness</a>
     */
    public double lightness(Color color) {
        return toHslColor(color).getChannel3();
    }

    /**
     * Returns a color that’s a mixture of $color1 and $color2.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#mix">mix</a>
     */
    public Color mix(Color color1, Color color2) {
        return mix(color1, color2, 0.5);
    }

    /**
     * Returns a color that’s a mixture of $color1 and $color2.
     * <p>
     * Both the $weight and the relative opacity of each color determines how much of each color is in the result.
     * The $weight must be a number between 0% and 100% (inclusive).
     * A larger weight indicates that more of $color1 should be used, and a smaller weight indicates that more of $color2 should be used.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#mix">mix</a>
     */
    public Color mix(Color color1, Color color2, double weight) {
        if (weight <= 0) {
            return color2;
        } else if (weight > 1) {
            return color1;
        }

        double normalizedWeight = weight * 2 - 1;
        double alphaDistance = color1.getAlpha() - color2.getAlpha();

        double combinedWeight1 = normalizedWeight * alphaDistance == -1
                ? normalizedWeight
                : (normalizedWeight + alphaDistance) /
                (1 + normalizedWeight * alphaDistance);
        double weight1 = (combinedWeight1 + 1) / 2;
        double weight2 = 1 - weight1;

        int r = (int) Math.round(red(color1) * weight1 + red(color2) * weight2);
        int g = (int) Math.round(green(color1) * weight1 + green(color2) * weight2);
        int b = (int) Math.round(blue(color1) * weight1 + blue(color2) * weight2);
        double a = color1.getAlpha() * weight + color2.getAlpha() * (1 - weight);

        return Color.newBuilder()
                .setSpace("rgb")
                .setChannel1(r)
                .setChannel2(g)
                .setChannel3(b)
                .setAlpha(a)
                .build();
    }

    /**
     * Makes $color more opaque.
     * <p>
     * The $amount must be a number between 0 and 1 (inclusive). Increases the alpha channel of $color by that amount.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#opacify">opacify</a>
     */
    public Color opacify(Color color, double amount) {
        if (amount < 0 || amount > 1) throw new IllegalArgumentException("amount");
        return color.toBuilder()
                .setAlpha(normalize1(color.getAlpha() + amount))
                .build();
    }

    /**
     * Returns the red channel of $color as a number between 0 and 255.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#red">red</a>
     */
    public int red(Color color) {
        return (int) toRgbColor(color).getChannel1();
    }

    /**
     * Makes $color more saturated.
     * <p>
     * The $amount must be a number between 0% and 100% (inclusive). Increases the HSL saturation of $color by that amount.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#saturate">saturate</a>
     */
    public Color saturate(Color color, double amount) {

        color = toHslColor(color);

        if (amount < 0 || amount > 100) throw new IllegalArgumentException("amount");
        return color.toBuilder()
                .setChannel2(normalize100(color.getChannel2() + amount))
                .build();
    }

    /**
     * Returns the HSL saturation of $color as a number between 0% and 100%.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#saturation">saturation</a>
     */
    public double saturation(Color color) {
        return toHslColor(color).getChannel2();
    }

    /**
     * Makes $color more transparent.
     * <p>
     * The $amount must be a number between 0 and 1 (inclusive). Decreases the alpha channel of $color by that amount.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#transparentize">transparentize</a>
     */
    public Color transparentize(Color color, double amount) {
        if (amount < 0 || amount > 1) throw new IllegalArgumentException("amount");
        return color.toBuilder()
                .setAlpha(normalize1(color.getAlpha() - amount))
                .build();
    }

    /**
     * Returns the HWB whiteness of $color as a number between 0% and 100%.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#whiteness">whiteness</a>
     */
    public double whiteness(Color color) {
        return toHwbColor(color).getChannel2();
    }

    static double normalizeHue(double hue) {
        hue = hue % 360;

        if (hue < 0) {
            hue += 360;
        }

        return hue;
    }

    static double normalize1(double number) {
        if (number < 0) return 0;
        if (number > 1) return 1;
        return number;
    }

    static double normalize100(double number) {
        if (number < 0) return 0;
        if (number > 100) return 100;
        return number;
    }
}
