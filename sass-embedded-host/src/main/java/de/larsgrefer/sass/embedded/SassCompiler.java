package de.larsgrefer.sass.embedded;

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

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Lars Grefer
 */
@Slf4j
public class SassCompiler implements AutoCloseable {

    @Getter
    @Setter
    private EmbeddedSass.InboundMessage.CompileRequest.OutputStyle outputStyle = EmbeddedSass.InboundMessage.CompileRequest.OutputStyle.EXPANDED;

    private Process process;

    private final Map<String, HostFunction> globalFunctions = new HashMap<>();
    private final Map<Integer, FileImporter> fileImporters = new HashMap<>();
    private final Map<Integer, CustomImporter> customImporters = new HashMap<>();

    @Setter
    @Getter
    private LoggingHandler loggingHandler = new Slf4jLoggingHandler(log);

    @Getter
    @Setter
    private List<File> loadPaths = new LinkedList<>();

    public SassCompiler(ProcessBuilder processBuilder) throws IOException {
        process = processBuilder
                .redirectInput(ProcessBuilder.Redirect.PIPE)
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .redirectError(ProcessBuilder.Redirect.PIPE)
                .start();
    }

    public SassCompiler(Process process) throws IOException {
        this.process = process;
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

    protected EmbeddedSass.InboundMessage.CompileRequest.Builder compileRequestBuilder() {
        EmbeddedSass.InboundMessage.CompileRequest.Builder builder = EmbeddedSass.InboundMessage.CompileRequest.newBuilder();

        builder.setStyle(outputStyle);

        for (File loadPath : loadPaths) {
            EmbeddedSass.InboundMessage.CompileRequest.Importer importer = EmbeddedSass.InboundMessage.CompileRequest.Importer.newBuilder()
                    .setPath(loadPath.getAbsolutePath())
                    .build();
            builder.addImporters(importer);
        }

        for (Importer value : customImporters.values()) {
            EmbeddedSass.InboundMessage.CompileRequest.Importer importer = EmbeddedSass.InboundMessage.CompileRequest.Importer.newBuilder()
                    .setImporterId(value.getId())
                    .build();
            builder.addImporters(importer);
        }

        for (Importer value : fileImporters.values()) {
            EmbeddedSass.InboundMessage.CompileRequest.Importer importer = EmbeddedSass.InboundMessage.CompileRequest.Importer.newBuilder()
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
        return compileString(source, EmbeddedSass.InboundMessage.Syntax.SCSS);
    }

    public String compileString(String source, EmbeddedSass.InboundMessage.Syntax syntax) throws IOException, SassCompilationFailedException {
        EmbeddedSass.InboundMessage.CompileRequest.StringInput stringInput = EmbeddedSass.InboundMessage.CompileRequest.StringInput.newBuilder()
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

    public EmbeddedSass.OutboundMessage.CompileResponse.CompileSuccess compileString(EmbeddedSass.InboundMessage.CompileRequest.StringInput string, EmbeddedSass.InboundMessage.CompileRequest.OutputStyle outputStyle, boolean generateSourceMaps) throws IOException, SassCompilationFailedException {
        EmbeddedSass.InboundMessage.CompileRequest compileRequest = compileRequestBuilder()
                .setString(string)
                .setStyle(outputStyle != null ? outputStyle : this.outputStyle)
                .setSourceMap(generateSourceMaps)
                .build();

        return execCompileRequest(compileRequest);
    }

    public EmbeddedSass.OutboundMessage.CompileResponse.CompileSuccess compileFile(File file, EmbeddedSass.InboundMessage.CompileRequest.OutputStyle outputStyle, boolean generateSourceMaps) throws IOException, SassCompilationFailedException {
        EmbeddedSass.InboundMessage.CompileRequest compileRequest = compileRequestBuilder()
                .setPath(file.getAbsolutePath())
                .setStyle(outputStyle != null ? outputStyle : this.outputStyle)
                .setSourceMap(generateSourceMaps)
                .build();

        return execCompileRequest(compileRequest);
    }

    private EmbeddedSass.OutboundMessage.CompileResponse.CompileSuccess execCompileRequest(EmbeddedSass.InboundMessage.CompileRequest compileRequest) throws IOException, SassCompilationFailedException {
        EmbeddedSass.InboundMessage inboundMessage = EmbeddedSass.InboundMessage.newBuilder()
                .setCompileRequest(compileRequest)
                .build();

        EmbeddedSass.OutboundMessage outboundMessage = exec(inboundMessage);

        if (!outboundMessage.hasCompileResponse()) {
            throw new IllegalStateException("No compile response");
        }

        EmbeddedSass.OutboundMessage.CompileResponse compileResponse = outboundMessage.getCompileResponse();

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

    private synchronized EmbeddedSass.OutboundMessage exec(EmbeddedSass.InboundMessage inboundMessage) throws IOException {
        sendMessage(inboundMessage);

        while (true) {
            EmbeddedSass.OutboundMessage outboundMessage = EmbeddedSass.OutboundMessage.parseDelimitedFrom(process.getInputStream());

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

    private void sendMessage(EmbeddedSass.InboundMessage inboundMessage) throws IOException {
        if (!process.isAlive()) {
            throw new IllegalStateException("Process is dead");
        }

        OutputStream outputStream = process.getOutputStream();
        inboundMessage.writeDelimitedTo(outputStream);
        outputStream.flush();
    }

    private void handleFileImportRequest(EmbeddedSass.OutboundMessage.FileImportRequest fileImportRequest) throws IOException {
        EmbeddedSass.InboundMessage.FileImportResponse.Builder fileImportResponse = EmbeddedSass.InboundMessage.FileImportResponse.newBuilder()
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

        EmbeddedSass.InboundMessage inboundMessage = EmbeddedSass.InboundMessage.newBuilder()
                .setFileImportResponse(fileImportResponse.build())
                .build();
        sendMessage(inboundMessage);
    }

    private void handleImportRequest(EmbeddedSass.OutboundMessage.ImportRequest importRequest) throws IOException {
        EmbeddedSass.InboundMessage.ImportResponse.Builder importResponse = EmbeddedSass.InboundMessage.ImportResponse.newBuilder()
                .setId(importRequest.getId());

        CustomImporter customImporter = customImporters.get(importRequest.getImporterId());

        try {
            EmbeddedSass.InboundMessage.ImportResponse.ImportSuccess success = customImporter.handleImport(importRequest.getUrl());
            if (success != null) {
                importResponse.setSuccess(success);
            }
        } catch (Throwable t) {
            log.debug("Failed to handle ImportRequest {}", importRequest, t);
            importResponse.setError(getErrorMessage(t));
        }

        EmbeddedSass.InboundMessage inboundMessage = EmbeddedSass.InboundMessage.newBuilder()
                .setImportResponse(importResponse.build())
                .build();

        sendMessage(inboundMessage);
    }

    private void handleCanonicalizeRequest(EmbeddedSass.OutboundMessage.CanonicalizeRequest canonicalizeRequest) throws IOException {
        EmbeddedSass.InboundMessage.CanonicalizeResponse.Builder canonicalizeResponse = EmbeddedSass.InboundMessage.CanonicalizeResponse.newBuilder()
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

        EmbeddedSass.InboundMessage inboundMessage = EmbeddedSass.InboundMessage.newBuilder()
                .setCanonicalizeResponse(canonicalizeResponse.build())
                .build();

        sendMessage(inboundMessage);
    }

    private void handleFunctionCallRequest(EmbeddedSass.OutboundMessage.FunctionCallRequest functionCallRequest) throws IOException {

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

        EmbeddedSass.InboundMessage.FunctionCallResponse.Builder responseBuilder = EmbeddedSass.InboundMessage.FunctionCallResponse.newBuilder();
        responseBuilder.setId(functionCallRequest.getId());

        try {
            EmbeddedSass.Value result = sassFunction.invoke(argumentsList);
            responseBuilder.setSuccess(result);
        } catch (Throwable e) {
            log.debug("Failed to handle FunctionCallRequest for function {}", sassFunction, e);
            responseBuilder.setError(getErrorMessage(e));
        }

        sendMessage(EmbeddedSass.InboundMessage.newBuilder().setFunctionCallResponse(responseBuilder.build()).build());

    }

    private String getErrorMessage(Throwable t) {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    @Override
    public void close() throws Exception {
        process.destroy();
        if (!process.waitFor(2, TimeUnit.SECONDS)) {
            process.destroyForcibly();
        }
    }
}
