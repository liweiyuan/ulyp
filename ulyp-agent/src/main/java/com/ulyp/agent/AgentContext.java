package com.ulyp.agent;

import com.ulyp.agent.transport.UploadingTransport;
import com.ulyp.agent.util.ProcessUtils;
import com.ulyp.core.MethodDescriptionDictionary;
import com.ulyp.core.ProcessInfo;

import java.util.concurrent.TimeUnit;

public class AgentContext {

    private final Settings settings;
    private final UploadingTransport transport;
    private final MethodDescriptionDictionary methodDescriptionDictionary;
    private final ProcessInfo processInfo;

    public AgentContext() {
        this.settings = Settings.getInstance();
        this.methodDescriptionDictionary = new MethodDescriptionDictionary();
        this.processInfo = new ProcessInfo(ProcessUtils.getMainClassName());
        this.transport = new UploadingTransport(settings.getUiAddress());

        Thread shutdown = new Thread(
                () -> {
                    try {
                        transport.shutdownNowAndAwaitForTraceLogsSending(5, TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        // ignore
                    }
                }
        );
        Runtime.getRuntime().addShutdownHook(shutdown);
    }

    public ProcessInfo getProcessInfo() {
        return processInfo;
    }

    public UploadingTransport getTransport() {
        return transport;
    }

    public Settings getSettings() {
        return settings;
    }

    public MethodDescriptionDictionary getMethodDescriptionDictionary() {
        return methodDescriptionDictionary;
    }
}
