package de.larsgrefer.sass.embedded.util;

import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.util.Properties;

@UtilityClass
public class PropertyUtils {

    private static final Properties properties = new Properties();

    static {
        try {
            properties.load(PropertyUtils.class.getResourceAsStream("/de/larsgrefer/sass/embedded/sass-embedded-host.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getDartSassVersion() {
        return properties.getProperty("dartSassVersion");
    }

    public String getEmbeddedProtocolVersion() {
        return properties.getProperty("embeddedProtocolVersion");
    }

    public String getHostVersion() {
        return properties.getProperty("hostVersion");
    }
}
