package de.larsgrefer.sass.embedded.functions;

import de.larsgrefer.sass.embedded.util.ColorUtil;
import lombok.experimental.UtilityClass;
import sass.embedded_protocol.EmbeddedSass.Value.String;
import sass.embedded_protocol.EmbeddedSass.Value.*;

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
    public static RgbColor adjustHue(RgbColorOrBuilder color, double hue) {
        HslColor hslColor = ColorUtil.toHslColor(color);
        return toRgbColor(adjustHue(hslColor, hue));
    }

    /**
     * Increases or decreases color‘s hue.
     * <p>
     * The hue must be a number between -360deg and 360deg (inclusive) to add to $color’s hue.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#adjust-hue">adjust-hue</a>
     */
    public static HslColor adjustHue(HslColor color, double hue) {
        double newHue = color.getHue() + hue;
        return color.toBuilder().setHue(normalizeHue(newHue)).build();
    }

    /**
     * Increases or decreases color‘s hue.
     * <p>
     * The hue must be a number between -360deg and 360deg (inclusive) to add to $color’s hue.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#adjust-hue">adjust-hue</a>
     */
    public static HwbColor adjustHue(HwbColor color, double hue) {
        double newHue = color.getHue() + hue;
        return color.toBuilder().setHue(normalizeHue(newHue)).build();
    }

    /**
     * Returns the alpha channel of $color as a number between 0 and 1.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#alpha">alpha</a>
     */
    public static double alpha(RgbColorOrBuilder color) {
        return color.getAlpha();
    }

    /**
     * Returns the alpha channel of $color as a number between 0 and 1.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#alpha">alpha</a>
     */
    public static double alpha(HslColorOrBuilder color) {
        return color.getAlpha();
    }

    /**
     * Returns the alpha channel of $color as a number between 0 and 1.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#alpha">alpha</a>
     */
    public static double alpha(HwbColorOrBuilder color) {
        return color.getAlpha();
    }

    /**
     * Returns the HWB blackness of $color as a number between 0% and 100%.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#blackness">blackness</a>
     */
    public static double blackness(RgbColorOrBuilder color) {
        return blackness(toHwbColor(color));
    }

    /**
     * Returns the HWB blackness of $color as a number between 0% and 100%.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#blackness">blackness</a>
     */
    public static double blackness(HslColorOrBuilder color) {
        return blackness(toHwbColor(color));
    }

    /**
     * Returns the HWB blackness of $color as a number between 0% and 100%.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#blackness">blackness</a>
     */
    public static double blackness(HwbColorOrBuilder color) {
        return color.getBlackness();
    }

    /**
     * Returns the blue channel of $color as a number between 0 and 255.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#blue">blue</a>
     */
    public static int blue(RgbColorOrBuilder color) {
        return color.getBlue();
    }

    /**
     * Returns the blue channel of $color as a number between 0 and 255.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#blue">blue</a>
     */
    public static int blue(HslColorOrBuilder color) {
        return blue(toRgbColor(color));
    }

    /**
     * Returns the blue channel of $color as a number between 0 and 255.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#blue">blue</a>
     */
    public static int blue(HwbColorOrBuilder color) {
        return blue(toRgbColor(color));
    }

    /**
     * Returns the RGB complement of $color.
     * <p>
     * This is identical to color.adjust($color, $hue: 180deg).
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#complement">complement</a>
     */
    public static RgbColor complement(RgbColorOrBuilder color) {
        return adjustHue(color, 180);
    }

    /**
     * Returns the RGB complement of $color.
     * <p>
     * This is identical to color.adjust($color, $hue: 180deg).
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#complement">complement</a>
     */
    public static HslColor complement(HslColor color) {
        return adjustHue(color, 180);
    }

    /**
     * Returns the RGB complement of $color.
     * <p>
     * This is identical to color.adjust($color, $hue: 180deg).
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#complement">complement</a>
     */
    public static HwbColor complement(HwbColor color) {
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
    public static RgbColor darken(RgbColorOrBuilder color, double amount) {
        return toRgbColor(darken(toHslColor(color), amount));
    }

    /**
     * Makes $color darker.
     * <p>
     * The $amount must be a number between 0% and 100% (inclusive).
     * Decreases the HSL lightness of $color by that amount.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#darken">darken</a>
     */
    public static HslColor darken(HslColor color, double amount) {
        double newLightness = color.getLightness() - amount;
        return color.toBuilder()
                .setLightness(normalize100(newLightness))
                .build();
    }

    /**
     * Makes $color darker.
     * <p>
     * The $amount must be a number between 0% and 100% (inclusive).
     * Decreases the HSL lightness of $color by that amount.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#darken">darken</a>
     */
    public static HwbColor darken(HwbColorOrBuilder color, double amount) {
        return toHwbColor(darken(toHslColor(color), amount));
    }

    /**
     * Makes $color less saturated.
     * <p>
     * The $amount must be a number between 0% and 100% (inclusive).
     * Decreases the HSL saturation of $color by that amount.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#desaturate">desaturate</a>
     */
    public static RgbColor desaturate(RgbColorOrBuilder color, double amount) {
        return toRgbColor(desaturate(toHslColor(color), amount));
    }

    /**
     * Makes $color less saturated.
     * <p>
     * The $amount must be a number between 0% and 100% (inclusive).
     * Decreases the HSL saturation of $color by that amount.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#desaturate">desaturate</a>
     */
    public static HslColor desaturate(HslColor color, double amount) {
        double newSaturation = color.getSaturation() - amount;
        return color.toBuilder()
                .setSaturation(normalize100(newSaturation))
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
    public static HwbColor desaturate(HwbColorOrBuilder color, double amount) {
        return toHwbColor(desaturate(toHslColor(color), amount));
    }

    /**
     * Returns a gray color with the same lightness as $color.
     * <p>
     * This is identical to color.change($color, $saturation: 0%).
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#grayscale">grayscale</a>
     */
    public static RgbColor grayscale(RgbColorOrBuilder color) {
        return toRgbColor(grayscale(toHslColor(color)));
    }

    /**
     * Returns a gray color with the same lightness as $color.
     * <p>
     * This is identical to color.change($color, $saturation: 0%).
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#grayscale">grayscale</a>
     */
    public static HslColor grayscale(HslColor color) {
        return color.toBuilder().setSaturation(0d).build();
    }

    /**
     * Returns a gray color with the same lightness as $color.
     * <p>
     * This is identical to color.change($color, $saturation: 0%).
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#grayscale">grayscale</a>
     */
    public static HwbColor grayscale(HwbColorOrBuilder color) {
        return toHwbColor(grayscale(toHslColor(color)));
    }

    /**
     * Returns the green channel of $color as a number between 0 and 255.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#green">green</a>
     */
    public static int green(RgbColorOrBuilder color) {
        return color.getGreen();
    }

    /**
     * Returns the green channel of $color as a number between 0 and 255.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#green">green</a>
     */
    public static int green(HslColorOrBuilder color) {
        return green(toRgbColor(color));
    }

    /**
     * Returns the green channel of $color as a number between 0 and 255.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#green">green</a>
     */
    public static int green(HwbColorOrBuilder color) {
        return green(toRgbColor(color));
    }

    /**
     * Returns the hue of $color as a number between 0deg and 360deg.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#hue">hue</a>
     */
    public double hue(RgbColorOrBuilder color) {
        return hue(toHslColor(color));
    }

    /**
     * Returns the hue of $color as a number between 0deg and 360deg.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#hue">hue</a>
     */
    public double hue(HslColorOrBuilder color) {
        return color.getHue();
    }

    /**
     * Returns the hue of $color as a number between 0deg and 360deg.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#hue">hue</a>
     */
    public double hue(HwbColorOrBuilder color) {
        return color.getHue();
    }

    /**
     * Returns a color with the given hue, whiteness, and blackness and the given alpha channel.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#hwb">hwb</a>
     */
    public HwbColor hwb(double hue, double whiteness, double blackness) {
        return hwb(hue, whiteness, blackness, 1d);
    }

    /**
     * Returns a color with the given hue, whiteness, and blackness and the given alpha channel.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#hwb">hwb</a>
     */
    public HwbColor hwb(double hue, double whiteness, double blackness, double alpha) {
        return HwbColor.newBuilder()
                .setHue(hue)
                .setWhiteness(whiteness)
                .setBlackness(blackness)
                .setAlpha(alpha)
                .build();
    }

    /**
     * Returns an unquoted string that represents $color in the #AARRGGBB format expected by Internet Explorer’s -ms-filter property.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#ie-hex-str">ie-hex-str</a>
     */
    public String ieHexStr(RgbColor rgbColor) {
        int a = (int) Math.round(rgbColor.getAlpha() * 255d);

        int value = ((a & 0xFF) << 24) |
                ((rgbColor.getRed() & 0xFF) << 16) |
                ((rgbColor.getGreen() & 0xFF) << 8) |
                ((rgbColor.getBlue() & 0xFF) << 0);

        return String.newBuilder()
                .setText("#" + Integer.toHexString(value).toUpperCase(Locale.ROOT))
                .setQuoted(false)
                .build();
    }

    /**
     * Returns an unquoted string that represents $color in the #AARRGGBB format expected by Internet Explorer’s -ms-filter property.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#ie-hex-str">ie-hex-str</a>
     */
    public String ieHexStr(HslColor color) {
        return ieHexStr(toRgbColor(color));
    }

    /**
     * Returns an unquoted string that represents $color in the #AARRGGBB format expected by Internet Explorer’s -ms-filter property.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#ie-hex-str">ie-hex-str</a>
     */
    public String ieHexStr(HwbColor color) {
        return ieHexStr(toRgbColor(color));
    }

    /**
     * Returns the inverse or negative of $color.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#invert">invert</a>
     */
    public RgbColor invert(RgbColor color) {
        return invert(color, 1);
    }

    /**
     * Returns the inverse or negative of $color.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#invert">invert</a>
     */
    public HslColor invert(HslColor color) {
        return invert(color, 1);
    }

    /**
     * Returns the inverse or negative of $color.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#invert">invert</a>
     */
    public HwbColor invert(HwbColor color) {
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
    public RgbColor invert(RgbColor color, double weight) {
        if (weight < 0 || weight > 1) {
            throw new IllegalArgumentException("weight must be between 0 and 1");
        }

        RgbColor inverse = color.toBuilder()
                .setRed(255 - color.getRed())
                .setGreen(255 - color.getGreen())
                .setBlue(255 - color.getBlue())
                .build();

        return mix(inverse, color, weight);
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
    public HslColor invert(HslColor color, double weight) {
        return toHslColor(invert(toRgbColor(color), weight));
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
    public HwbColor invert(HwbColor color, double weight) {
        return toHwbColor(invert(toRgbColor(color), weight));
    }

    /**
     * Makes $color lighter.
     * <p>
     * The $amount must be a number between 0% and 100% (inclusive). Increases the HSL lightness of $color by that amount.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#lighten">lighten</a>
     */
    public RgbColor lighten(RgbColor color, double amount) {
        return toRgbColor(lighten(toHslColor(color), amount));
    }

    /**
     * Makes $color lighter.
     * <p>
     * The $amount must be a number between 0% and 100% (inclusive). Increases the HSL lightness of $color by that amount.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#lighten">lighten</a>
     */
    public HslColor lighten(HslColor color, double amount) {
        if (amount < 0 || amount > 100) throw new IllegalArgumentException("amount");
        return color.toBuilder()
                .setLightness(normalize100(color.getLightness() + amount))
                .build();
    }

    /**
     * Makes $color lighter.
     * <p>
     * The $amount must be a number between 0% and 100% (inclusive). Increases the HSL lightness of $color by that amount.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#lighten">lighten</a>
     */
    public HwbColor lighten(HwbColor color, double amount) {
        return toHwbColor(lighten(toHslColor(color), amount));
    }

    /**
     * Returns the HSL lightness of $color as a number between 0% and 100%.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#lightness">lightness</a>
     */
    public double lightness(RgbColor color) {
        return lightness(toHslColor(color));
    }

    /**
     * Returns the HSL lightness of $color as a number between 0% and 100%.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#lightness">lightness</a>
     */
    public double lightness(HslColor color) {
        return color.getLightness();
    }

    /**
     * Returns the HSL lightness of $color as a number between 0% and 100%.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#lightness">lightness</a>
     */
    public double lightness(HwbColor color) {
        return lightness(toHslColor(color));
    }

    /**
     * Returns a color that’s a mixture of $color1 and $color2.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#mix">mix</a>
     */
    public RgbColor mix(RgbColor color1, RgbColor color2) {
        return mix(color1, color2, 0.5);
    }

    /**
     * Returns a color that’s a mixture of $color1 and $color2.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#mix">mix</a>
     */
    public HslColor mix(HslColor color1, HslColor color2) {
        return mix(color1, color2, 0.5);
    }

    /**
     * Returns a color that’s a mixture of $color1 and $color2.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#mix">mix</a>
     */
    public HwbColor mix(HwbColor color1, HwbColor color2) {
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
    public RgbColor mix(RgbColor color1, RgbColor color2, double weight) {
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

        int r = (int) Math.round(color1.getRed() * weight1 + color2.getRed() * weight2);
        int g = (int) Math.round(color1.getGreen() * weight1 + color2.getGreen() * weight2);
        int b = (int) Math.round(color1.getBlue() * weight1 + color2.getBlue() * weight2);
        double a = color1.getAlpha() * weight + color2.getAlpha() * (1 - weight);

        return RgbColor.newBuilder()
                .setRed(r)
                .setGreen(g)
                .setBlue(b)
                .setAlpha(a)
                .build();
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
    public HslColor mix(HslColor color1, HslColor color2, double weight) {
        RgbColor mix = mix(
                toRgbColor(color1),
                toRgbColor(color2),
                weight
        );
        return toHslColor(mix);
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
    public HwbColor mix(HwbColor color1, HwbColor color2, double weight) {
        RgbColor mix = mix(
                toRgbColor(color1),
                toRgbColor(color2),
                weight
        );
        return toHwbColor(mix);
    }

    /**
     * Makes $color more opaque.
     * <p>
     * The $amount must be a number between 0 and 1 (inclusive). Increases the alpha channel of $color by that amount.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#opacify">opacify</a>
     */
    public RgbColor opacify(RgbColor color, double amount) {
        if (amount < 0 || amount > 1) throw new IllegalArgumentException("amount");
        return color.toBuilder()
                .setAlpha(normalize1(color.getAlpha() + amount))
                .build();
    }

    /**
     * Makes $color more opaque.
     * <p>
     * The $amount must be a number between 0 and 1 (inclusive). Increases the alpha channel of $color by that amount.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#opacify">opacify</a>
     */
    public HslColor opacify(HslColor color, double amount) {
        if (amount < 0 || amount > 1) throw new IllegalArgumentException("amount");
        return color.toBuilder()
                .setAlpha(normalize1(color.getAlpha() + amount))
                .build();
    }

    /**
     * Makes $color more opaque.
     * <p>
     * The $amount must be a number between 0 and 1 (inclusive). Increases the alpha channel of $color by that amount.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#opacify">opacify</a>
     */
    public HwbColor opacify(HwbColor color, double amount) {
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
    public int red(RgbColor color) {
        return color.getRed();
    }

    /**
     * Returns the red channel of $color as a number between 0 and 255.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#red">red</a>
     */
    public int red(HslColor color) {
        return red(toRgbColor(color));
    }

    /**
     * Returns the red channel of $color as a number between 0 and 255.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#red">red</a>
     */
    public int red(HwbColor color) {
        return red(toRgbColor(color));
    }

    /**
     * Makes $color more saturated.
     * <p>
     * The $amount must be a number between 0% and 100% (inclusive). Increases the HSL saturation of $color by that amount.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#saturate">saturate</a>
     */
    public RgbColor saturate(RgbColor color, double amount) {
        return toRgbColor(saturate(toHslColor(color), amount));
    }

    /**
     * Makes $color more saturated.
     * <p>
     * The $amount must be a number between 0% and 100% (inclusive). Increases the HSL saturation of $color by that amount.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#saturate">saturate</a>
     */
    public HslColor saturate(HslColor color, double amount) {
        if (amount < 0 || amount > 100) throw new IllegalArgumentException("amount");
        return color.toBuilder()
                .setSaturation(normalize100(color.getSaturation() + amount))
                .build();
    }

    /**
     * Makes $color more saturated.
     * <p>
     * The $amount must be a number between 0% and 100% (inclusive). Increases the HSL saturation of $color by that amount.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#saturate">saturate</a>
     */
    public HwbColor saturate(HwbColor color, double amount) {
        return toHwbColor(saturate(toHslColor(color), amount));
    }

    /**
     * Returns the HSL saturation of $color as a number between 0% and 100%.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#saturation">saturation</a>
     */
    public double saturation(RgbColor color) {
        return saturation(toHslColor(color));
    }

    /**
     * Returns the HSL saturation of $color as a number between 0% and 100%.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#saturation">saturation</a>
     */
    public double saturation(HslColor color) {
        return color.getSaturation();
    }

    /**
     * Returns the HSL saturation of $color as a number between 0% and 100%.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#saturation">saturation</a>
     */
    public double saturation(HwbColor color) {
        return saturation(toHslColor(color));
    }

    /**
     * Makes $color more transparent.
     * <p>
     * The $amount must be a number between 0 and 1 (inclusive). Decreases the alpha channel of $color by that amount.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#transparentize">transparentize</a>
     */
    public RgbColor transparentize(RgbColor color, double amount) {
        if (amount < 0 || amount > 1) throw new IllegalArgumentException("amount");
        return color.toBuilder()
                .setAlpha(normalize1(color.getAlpha() - amount))
                .build();
    }

    /**
     * Makes $color more transparent.
     * <p>
     * The $amount must be a number between 0 and 1 (inclusive). Decreases the alpha channel of $color by that amount.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#transparentize">transparentize</a>
     */
    public HslColor transparentize(HslColor color, double amount) {
        if (amount < 0 || amount > 1) throw new IllegalArgumentException("amount");
        return color.toBuilder()
                .setAlpha(normalize1(color.getAlpha() - amount))
                .build();
    }

    /**
     * Makes $color more transparent.
     * <p>
     * The $amount must be a number between 0 and 1 (inclusive). Decreases the alpha channel of $color by that amount.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#transparentize">transparentize</a>
     */
    public HwbColor transparentize(HwbColor color, double amount) {
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
    public double whiteness(RgbColor color) {
        return whiteness(toHwbColor(color));
    }

    /**
     * Returns the HWB whiteness of $color as a number between 0% and 100%.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#whiteness">whiteness</a>
     */
    public double whiteness(HslColor color) {
        return whiteness(toHwbColor(color));
    }

    /**
     * Returns the HWB whiteness of $color as a number between 0% and 100%.
     *
     * @see <a href="https://sass-lang.com/documentation/modules/color#whiteness">whiteness</a>
     */
    public double whiteness(HwbColor color) {
        return color.getWhiteness();
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
