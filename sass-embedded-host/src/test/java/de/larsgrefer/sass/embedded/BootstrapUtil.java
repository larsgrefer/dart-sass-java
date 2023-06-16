package de.larsgrefer.sass.embedded;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

@UtilityClass
@Slf4j
public class BootstrapUtil {

    private static String bootstrapVersion;

    public String getBoostrapVersion() throws IOException {

        if (!StringUtils.hasText(bootstrapVersion)) {
            URL resource = BootstrapUtil.class.getResource("/META-INF/maven/org.webjars/bootstrap/pom.properties");

            Properties properties = new Properties();
            try (InputStream in = resource.openStream()) {
                properties.load(in);
            }

            bootstrapVersion = properties.getProperty("version");
        }

        return bootstrapVersion;
    }
}
