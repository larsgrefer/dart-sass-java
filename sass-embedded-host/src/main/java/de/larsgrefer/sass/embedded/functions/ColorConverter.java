package de.larsgrefer.sass.embedded.functions;

import lombok.experimental.UtilityClass;
import sass.embedded_protocol.EmbeddedSass;
import sass.embedded_protocol.EmbeddedSass.Value.HwbColorOrBuilder;
import sass.embedded_protocol.EmbeddedSass.Value.RgbColor;

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
        float white = (float) hwbColor.getWhiteness();
        float black = (float) hwbColor.getBlackness();

        if (white + black >= 1) {
            float gray = white / (white + black);

            return new Color(gray, gray, gray, (float) hwbColor.getAlpha());
        }

        float[] rgb = new Color(Color.HSBtoRGB((float) hwbColor.getHue(), 1f, 1f)).getRGBColorComponents(null);

        for (int i = 0; i < 3; i++) {
            rgb[i] *= (1 - white - black);
            rgb[i] += white;
        }

        return new Color(rgb[0], rgb[1], rgb[2], (float) hwbColor.getAlpha());
    }

    public static Color toJavaColor(EmbeddedSass.Value.HslColorOrBuilder hslColor) {
        Color color = Color.getHSBColor(
                (float) hslColor.getHue(),
                (float) hslColor.getSaturation(),
                (float) hslColor.getLightness());
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (hslColor.getAlpha() * 255));
    }

    public static Color toJavaColor(RgbColor rgbColor) {
        return new Color(rgbColor.getRed(), rgbColor.getGreen(), rgbColor.getBlue(), (int) (rgbColor.getAlpha() * 255));
    }

    public static RgbColor toRgbColor(HwbColorOrBuilder hwbColor) {
        Color color = toJavaColor(hwbColor);
        return toRgbColor(color);
    }

    public static RgbColor toRgbColor(Color color) {
        return RgbColor.newBuilder()
                .setRed(color.getRed())
                .setGreen(color.getGreen())
                .setBlue(color.getBlue())
                .setAlpha(color.getAlpha() / 255d)
                .build();
    }
}
