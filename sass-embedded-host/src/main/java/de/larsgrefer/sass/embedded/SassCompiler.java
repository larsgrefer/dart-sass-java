package de.larsgrefer.sass.embedded;

import de.larsgrefer.sass.embedded.functions.HostFunction;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import sass.embedded_protocol.EmbeddedSass;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public class SassCompiler implements AutoCloseable {

    @Getter
    @Setter
    private EmbeddedSass.InboundMessage.CompileRequest.OutputStyle outputStyle = EmbeddedSass.InboundMessage.CompileRequest.OutputStyle.EXPANDED;

    private Process process;

    private final Map<String, HostFunction> globalFunctions = new HashMap<>();

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

    protected EmbeddedSass.InboundMessage.CompileRequest.Builder compileRequestBuilder() {
        EmbeddedSass.InboundMessage.CompileRequest.Builder builder = EmbeddedSass.InboundMessage.CompileRequest.newBuilder();

        builder.setStyle(outputStyle);

        for (HostFunction sassFunction : globalFunctions.values()) {
            builder.addGlobalFunctions(sassFunction.getSignature());
        }

        return builder;
    }

    public String compileString(String sass) throws Exception {

        EmbeddedSass.InboundMessage.CompileRequest.StringInput stringInput = EmbeddedSass.InboundMessage.CompileRequest.StringInput.newBuilder()
                .setSource(sass)
                .build();

        EmbeddedSass.InboundMessage.CompileRequest compileRequest = compileRequestBuilder()
                .setString(stringInput)
                .build();

        return execCompileRequest(compileRequest).getCss();
    }

    public void compileString(String sass, File outputFile) throws Exception {

        String css = compileString(sass);

        Files.write(outputFile.toPath(), css.getBytes(StandardCharsets.UTF_8));

    }

    public String compileFile(File inputFile) throws IOException, SassCompilationFailedException {

        EmbeddedSass.InboundMessage.CompileRequest compileRequest = compileRequestBuilder()
                .setPath(inputFile.getCanonicalPath())
                .build();

        return execCompileRequest(compileRequest).getCss();
    }

    public void compileFile(File inputFile, File outputFile) throws IOException, SassCompilationFailedException {
        String css = compileFile(inputFile);

        Files.write(outputFile.toPath(), css.getBytes(StandardCharsets.UTF_8));
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

            if (outboundMessage.hasError()) {
                EmbeddedSass.ProtocolError error = outboundMessage.getError();
                throw new SassProtocolErrorException(error);
            }
            else if (outboundMessage.hasLogEvent()) {
                handleLogEvent(outboundMessage.getLogEvent());
            }
            else if (outboundMessage.hasFunctionCallRequest()) {
                handle(outboundMessage.getFunctionCallRequest());
            }
            else {
                return outboundMessage;
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

    private void handle(EmbeddedSass.OutboundMessage.FunctionCallRequest functionCallRequest) throws IOException {

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
            log.error(e.getLocalizedMessage(), e);
            responseBuilder.setError(e.getMessage());
        }

        sendMessage(EmbeddedSass.InboundMessage.newBuilder().setFunctionCallResponse(responseBuilder.build()).build());

    }

    private void handleLogEvent(EmbeddedSass.OutboundMessage.LogEvent logEvent) {
        EmbeddedSass.OutboundMessage.LogEvent.Type type = logEvent.getType();

        switch (type) {

            case WARNING:
                log.warn(logEvent.getMessage());
                break;
            case DEPRECATION_WARNING:
                log.warn(logEvent.getMessage());
                break;
            case DEBUG:
                log.debug(logEvent.getMessage());
                break;
            case UNRECOGNIZED:
                break;
        }
    }

    @Override
    public void close() throws Exception {
        process.destroy();
        if (!process.waitFor(1, TimeUnit.SECONDS)) {
            process.destroyForcibly();
        }

        process = null;
    }
}
