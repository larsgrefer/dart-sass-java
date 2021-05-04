package de.larsgrefer.sass.embedded.importer;

import lombok.experimental.UtilityClass;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Lars Grefer
 */
@UtilityClass
public class CanonicalizationHelper {

    public static List<String> resolvePossiblePaths(String url) {
        List<String> results = new LinkedList<>();

        int i = url.lastIndexOf("/");

        String base = url.substring(0, i + 1);
        String name = url.substring(i + 1);

        boolean hasExtension = name.endsWith(".scss") || name.endsWith(".sass");

        if (!hasExtension) {
            results.add(base + name + ".sass");
            results.add(base + name + ".scss");
            results.add(base + "_" + name + ".sass");
            results.add(base + "_" + name + ".scss");
        }
        else {
            results.add(base + name);
            results.add(base + "_" + name);
        }

        return results;
    }

    public static List<String> resolvePossibleIndexPaths(String url) {
        List<String> results = new LinkedList<>();

        int i = url.lastIndexOf("/");
        int j = url.lastIndexOf(".");

        boolean hasExtension = j > i;

        if (hasExtension) {
            return Collections.emptyList();
        }
        else {
            results.add(url + "/_index.sass");
            results.add(url + "/index.sass");
            results.add(url + "/_index.scss");
            results.add(url + "/index.scss");
        }

        return results;
    }
}
