package com.ulyp.agent.transport;

import com.ulyp.core.CallTraceLog;
import com.ulyp.core.MethodDescriptionDictionary;
import com.ulyp.core.util.ProcessInfo;
import com.ulyp.transport.SettingsResponse;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class DisconnectedTransport implements UploadingTransport {

    private final SettingsResponse settings;

    public DisconnectedTransport(SettingsResponse settings) {
        this.settings = settings;
    }

    @Override
    public SettingsResponse getSettingsBlocking(Duration duration) {
        return settings;
    }

    @Override
    public void uploadAsync(CallTraceLog traceLog, MethodDescriptionDictionary methodDescriptionDictionary, ProcessInfo processInfo) {

    }

    @Override
    public void shutdownNowAndAwaitForTraceLogsSending(long time, TimeUnit timeUnit) throws InterruptedException {

    }
}
