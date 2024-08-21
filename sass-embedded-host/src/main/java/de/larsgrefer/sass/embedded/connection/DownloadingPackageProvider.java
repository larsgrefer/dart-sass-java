package de.larsgrefer.sass.embedded.connection;

import de.larsgrefer.sass.embedded.util.PlatformUtils;
import de.larsgrefer.sass.embedded.util.PropertyUtils;

import java.io.IOException;
import java.net.URL;

public class DownloadingPackageProvider extends DartSassPackageProvider {


    @Override
    protected URL getPackageUrl() throws IOException {

        String version = PropertyUtils.getDartSassVersion();
        String suffix = PlatformUtils.getDartSassPackageSuffix();

        String urlString = String.format("https://github.com/sass/dart-sass/releases/download/%1$s/dart-sass-%1$s-%2$s", version, suffix);
        return new URL(urlString);
    }
}
