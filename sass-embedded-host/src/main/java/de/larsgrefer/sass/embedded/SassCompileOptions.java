package de.larsgrefer.sass.embedded;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import sass.embedded_protocol.EmbeddedSass;

@Data
public class SassCompileOptions {

    private Syntax syntax = Syntax.SCSS;
    private EmbeddedSass.InboundMessage.CompileRequest.OutputStyle outputStyle = EmbeddedSass.InboundMessage.CompileRequest.OutputStyle.EXPANDED;

    public static enum Syntax {
        SCSS, SASS
    }

}
