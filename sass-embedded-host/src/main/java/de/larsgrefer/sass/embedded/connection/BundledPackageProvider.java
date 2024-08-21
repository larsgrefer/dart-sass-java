package de.larsgrefer.sass.embedded.connection;

import androidx.annotation.RequiresApi;
import de.larsgrefer.sass.embedded.util.PlatformUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;

@Slf4j
@RequiresApi(10000)
public class BundledPackageProvider extends DartSassPackageProvider {

    protected URL getPackageUrl() {
        String dartSassPackageSuffix = PlatformUtils.getDartSassPackageSuffix();

        String resourcePath = String.format("/de/larsgrefer/sass/embedded/bundled/dart-sass-%s", dartSassPackageSuffix);

        return this.getClass().getResource(resourcePath);
    }


}
