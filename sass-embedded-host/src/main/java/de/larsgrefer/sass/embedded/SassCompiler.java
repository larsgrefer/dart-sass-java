package de.larsgrefer.sass.embedded;

import com.google.protobuf.ByteString;
import de.larsgrefer.sass.embedded.connection.CompilerConnection;
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
import sass.embedded_protocol.EmbeddedSass;
import sass.embedded_protocol.EmbeddedSass.InboundMessage;
import sass.embedded_protocol.EmbeddedSass.InboundMessage.CanonicalizeResponse;
import sass.embedded_protocol.EmbeddedSass.InboundMessage.CompileRequest;
import sass.embedded_protocol.EmbeddedSass.InboundMessage.FunctionCallResponse;
import sass.embedded_protocol.EmbeddedSass.OutboundMessage;
import sass.embedded_protocol.EmbeddedSass.OutboundMessage.CanonicalizeRequestOrBuilder;
import sass.embedded_protocol.EmbeddedSass.OutboundMessage.CompileResponse.CompileSuccess;
import sass.embedded_protocol.EmbeddedSass.OutboundMessage.FileImportRequestOrBuilder;
import sass.embedded_protocol.EmbeddedSass.OutboundMessage.FunctionCallRequestOrBuilder;
import sass.embedded_protocol.EmbeddedSass.OutboundMessage.ImportRequestOrBuilder;
import sass.embedded_protocol.EmbeddedSass.OutputStyle;
import sass.embedded_protocol.EmbeddedSass.Syntax;

import javax.annotation.Nonnull;
import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Lars Grefer
 * @see SassCompilerFactory
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

    private final CompilerConnection connection;

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
        InboundMessage inboundMessage = InboundMessage.newBuilder()
                .setVersionRequest(InboundMessage.VersionRequest.newBuilder().build())
                .build();

        return exec(inboundMessage).getVersionResponse();
    }

    public void registerFunction(HostFunction sassFunction) {
        globalFunctions.put(sassFunction.getName(), sassFunction);
    }

    public void registerImporter(FileImporter fileImporter) {
        fileImporters.put(fileImporter.getId(), fileImporter);
    }

    public void registerImporter(CustomImporter customImporter) {
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

        return builder;
    }

    public CompileSuccess compile(@NonNull URL source) throws SassCompilationFailedException, IOException {
        Syntax syntax = SyntaxUtil.guessSyntax(source);

        if (source.getProtocol().equals("file")) {
            File file = new File(source.getPath());
            return compileFile(file);
        }

        ByteString content;
        try (InputStream in = source.openStream()) {
            content = ByteString.readFrom(in);
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
            return compileString(build, getOutputStyle());
        } finally {
            customImporters.remove(importer.getId());
        }
    }

    //region compileString and overloads
    public CompileSuccess compileScssString(String source) throws IOException, SassCompilationFailedException {
        return compileString(source, Syntax.SCSS);
    }

    public CompileSuccess compileSassString(String source) throws IOException, SassCompilationFailedException {
        return compileString(source, Syntax.INDENTED);
    }

    public CompileSuccess compileCssString(String source) throws IOException, SassCompilationFailedException {
        return compileString(source, Syntax.CSS);
    }

    public CompileSuccess compileString(String source, Syntax syntax) throws IOException, SassCompilationFailedException {
        CompileRequest.StringInput stringInput = CompileRequest.StringInput.newBuilder()
                .setSource(source)
                .setSyntax(syntax)
                .build();

        return compileString(stringInput, getOutputStyle());
    }

    @Nonnull
    public CompileSuccess compileString(CompileRequest.StringInput string, OutputStyle outputStyle) throws IOException, SassCompilationFailedException {
        if (outputStyle == null) {
            throw new IllegalArgumentException("outputStyle must not be null");
        }

        CompileRequest compileRequest = compileRequestBuilder()
                .setString(string)
                .setStyle(outputStyle)
                .build();

        return execCompileRequest(compileRequest);
    }
    //endregion

    //region compileFile

    public CompileSuccess compileFile(File inputFile) throws IOException, SassCompilationFailedException {
        return compileFile(inputFile, getOutputStyle());
    }

    public CompileSuccess compileFile(File file, OutputStyle outputStyle) throws IOException, SassCompilationFailedException {
        CompileRequest compileRequest = compileRequestBuilder()
                .setPath(file.getPath())
                .setStyle(outputStyle)
                .build();

        return execCompileRequest(compileRequest);
    }

    //endregion

    private CompileSuccess execCompileRequest(CompileRequest compileRequest) throws IOException, SassCompilationFailedException {
        InboundMessage inboundMessage = InboundMessage.newBuilder()
                .setCompileRequest(compileRequest)
                .build();

        OutboundMessage outboundMessage = exec(inboundMessage);

        if (!outboundMessage.hasCompileResponse()) {
            throw new IllegalStateException("No compile response");
        }

        OutboundMessage.CompileResponse compileResponse = outboundMessage.getCompileResponse();

        if (compileResponse.hasSuccess()) {
            return compileResponse.getSuccess();
        }
        else if (compileResponse.hasFailure()) {
            throw new SassCompilationFailedException(compileResponse.getFailure());
        }
        else {
            throw new IllegalStateException("Neither success nor failure");
        }
    }

    private synchronized OutboundMessage exec(InboundMessage inboundMessage) throws IOException {
        connection.sendMessage(inboundMessage);

        while (true) {
            OutboundMessage outboundMessage = connection.readResponse();

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
                    handleCanonicalizeRequest(outboundMessage.getCanonicalizeRequest());
                    break;
                case IMPORT_REQUEST:
                    handleImportRequest(outboundMessage.getImportRequest());
                    break;
                case FILE_IMPORT_REQUEST:
                    handleFileImportRequest(outboundMessage.getFileImportRequest());
                    break;
                case FUNCTION_CALL_REQUEST:
                    handleFunctionCallRequest(outboundMessage.getFunctionCallRequest());
                    break;
                case MESSAGE_NOT_SET:
                    throw new IllegalStateException("No message set");
                default:
                    throw new IllegalStateException("Unknown OutboundMessage: " + outboundMessage.getMessageCase());
            }
        }
    }

    private void handleFileImportRequest(FileImportRequestOrBuilder fileImportRequest) throws IOException {
        InboundMessage.FileImportResponse.Builder fileImportResponse = InboundMessage.FileImportResponse.newBuilder()
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

        InboundMessage inboundMessage = InboundMessage.newBuilder()
                .setFileImportResponse(fileImportResponse.build())
                .build();
        connection.sendMessage(inboundMessage);
    }

    private void handleImportRequest(ImportRequestOrBuilder importRequest) throws IOException {
        InboundMessage.ImportResponse.Builder importResponse = InboundMessage.ImportResponse.newBuilder()
                .setId(importRequest.getId());

        CustomImporter customImporter = customImporters.get(importRequest.getImporterId());

        try {
            InboundMessage.ImportResponse.ImportSuccess success = customImporter.handleImport(importRequest.getUrl());
            if (success != null) {
                importResponse.setSuccess(success);
            }
        } catch (Throwable t) {
            log.debug("Failed to handle ImportRequest {}", importRequest, t);
            importResponse.setError(getErrorMessage(t));
        }

        InboundMessage inboundMessage = InboundMessage.newBuilder()
                .setImportResponse(importResponse.build())
                .build();

        connection.sendMessage(inboundMessage);
    }

    private void handleCanonicalizeRequest(CanonicalizeRequestOrBuilder canonicalizeRequest) throws IOException {
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

        InboundMessage inboundMessage = InboundMessage.newBuilder()
                .setCanonicalizeResponse(canonicalizeResponse.build())
                .build();

        connection.sendMessage(inboundMessage);
    }

    private void handleFunctionCallRequest(FunctionCallRequestOrBuilder functionCallRequest) throws IOException {

        HostFunction sassFunction = null;

        switch (functionCallRequest.getIdentifierCase()) {

            case NAME:
                sassFunction = globalFunctions.get(functionCallRequest.getName());
                break;
            case FUNCTION_ID:
                throw new UnsupportedOperationException();
            case IDENTIFIER_NOT_SET:
                throw new IllegalArgumentException("FunctionCallRequest has no identifier");
        }

        List<EmbeddedSass.Value> argumentsList = functionCallRequest.getArgumentsList();

        FunctionCallResponse.Builder responseBuilder = FunctionCallResponse.newBuilder();
        responseBuilder.setId(functionCallRequest.getId());

        try {
            EmbeddedSass.Value result = sassFunction.invoke(argumentsList);
            responseBuilder.setSuccess(result);
        } catch (Throwable e) {
            log.debug("Failed to handle FunctionCallRequest for function {}", sassFunction, e);
            responseBuilder.setError(getErrorMessage(e));
        }

        connection.sendMessage(InboundMessage.newBuilder().setFunctionCallResponse(responseBuilder.build()).build());

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
