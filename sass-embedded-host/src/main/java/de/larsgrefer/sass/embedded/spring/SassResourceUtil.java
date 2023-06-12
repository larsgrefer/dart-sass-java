package de.larsgrefer.sass.embedded.spring;

import com.google.protobuf.ByteString;
import com.sass_lang.embedded_protocol.InboundMessage.CompileRequest.StringInput;
import com.sass_lang.embedded_protocol.Syntax;
import de.larsgrefer.sass.embedded.util.SyntaxUtil;
import lombok.experimental.UtilityClass;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Lars Grefer
 */
@UtilityClass
public class SassResourceUtil {

    public static StringInput toStringInput(Resource resource) throws IOException {
        Assert.notNull(resource, "resource must not be null");
        Assert.notNull(resource.getFilename(), "resource must have a filename");
        return toStringInput(resource, SyntaxUtil.guessSyntax(resource.getFilename()));
    }

    public static StringInput toStringInput(Resource resource, Syntax syntax) throws IOException {
        Assert.notNull(resource, "resource must not be null");
        Assert.notNull(syntax, "syntax must not be null");

        ByteString byteString = getByteString(resource);

        return StringInput.newBuilder()
                .setSourceBytes(byteString)
                .setSyntax(syntax)
                .build();
    }

    public static ByteString getByteString(Resource resource) throws IOException {

        if (resource instanceof ByteArrayResource) {
            return ByteString.copyFrom(((ByteArrayResource) resource).getByteArray());
        } else {
            try (InputStream in = resource.getInputStream()) {
                return ByteString.readFrom(in);
            }
        }
    }
}
