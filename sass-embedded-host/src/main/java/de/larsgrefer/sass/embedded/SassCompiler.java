package de.larsgrefer.sass.embedded;

import de.larsgrefer.sass.embedded.connection.CompilerConnection;
import de.larsgrefer.sass.embedded.functions.HostFunction;
import de.larsgrefer.sass.embedded.importer.CustomImporter;
import de.larsgrefer.sass.embedded.importer.FileImporter;
import de.larsgrefer.sass.embedded.importer.Importer;
import de.larsgrefer.sass.embedded.logging.LoggingHandler;
import de.larsgrefer.sass.embedded.logging.Slf4jLoggingHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import sass.embedded_protocol.EmbeddedSass;
import sass.embedded_protocol.EmbeddedSass.InboundMessage;
import sass.embedded_protocol.EmbeddedSass.InboundMessage.CanonicalizeResponse;
import sass.embedded_protocol.EmbeddedSass.InboundMessage.CompileRequest;
import sass.embedded_protocol.EmbeddedSass.InboundMessage.CompileRequest.OutputStyle;
import sass.embedded_protocol.EmbeddedSass.InboundMessage.FunctionCallResponse;
import sass.embedded_protocol.EmbeddedSass.OutboundMessage;
import sass.embedded_protocol.EmbeddedSass.OutboundMessage.CanonicalizeRequest;
import sass.embedded_protocol.EmbeddedSass.OutboundMessage.CompileResponse.CompileSuccess;
import sass.embedded_protocol.EmbeddedSass.OutboundMessage.FileImportRequest;
import sass.embedded_protocol.EmbeddedSass.OutboundMessage.FunctionCallRequest;
import sass.embedded_protocol.EmbeddedSass.OutboundMessage.ImportRequest;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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

    @Getter
    @Setter
    private OutputStyle outputStyle = OutputStyle.EXPANDED;

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

    public String compileString(String source) throws IOException, SassCompilationFailedException {
        return compileString(source, InboundMessage.Syntax.SCSS);
    }

    public String compileString(String source, InboundMessage.Syntax syntax) throws IOException, SassCompilationFailedException {
        CompileRequest.StringInput stringInput = CompileRequest.StringInput.newBuilder()
                .setSource(source)
                .setSyntax(syntax)
                .build();

        return compileString(stringInput, null, false).getCss();
    }

    public void compileString(String sass, File outputFile) throws Exception {

        String css = compileString(sass);

        Files.write(outputFile.toPath(), css.getBytes(StandardCharsets.UTF_8));
    }

    public String compileFile(File inputFile) throws IOException, SassCompilationFailedException {
        return compileFile(inputFile, getOutputStyle(), false).getCss();
    }

    public CompileSuccess compileString(CompileRequest.StringInput string, OutputStyle outputStyle, boolean generateSourceMaps) throws IOException, SassCompilationFailedException {
        CompileRequest compileRequest = compileRequestBuilder()
                .setString(string)
                .setStyle(outputStyle != null ? outputStyle : this.outputStyle)
                .setSourceMap(generateSourceMaps)
                .build();

        return execCompileRequest(compileRequest);
    }

    public CompileSuccess compileFile(File file, OutputStyle outputStyle, boolean generateSourceMaps) throws IOException, SassCompilationFailedException {
        CompileRequest compileRequest = compileRequestBuilder()
                .setPath(file.getPath())
                .setStyle(outputStyle != null ? outputStyle : this.outputStyle)
                .setSourceMap(generateSourceMaps)
                .build();

        return execCompileRequest(compileRequest);
    }

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
                case COMPILERESPONSE:
                case VERSIONRESPONSE:
                    return outboundMessage;
                case LOGEVENT:
                    loggingHandler.handle(outboundMessage.getLogEvent());
                    break;
                case CANONICALIZEREQUEST:
                    handleCanonicalizeRequest(outboundMessage.getCanonicalizeRequest());
                    break;
                case IMPORTREQUEST:
                    handleImportRequest(outboundMessage.getImportRequest());
                    break;
                case FILEIMPORTREQUEST:
                    handleFileImportRequest(outboundMessage.getFileImportRequest());
                    break;
                case FUNCTIONCALLREQUEST:
                    handleFunctionCallRequest(outboundMessage.getFunctionCallRequest());
                    break;
                case MESSAGE_NOT_SET:
                    throw new IllegalStateException("No message set");
                default:
                    throw new IllegalStateException("Unknown OutboundMessage: " + outboundMessage.getMessageCase());
            }
        }
    }

    private void handleFileImportRequest(FileImportRequest fileImportRequest) throws IOException {
        InboundMessage.FileImportResponse.Builder fileImportResponse = InboundMessage.FileImportResponse.newBuilder()
                .setId(fileImportRequest.getId());

        FileImporter fileImporter = fileImporters.get(fileImportRequest.getImporterId());

        try {
            File file = fileImporter.handleImport(fileImportRequest.getUrl());
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

    private void handleImportRequest(ImportRequest importRequest) throws IOException {
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

    private void handleCanonicalizeRequest(CanonicalizeRequest canonicalizeRequest) throws IOException {
        CanonicalizeResponse.Builder canonicalizeResponse = CanonicalizeResponse.newBuilder()
                .setId(canonicalizeRequest.getId());

        CustomImporter customImporter = customImporters.get(canonicalizeRequest.getImporterId());

        try {
            String canonicalize = customImporter.canonicalize(canonicalizeRequest.getUrl());
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

    private void handleFunctionCallRequest(FunctionCallRequest functionCallRequest) throws IOException {

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
