package de.larsgrefer.sass.embedded.importer;

import com.google.protobuf.ByteString;
import com.sass_lang.embedded_protocol.InboundMessage.ImportResponse.ImportSuccess;
import com.sass_lang.embedded_protocol.Syntax;
import de.larsgrefer.sass.embedded.util.SyntaxUtil;
import lombok.extern.slf4j.Slf4j;
import org.jboss.vfs.VFS;
import org.jboss.vfs.VFSUtils;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.stream.Collectors;

/**
 * @author Lars Grefer
 */
@Slf4j
public abstract class CustomUrlImporter extends CustomImporter {

    private final Set<String> protocols = new HashSet<>();
    private final Set<String> usedPrefixes = new HashSet<>();

    @Override
    public String canonicalize(String url, boolean fromImport) throws Exception {
        URL result = canonicalizeUrl(url);

        if (result == null && !usedPrefixes.isEmpty() && isAbsolute(url)) {
            result = canonicalizeUrl(getRelativePart(url));
        }

        if (result == null) {
            return null;
        }

        String urlString;

        if (result.getProtocol().equals("file")) {
            URI fileUri = new File(result.getPath()).toPath().toUri();
            protocols.add(fileUri.getScheme());
            urlString = fileUri.toString();
        }
        else {
            protocols.add(result.getProtocol());
            urlString = result.toString();
        }

        if (urlString.length() > url.length()) {
            if (urlString.endsWith(url)) {
                usedPrefixes.add(urlString.substring(0, urlString.length() - url.length()));
            }
            else {
                log.info("{} -> {}", url, urlString);
            }
        }

        return urlString;
    }

    @Nullable
    public abstract URL canonicalizeUrl(String url) throws Exception;

    private boolean isAbsolute(String url) {
        if (!url.contains(":")) {
            return false;
        }

        for (String protocol : protocols) {
            if (url.startsWith(protocol + ":")) {
                return true;
            }
        }

        return false;
    }

    private String getRelativePart(String url) {
        List<String> prefixes = usedPrefixes.stream()
                .filter(url::startsWith)
                .sorted(Comparator.comparingInt(String::length))
                .collect(Collectors.toList());

        if (prefixes.size() == 0) {
            throw new IllegalStateException(url + " has no known prefix");
        }
        else if (prefixes.size() == 1) {
            String prefix = prefixes.get(0);
            return url.substring(prefix.length());
        }
        else {
            throw new IllegalStateException("Multiple prefixes for URL " + url);
        }
    }

    @Override
    public ImportSuccess handleImport(String string) throws Exception {
        URL url = new URL(string);
        return handleImport(url);
    }

    public ImportSuccess handleImport(URL url) throws Exception {
        ImportSuccess.Builder result = ImportSuccess.newBuilder();

        URLConnection urlConnection = url.openConnection();
        try (InputStream in = urlConnection.getInputStream()) {
            ByteString content = ByteString.readFrom(in);
            result.setContentsBytes(content);
            Syntax syntax = SyntaxUtil.guessSyntax(urlConnection);
            if (syntax == Syntax.UNRECOGNIZED) {
                throw new IllegalStateException("Failed to guess syntax for URL " + url);
            }
            result.setSyntax(syntax);
        }

        return result.build();
    }

    protected boolean isFile(URL url) throws IOException {

        String urlPath = url.getPath();
        if (url.getProtocol().equals("file")) {
            File file = new File(urlPath);
            return file.exists() && file.isFile();
        }

        if (url.getProtocol().equals("vfs")) {
            try {
                URI uri = VFSUtils.toURI(url);
                return VFS.getChild(uri).isFile();
            } catch (URISyntaxException e) {
                throw new IOException(e);
            }
        }

        URLConnection connection = url.openConnection();
        if (connection instanceof JarURLConnection) {
            JarURLConnection jarURLConnection = (JarURLConnection) connection;

            try {
                JarEntry jarEntry = jarURLConnection.getJarEntry();

                return jarEntry != null && !jarEntry.isDirectory();
            } catch (FileNotFoundException e) {
                return false;
            }
        } else if (connection instanceof HttpURLConnection) {
            HttpURLConnection httpURLConnection = (HttpURLConnection) connection;
            httpURLConnection.setRequestMethod("HEAD");

            int responseCode = httpURLConnection.getResponseCode();
            Syntax syntax = SyntaxUtil.guessSyntax(connection);

            log.debug("Got {} with {} for HEAD {}", responseCode, httpURLConnection.getContentType(), connection.getURL());

            return responseCode == 200 && syntax != Syntax.UNRECOGNIZED;
        }

        // Best guess

        long contentLength = connection.getContentLengthLong();
        if (contentLength == 0) {
            return false;
        } else if (contentLength > 0) {
            return true;
        }

        throw new IllegalArgumentException("Can't handle url: " + url);
    }
}
