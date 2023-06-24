package de.larsgrefer.sass.embedded;

import com.google.protobuf.ByteString;
import com.sass_lang.embedded_protocol.*;
import com.sass_lang.embedded_protocol.InboundMessage.*;
import com.sass_lang.embedded_protocol.OutboundMessage.CanonicalizeRequest;
import com.sass_lang.embedded_protocol.OutboundMessage.FileImportRequest;
import com.sass_lang.embedded_protocol.OutboundMessage.FunctionCallRequest;
import com.sass_lang.embedded_protocol.OutboundMessage.ImportRequest;
import de.larsgrefer.sass.embedded.connection.CompilerConnection;
import de.larsgrefer.sass.embedded.connection.Packet;
import de.larsgrefer.sass.embedded.functions.HostFunction;
import de.larsgrefer.sass.embedded.importer.CustomImporter;
import de.larsgrefer.sass.embedded.importer.FileImporter;
import de.larsgrefer.sass.embedded.importer.Importer;
import de.larsgrefer.sass.embedded.importer.RelativeUrlImporter;
import de.larsgrefer.sass.embedded.logging.LoggingHandler;
import de.larsgrefer.sass.embedded.logging.Slf4jLoggingHandler;
import de.larsgrefer.sass.embedded.util.SyntaxUtil;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.intellij.lang.annotations.Language;

import javax.annotation.Nonnull;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

import static de.larsgrefer.sass.embedded.util.ProtocolUtil.inboundMessage;

/**
 * @author Lars Grefer
 * @see SassCompilerFactory#bundled()
 */
@Slf4j
public class SassCompiler implements Closeable {

    /**
     * How to format the CSS output.
     *
     * @see CompileRequest#getStyle()
     */
    @Getter
    @Setter
    private OutputStyle outputStyle = OutputStyle.EXPANDED;

    /**
     * Whether to generate a source map. Note that this will *not* add a source
     * map comment to the stylesheet; that's up to the host or its users.
     *
     * @see CompileRequest#getSourceMap()
     */
    @Getter
    @Setter
    private boolean generateSourceMaps = false;

    /**
     * Whether to use terminal colors in the formatted message of errors and
     * logs.
     *
     * @see CompileRequest#getAlertColor()
     */
    @Getter
    @Setter
    private boolean alertColor = false;

    /**
     * Whether to encode the formatted message of errors and logs in ASCII.
     *
     * @see CompileRequest#getAlertAscii()
     */
    @Getter
    @Setter
    private boolean alertAscii = false;

    /**
     * Whether to report all deprecation warnings or only the first few ones.
     * If this is `false`, the compiler may choose not to send events for
     * repeated deprecation warnings. If this is `true`, the compiler must emit
     * an event for every deprecation warning it encounters.
     *
     * @see CompileRequest#getVerbose()
     */
    @Getter
    @Setter
    private boolean verbose = false;

    /**
     * Whether to omit events for deprecation warnings coming from dependencies
     * (files loaded from a different importer than the input).
     *
     * @see CompileRequest#getQuietDeps()
     */
    @Getter
    @Setter
    private boolean quietDeps = false;

    /**
     * Whether to include sources in the generated sourcemap
     *
     * @see CompileRequest#getSourceMapIncludeSources()
     */
    @Getter
    @Setter
    private boolean sourceMapIncludeSources = false;

    /**
     * Whether to emit a `@charset`/BOM for non-ASCII stylesheets.
     *
     * @see CompileRequest#getCharset()
     */
    @Getter
    @Setter
    private boolean emitCharset = false;

    private final CompilerConnection connection;

    private final Random compileRequestIds = new Random();

    private final Map<String, HostFunction> globalFunctions = new HashMap<>();
    private final Map<Integer, FileImporter> fileImporters = new HashMap<>();
    private final Map<Integer, CustomImporter> customImporters = new HashMap<>();

    @Setter
    @Getter
    private LoggingHandler loggingHandler = new Slf4jLoggingHandler(log);

    @Getter
    @Setter
    private List<File> loadPaths = new LinkedList<>();

    public SassCompiler(CompilerConnection connection) {
        this.connection = connection;
    }

    public OutboundMessage.VersionResponse getVersion() throws IOException {
        return exec(inboundMessage(VersionRequest.getDefaultInstance())).getVersionResponse();
    }

    public void registerFunction(@NonNull HostFunction sassFunction) {
        globalFunctions.put(sassFunction.getName(), sassFunction);
    }

    public void registerImporter(@NonNull FileImporter fileImporter) {
        fileImporters.put(fileImporter.getId(), fileImporter);
    }

    public void registerImporter(@NonNull CustomImporter customImporter) {
        customImporters.put(customImporter.getId(), customImporter);
    }

    protected CompileRequest.Builder compileRequestBuilder() {
        CompileRequest.Builder builder = CompileRequest.newBuilder();

        builder.setStyle(outputStyle);
        builder.setSourceMap(generateSourceMaps);

        for (File loadPath : loadPaths) {
            CompileRequest.Importer importer = CompileRequest.Importer.newBuilder()
                    .setPath(loadPath.getAbsolutePath())
                    .build();
            builder.addImporters(importer);
        }

        for (Importer value : customImporters.values()) {
            CompileRequest.Importer importer = CompileRequest.Importer.newBuilder()
                    .setImporterId(value.getId())
                    .build();
            builder.addImporters(importer);
        }

        for (Importer value : fileImporters.values()) {
            CompileRequest.Importer importer = CompileRequest.Importer.newBuilder()
                    .setFileImporterId(value.getId())
                    .build();
            builder.addImporters(importer);
        }

        for (HostFunction sassFunction : globalFunctions.values()) {
            builder.addGlobalFunctions(sassFunction.getSignature());
        }

        builder.setAlertColor(alertColor);
        builder.setAlertAscii(alertAscii);
        builder.setVerbose(verbose);
        builder.setQuietDeps(quietDeps);
        builder.setSourceMapIncludeSources(sourceMapIncludeSources);
        builder.setCharset(emitCharset);

        return builder;
    }

    public CompileSuccess compile(@NonNull URL source) throws SassCompilationFailedException, IOException {
        return compile(source, getOutputStyle());
    }

    public CompileSuccess compile(@NonNull URL source, OutputStyle outputStyle) throws SassCompilationFailedException, IOException {
        if (source.getProtocol().equals("file")) {
            File file = new File(source.getPath());
            return compileFile(file);
        }

        Syntax syntax;
        ByteString content;
        URLConnection urlConnection = source.openConnection();
        try (InputStream in = urlConnection.getInputStream()) {
            content = ByteString.readFrom(in);
            syntax = SyntaxUtil.guessSyntax(urlConnection);
        }

        CustomImporter importer = new RelativeUrlImporter(source).autoCanonicalize();

        customImporters.put(importer.getId(), importer);

        CompileRequest.StringInput build = CompileRequest.StringInput.newBuilder()
                .setUrl(source.toString())
                .setSourceBytes(content)
                .setImporter(CompileRequest.Importer.newBuilder()
                        .setImporterId(importer.getId())
                        .build())
                .setSyntax(syntax)
                .build();

        try {
            return compileString(build, outputStyle);
        } finally {
            customImporters.remove(importer.getId());
        }
    }

    //region compileString and overloads
    public CompileSuccess compileScssString(@NonNull @Language("SCSS") String source) throws IOException, SassCompilationFailedException {
        return compileString(source, Syntax.SCSS);
    }

    public CompileSuccess compileSassString(@NonNull @Language("SASS") String source) throws IOException, SassCompilationFailedException {
        return compileString(source, Syntax.INDENTED);
    }

    public CompileSuccess compileCssString(@NonNull @Language("CSS") String source) throws IOException, SassCompilationFailedException {
        return compileString(source, Syntax.CSS);
    }

    public CompileSuccess compileString(String source, Syntax syntax) throws SassCompilationFailedException, IOException {
        return compileString(source, syntax, getOutputStyle());
    }

    public CompileSuccess compileString(@NonNull String source, Syntax syntax, OutputStyle outputStyle) throws IOException, SassCompilationFailedException {
        CompileRequest.StringInput stringInput = CompileRequest.StringInput.newBuilder()
                .setSource(source)
                .setSyntax(syntax)
                .build();

        return compileString(stringInput, outputStyle);
    }

    @Nonnull
    public CompileSuccess compileString(CompileRequest.StringInput string, @NonNull OutputStyle outputStyle) throws IOException, SassCompilationFailedException {

        CompileRequest compileRequest = compileRequestBuilder()
                .setString(string)
                .setStyle(outputStyle)
                .build();

        return execCompileRequest(compileRequest);
    }
    //endregion

    //region compileFile

    public CompileSuccess compileFile(@NonNull File inputFile) throws IOException, SassCompilationFailedException {
        return compileFile(inputFile, getOutputStyle());
    }

    public CompileSuccess compileFile(@NonNull File file, @NonNull OutputStyle outputStyle) throws IOException, SassCompilationFailedException {
        CompileRequest compileRequest = compileRequestBuilder()
                .setPath(file.getPath())
                .setStyle(outputStyle)
                .build();

        return execCompileRequest(compileRequest);
    }

    //endregion

    private CompileSuccess execCompileRequest(CompileRequest compileRequest) throws IOException, SassCompilationFailedException {

        OutboundMessage outboundMessage = exec(inboundMessage(compileRequest));

        if (!outboundMessage.hasCompileResponse()) {
            throw new IllegalStateException("No compile response");
        }

        OutboundMessage.CompileResponse compileResponse = outboundMessage.getCompileResponse();

        if (compileResponse.hasSuccess()) {
            return new CompileSuccess(compileResponse);
        } else if (compileResponse.hasFailure()) {
            throw new SassCompilationFailedException(compileResponse);
        } else {
            throw new IllegalStateException("Neither success nor failure");
        }
    }

    private OutboundMessage exec(InboundMessage inboundMessage) throws IOException {
        int compilationId;

        if (inboundMessage.hasVersionRequest()) {
            compilationId = 0;
        } else if (inboundMessage.hasCompileRequest()) {
            compilationId = Math.abs(compileRequestIds.nextInt());
        } else {
            throw new IllegalArgumentException("Invalid message type: " + inboundMessage.getMessageCase());
        }

        Packet<InboundMessage> request = new Packet<>(compilationId, inboundMessage);

        synchronized (connection) {
            connection.sendMessage(request);

            while (true) {
                Packet<OutboundMessage> response = connection.readResponse();

                if (response.getCompilationId() != compilationId) {
                    //Should never happen
                    throw new IllegalStateException(String.format("Compilation ID mismatch: expected %d, but got %d", compilationId, response.getCompilationId()));
                }

                OutboundMessage outboundMessage = response.getMessage();

                switch (outboundMessage.getMessageCase()) {

                    case ERROR:
                        throw new SassProtocolErrorException(outboundMessage.getError());
                    case COMPILE_RESPONSE:
                    case VERSION_RESPONSE:
                        return outboundMessage;
                    case LOG_EVENT:
                        loggingHandler.handle(outboundMessage.getLogEvent());
                        break;
                    case CANONICALIZE_REQUEST:
                        handleCanonicalizeRequest(compilationId, outboundMessage.getCanonicalizeRequest());
                        break;
                    case IMPORT_REQUEST:
                        handleImportRequest(compilationId, outboundMessage.getImportRequest());
                        break;
                    case FILE_IMPORT_REQUEST:
                        handleFileImportRequest(compilationId, outboundMessage.getFileImportRequest());
                        break;
                    case FUNCTION_CALL_REQUEST:
                        handleFunctionCallRequest(compilationId, outboundMessage.getFunctionCallRequest());
                        break;
                    case MESSAGE_NOT_SET:
                        throw new IllegalStateException("No message set");
                    default:
                        throw new IllegalStateException("Unknown OutboundMessage: " + outboundMessage.getMessageCase());
                }
            }
        }
    }

    private void handleFileImportRequest(int compilationId, FileImportRequest fileImportRequest) throws IOException {
        FileImportResponse.Builder fileImportResponse = FileImportResponse.newBuilder()
                .setId(fileImportRequest.getId());

        FileImporter fileImporter = fileImporters.get(fileImportRequest.getImporterId());

        try {
            File file = fileImporter.handleImport(fileImportRequest.getUrl(), fileImportRequest.getFromImport());
            if (file != null) {
                fileImportResponse.setFileUrl(file.toURI().toURL().toString());
            }
        } catch (Throwable t) {
            log.debug("Failed to execute FileImportRequest {}", fileImportRequest, t);
            fileImportResponse.setError(getErrorMessage(t));
        }

        connection.sendMessage(compilationId, inboundMessage(fileImportResponse.build()));
    }

    private void handleImportRequest(int compilationId, ImportRequest importRequest) throws IOException {
        ImportResponse.Builder importResponse = ImportResponse.newBuilder()
                .setId(importRequest.getId());

        CustomImporter customImporter = customImporters.get(importRequest.getImporterId());

        try {
            ImportResponse.ImportSuccess success = customImporter.handleImport(importRequest.getUrl());
            if (success != null) {
                importResponse.setSuccess(success);
            }
        } catch (Throwable t) {
            log.debug("Failed to handle ImportRequest {}", importRequest, t);
            importResponse.setError(getErrorMessage(t));
        }

        connection.sendMessage(compilationId, inboundMessage(importResponse.build()));
    }

    private void handleCanonicalizeRequest(int compilationId, CanonicalizeRequest canonicalizeRequest) throws IOException {
        CanonicalizeResponse.Builder canonicalizeResponse = CanonicalizeResponse.newBuilder()
                .setId(canonicalizeRequest.getId());

        CustomImporter customImporter = customImporters.get(canonicalizeRequest.getImporterId());

        try {
            String canonicalize = customImporter.canonicalize(canonicalizeRequest.getUrl(), canonicalizeRequest.getFromImport());
            if (canonicalize != null) {
                canonicalizeResponse.setUrl(canonicalize);
            }
        } catch (Throwable e) {
            log.debug("Failed to handle CanonicalizeRequest {}", canonicalizeRequest, e);
            canonicalizeResponse.setError(getErrorMessage(e));
        }

        connection.sendMessage(compilationId, inboundMessage(canonicalizeResponse.build()));
    }

    private void handleFunctionCallRequest(int compilationId, FunctionCallRequest functionCallRequest) throws IOException {
        FunctionCallResponse.Builder response = FunctionCallResponse.newBuilder()
                .setId(functionCallRequest.getId());

        HostFunction sassFunction = null;
        try {
            switch (functionCallRequest.getIdentifierCase()) {
                case NAME:
                    sassFunction = globalFunctions.get(functionCallRequest.getName());
                    break;
                case FUNCTION_ID:
                    throw new UnsupportedOperationException("Calling functions by ID is not supported");
                case IDENTIFIER_NOT_SET:
                    throw new IllegalArgumentException("FunctionCallRequest has no identifier");
            }

            List<Value> argumentsList = functionCallRequest.getArgumentsList();
            Value result = sassFunction.invoke(argumentsList);
            response.setSuccess(result);
        } catch (Throwable e) {
            log.debug("Failed to handle FunctionCallRequest for function {}", sassFunction, e);
            response.setError(getErrorMessage(e));
        }

        connection.sendMessage(compilationId, inboundMessage(response.build()));
    }

    private String getErrorMessage(Throwable t) {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    @Override
    public void close() throws IOException {
        connection.close();
    }
}
