package de.larsgrefer.sass.embedded.functions;

import com.sass_lang.embedded_protocol.Value.Color;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static de.larsgrefer.sass.embedded.util.ColorUtil.*;
import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.MethodName.class)
class SassColorTest {

    Offset<Double> offset = Offset.offset(0.000001d);

    @Test
    void adjustHue() {
        // Hue 222deg becomes 282deg.
        //@debug adjust-hue(#6b717f, 60deg); // #796b7f
        assertThat(toRgbColor(SassColor.adjustHue(rgb(0x6b717f), 60))).isEqualTo(rgb(0x796b7f));

        // Hue 164deg becomes 104deg.
        //@debug adjust-hue(#d2e1dd, -60deg); // #d6e1d2
        assertThat(toRgbColor(SassColor.adjustHue(rgb(0xd2e1dd), -60))).isEqualTo(rgb(0xd6e1d2));

        // Hue 210deg becomes 255deg.
        //@debug adjust-hue(#036, 45); // #1a0066
        assertThat(toRgbColor(SassColor.adjustHue(rgb(0x003366), 45))).isEqualTo(rgb(0x1a0066));
    }

    @Test
    void blackness() {
        //@debug color.blackness(#e1d7d2); // 11.7647058824%
        assertThat(SassColor.blackness(rgb(0xe1d7d2))).isCloseTo(11.7647058824d, offset);

        //@debug color.blackness(white); // 0%
        assertThat(SassColor.blackness(rgb(0xffffff))).isEqualTo(0d);

        //@debug color.blackness(black); // 100%
        assertThat(SassColor.blackness(rgb(0x000000))).isEqualTo(100d);
    }

    @Test
    void complement() {
        // Hue 222deg becomes 42deg.
        //@debug color.complement(#6b717f); // #7f796b
        assertThat(toRgbColor(SassColor.complement(rgb(0x6b717f)))).isEqualTo(rgb(0x7f796b));

        // Hue 164deg becomes 344deg.
        //@debug color.complement(#d2e1dd); // #e1d2d6
        assertThat(toRgbColor(SassColor.complement(rgb(0xd2e1dd)))).isEqualTo(rgb(0xe1d2d6));

        // Hue 210deg becomes 30deg.
        //@debug color.complement(#036); // #663300
        assertThat(toRgbColor(SassColor.complement(rgb(0x003366)))).isEqualTo(rgb(0x663300));
    }

    @Test
    @Disabled
    void darken() {
        // Lightness 92% becomes 72%.
        //@debug darken(#b37399, 20%); // #7c4465
        assertThat(SassColor.darken(rgb(0xb37399), 20)).isEqualTo(rgb(0x7c4465));

        // Lightness 85% becomes 45%.
        //@debug darken(#f2ece4, 40%); // #b08b5a
        assertThat(SassColor.darken(rgb(0xf2ece4), 40)).isEqualTo(rgb(0xb08b5a));

        // Lightness 20% becomes 0%.
        //@debug darken(#036, 30%); // black
        assertThat(SassColor.darken(rgb(0x003366), 30)).isEqualTo(rgb(0x000000));
    }

    @Test
    void desaturate() {
        // Saturation 100% becomes 80%.
        //@debug desaturate(#036, 20%); // #0a335c
        assertThat(toRgbColor(SassColor.desaturate(rgb(0x003366), 20))).isEqualTo(rgb(0x0a335c));

        // Saturation 35% becomes 15%.
        //@debug desaturate(#f2ece4, 20%); // #eeebe8
        assertThat(toRgbColor(SassColor.desaturate(rgb(0xf2ece4), 20))).isEqualTo(rgb(0xeeebe8));

        // Saturation 20% becomes 0%.
        //@debug desaturate(#d2e1dd, 30%); // #dadada
        assertThat(toRgbColor(SassColor.desaturate(rgb(0xd2e1dd), 20))).isEqualTo(rgb(0xdadada));
    }

    @Test
    void greyscale() {
        //@debug color.grayscale(#6b717f); // #757575
        assertThat(toRgbColor(SassColor.grayscale(rgb(0x6b717f)))).isEqualTo(rgb(0x757575));

        //@debug color.grayscale(#d2e1dd); // #dadada
        assertThat(toRgbColor(SassColor.grayscale(rgb(0xd2e1dd)))).isEqualTo(rgb(0xdadada));

        //@debug color.grayscale(#036); // #333333
        assertThat(toRgbColor(SassColor.grayscale(rgb(0x003366)))).isEqualTo(rgb(0x333333));
    }

    @Test
    void green() {
        //@debug color.green(#e1d7d2); // 215
        assertThat(SassColor.green(rgb(0xe1d7d2))).isEqualTo(215);

        //@debug color.green(white); // 255
        assertThat(SassColor.green(rgb(0xffffff))).isEqualTo(255);

        //@debug color.green(black); // 0
        assertThat(SassColor.green(rgb(0x000000))).isEqualTo(0);
    }

    @Test
    void hue() {
        //@debug color.hue(#e1d7d2); // 20deg
        assertThat(SassColor.hue(rgb(0xe1d7d2))).isCloseTo(20, offset);

        //@debug color.hue(#f2ece4); // 34.2857142857deg
        assertThat(SassColor.hue(rgb(0xf2ece4))).isCloseTo(34.2857142857, offset);

        //@debug color.hue(#dadbdf); // 228deg
        assertThat(SassColor.hue(rgb(0xdadbdf))).isCloseTo(228, offset);
    }

    @Test
    @Disabled
    void hwb() {
        //@debug color.hwb(210, 0%, 60%); // #036
        Color actual = SassColor.hwb(210, 0, 60);
        Color expected = rgb(0x003366);
        assertThat(actual).isEqualTo(toHwbColor(expected));
        assertThat(toRgbColor(actual)).isEqualTo(expected);

        //@debug color.hwb(34, 89%, 5%); // #f2ece4
        actual = SassColor.hwb(34, 89, 5);
        expected = rgb(0xf2ece4);
//        assertThat(actual).isEqualTo(toHwbColor(expected));
        assertThat(toRgbColor(actual)).isEqualTo(expected);

        //@debug color.hwb(210 0% 60% / 0.5); // rgba(0, 51, 102, 0.5)
        actual = SassColor.hwb(210, 0, 60, 0.5);
        expected = rgba(0, 51, 102, 0.5);
        assertThat(actual).isEqualTo(toHwbColor(expected));
        assertThat(toRgbColor(actual)).isEqualTo(expected);
    }

    @Test
    void ieHexString() {
        //@debug color.ie-hex-str(#b37399); // #FFB37399
        assertThat(SassColor.ieHexStr(rgb(0xb37399)).getText()).isEqualTo("#FFB37399");

        //@debug color.ie-hex-str(#808c99); // #FF808C99
        assertThat(SassColor.ieHexStr(rgb(0x808c99)).getText()).isEqualTo("#FF808C99");

        //@debug color.ie-hex-str(rgba(242, 236, 228, 0.6)); // #99F2ECE4
        assertThat(SassColor.ieHexStr(rgba(242, 236, 228, 0.6)).getText()).isEqualTo("#99F2ECE4");
    }

    @Test
    void invert() {
        //@debug color.invert(#b37399); // #4c8c66
        assertThat(SassColor.invert(rgb(0xb37399))).isEqualTo(rgb(0x4c8c66));

        //@debug color.invert(black); // white
        assertThat(SassColor.invert(black)).isEqualTo(white);

        //@debug color.invert(#550e0c, 20%); // #663b3a
        assertThat(SassColor.invert(rgb(0x550e0c), 0.2)).isEqualTo(rgb(0x663b3a));
    }

    @Test
    void lighten() {
        // Lightness 46% becomes 66%.
        //@debug lighten(#6b717f, 20%); // #a1a5af
        assertThat(toRgbColor(SassColor.lighten(rgb(0x6b717f), 20))).isEqualTo(rgb(0xa1a5af));

        // Lightness 20% becomes 80%.
        //@debug lighten(#036, 60%); // #99ccff
        assertThat(toRgbColor(SassColor.lighten(rgb(0x003366), 60))).isEqualTo(rgb(0x99ccff));

        // Lightness 85% becomes 100%.
        //@debug lighten(#e1d7d2, 30%); // white
        assertThat(toRgbColor(SassColor.lighten(rgb(0xe1d7d2), 30))).isEqualTo(white);
    }

    @Test
    void mix() {
        //@debug color.mix(#036, #d2e1dd); // #698aa2
        assertThat(SassColor.mix(rgb(0x003366), rgb(0xd2e1dd))).isEqualTo(rgb(0x698aa2));

        //@debug color.mix(#036, #d2e1dd, 75%); // #355f84
        assertThat(SassColor.mix(rgb(0x003366), rgb(0xd2e1dd), 0.75)).isEqualTo(rgb(0x355f84));

        //@debug color.mix(#036, #d2e1dd, 25%); // #9eb6bf
        assertThat(SassColor.mix(rgb(0x003366), rgb(0xd2e1dd), 0.25)).isEqualTo(rgb(0x9eb6bf));

        //@debug color.mix(rgba(242, 236, 228, 0.5), #6b717f); // rgba(141, 144, 152, 0.75)
        assertThat(SassColor.mix(rgba(242, 236, 228, 0.5), rgb(0x6b717f))).isEqualTo(rgba(141, 144, 152, 0.75));
    }

    @Test
    void opacify() {
        //@debug opacify(rgba(#6b717f, 0.5), 0.2); // rgba(107, 113, 127, 0.7)
        assertThat(SassColor.opacify(rgba(0x6b717f, 0.5), 0.2)).isEqualTo(rgba(107, 113, 127, 0.7));

        //@debug fade-in(rgba(#e1d7d2, 0.5), 0.4); // rgba(225, 215, 210, 0.9)
        assertThat(SassColor.opacify(rgba(0xe1d7d2, 0.5), 0.4)).isEqualTo(rgba(225, 215, 210, 0.9));

        //@debug opacify(rgba(#036, 0.7), 0.3); // #036
        assertThat(SassColor.opacify(rgba(0x003366, 0.7), 0.3)).isEqualTo(rgb(0x003366));
    }

    @Test
    void red() {
        //@debug color.red(#e1d7d2); // 225

        //@debug color.red(white); // 255
        assertThat(SassColor.red(white)).isEqualTo(255);

        //@debug color.red(black); // 0
        assertThat(SassColor.red(black)).isEqualTo(0);
    }

    @Test
    @Disabled
    void saturation() {
        //@debug color.saturation(#e1d7d2); // 20%
        assertThat(SassColor.saturation(rgb(0xe1d7d2))).isCloseTo(20, offset);

        //@debug color.saturation(#f2ece4); // 30%
        assertThat(SassColor.saturation(rgb(0xf2ece4))).isCloseTo(30, offset);

        //@debug color.saturation(#dadbdf); // 7.2463768116%
        assertThat(SassColor.saturation(rgb(0xdadbdf))).isCloseTo(7.2463768116, offset);
    }

    @Test
    void transparentize() {
        //@debug transparentize(rgba(#6b717f, 0.5), 0.2)  // rgba(107, 113, 127, 0.3)
        assertThat(SassColor.transparentize(rgba(0x6b717f, 0.5), 0.2)).isEqualTo(rgba(107, 113, 127, 0.3));

        //@debug fade-out(rgba(#e1d7d2, 0.5), 0.4)  // rgba(225, 215, 210, 0.1)
        assertThat(SassColor.transparentize(rgba(0xe1d7d2, 0.5), 0.4).getAlpha()).isCloseTo(0.1, offset);

        //@debug transparentize(rgba(#036, 0.3), 0.3)  // rgba(0, 51, 102, 0)
        assertThat(SassColor.transparentize(rgba(0x003366, 0.3), 0.3)).isEqualTo(rgba(0, 51, 102, 0));
    }

    @Test
    void whiteness() {
        //@debug color.whiteness(#e1d7d2); // 82.3529411765%
        assertThat(SassColor.whiteness(rgb(0xe1d7d2))).isCloseTo(82.3529411765, offset);

        //@debug color.whiteness(white); // 100%
        assertThat(SassColor.whiteness(white)).isEqualTo(100);

        //@debug color.whiteness(black); // 0%
        assertThat(SassColor.whiteness(black)).isEqualTo(0);
    }

    @Test
    void normalizeHue() {

        assertThat(SassColor.normalizeHue(0)).isEqualTo(0d);
        assertThat(SassColor.normalizeHue(1)).isEqualTo(1d);
        assertThat(SassColor.normalizeHue(360)).isEqualTo(0d);
        assertThat(SassColor.normalizeHue(361)).isEqualTo(1d);
        assertThat(SassColor.normalizeHue(-120)).isEqualTo(240d);
    }
}