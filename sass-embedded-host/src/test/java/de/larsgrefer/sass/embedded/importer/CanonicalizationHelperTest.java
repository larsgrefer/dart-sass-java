package de.larsgrefer.sass.embedded.importer;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CanonicalizationHelperTest {

    @Test
    void resolvePossiblePaths_noExt() {
        List<String> strings = CanonicalizationHelper.resolvePossiblePaths("foo/bar/baz");

        assertThat(strings).containsExactly(
                "foo/bar/baz.sass",
                "foo/bar/baz.scss",
                "foo/bar/_baz.sass",
                "foo/bar/_baz.scss"
        );
    }

    @Test
    void resolvePossiblePaths_ext() {
        List<String> strings = CanonicalizationHelper.resolvePossiblePaths("foo/bar/baz.scss");

        assertThat(strings).containsExactly(
                "foo/bar/baz.scss",
                "foo/bar/_baz.scss"
        );
    }
}