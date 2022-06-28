package de.larsgrefer.sass.embedded.util;

import lombok.experimental.UtilityClass;
import sass.embedded_protocol.EmbeddedSass;
import sass.embedded_protocol.EmbeddedSass.InboundMessage;
import sass.embedded_protocol.EmbeddedSass.InboundMessage.*;
import sass.embedded_protocol.EmbeddedSass.Value;

/**
 * @author Lars Grefer
 */
@UtilityClass
public class ProtocolUtil {

    public static InboundMessage inboundMessage(VersionRequest versionRequest) {
        return InboundMessage.newBuilder()
                .setVersionRequest(versionRequest)
                .build();
    }

    public static InboundMessage inboundMessage(CompileRequest compileRequest) {
        return InboundMessage.newBuilder()
                .setCompileRequest(compileRequest)
                .build();
    }

    public static InboundMessage inboundMessage(FileImportResponse fileImportResponse) {
        return InboundMessage.newBuilder()
                .setFileImportResponse(fileImportResponse)
                .build();
    }

    public static InboundMessage inboundMessage(ImportResponse importResponse) {
        return InboundMessage.newBuilder()
                .setImportResponse(importResponse)
                .build();
    }

    public static InboundMessage inboundMessage(CanonicalizeResponse canonicalizeResponse) {
        return InboundMessage.newBuilder()
                .setCanonicalizeResponse(canonicalizeResponse)
                .build();
    }

    public static InboundMessage inboundMessage(FunctionCallResponse functionCallResponse) {
        return InboundMessage.newBuilder()
                .setFunctionCallResponse(functionCallResponse)
                .build();
    }

    public static Value value(Value.String string) {
        return Value.newBuilder()
                .setString(string)
                .build();
    }

    public static Value value(Value.Number number) {
        return Value.newBuilder()
                .setNumber(number)
                .build();
    }

    public static Value value(Value.RgbColor rgbColor) {
        return Value.newBuilder()
                .setRgbColor(rgbColor)
                .build();
    }

    public static Value value(Value.HslColor hslColor) {
        return Value.newBuilder()
                .setHslColor(hslColor)
                .build();
    }

    public static Value value(Value.List list) {
        return Value.newBuilder()
                .setList(list)
                .build();
    }

    public static Value value(Value.Map map) {
        return Value.newBuilder()
                .setMap(map)
                .build();
    }

    public static Value value(EmbeddedSass.SingletonValue singletonValue) {
        return Value.newBuilder()
                .setSingleton(singletonValue)
                .build();
    }

    public static Value value(Value.CompilerFunction compilerFunction) {
        return Value.newBuilder()
                .setCompilerFunction(compilerFunction)
                .build();
    }

    public static Value value(Value.HostFunction hostFunction) {
        return Value.newBuilder()
                .setHostFunction(hostFunction)
                .build();
    }

    public static Value value(Value.ArgumentList argumentList) {
        return Value.newBuilder()
                .setArgumentList(argumentList)
                .build();
    }

    public static Value value(Value.HwbColor hwbColor) {
        return Value.newBuilder()
                .setHwbColor(hwbColor)
                .build();
    }

    public static Value value(Value.Calculation calculation) {
        return Value.newBuilder()
                .setCalculation(calculation)
                .build();
    }
}
