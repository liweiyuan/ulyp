package com.ulyp.agent;

import com.ulyp.agent.settings.AgentSettings;
import com.ulyp.agent.settings.SystemPropertiesSettings;
import com.ulyp.agent.settings.UiSettings;
import com.ulyp.agent.transport.UploadingTransport;
import com.ulyp.agent.util.ProcessUtils;
import com.ulyp.core.MethodDescriptionDictionary;
import com.ulyp.core.ProcessInfo;

import java.util.concurrent.TimeUnit;

public class AgentContext {

    private static final AgentContext instance = new AgentContext();

    public static AgentContext getInstance() {
        return instance;
    }

    private final SystemPropertiesSettings sysPropsSettings;
    private final UploadingTransport transport;
    private final UiSettings uiSettings;
    private final ProcessInfo processInfo;

    private AgentContext() {
        this.sysPropsSettings = SystemPropertiesSettings.load();
        this.processInfo = new ProcessInfo(ProcessUtils.getMainClassName());
        this.transport = new UploadingTransport(sysPropsSettings.getUiAddress());
        this.uiSettings = new UiSettings(transport);

        Thread shutdown = new Thread(
                () -> {
                    try {
                        transport.shutdownNowAndAwaitForTraceLogsSending(30, TimeUnit.SECONDS);
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

    public UiSettings getUiSettings() {
        return uiSettings;
    }

    public UploadingTransport getTransport() {
        return transport;
    }

    public AgentSettings getSysPropsSettings() {
        return sysPropsSettings;
    }
}
