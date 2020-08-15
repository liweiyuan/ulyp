package com.ulyp.agent;

import com.ulyp.agent.settings.AgentSettings;
import com.ulyp.agent.settings.SystemPropertiesSettings;
import com.ulyp.agent.settings.UiSettings;
import com.ulyp.agent.transport.UiTransport;
import com.ulyp.core.process.ProcessInfo;

import java.util.concurrent.TimeUnit;

public class AgentContext {

    private static final AgentContext instance = new AgentContext();

    public static AgentContext getInstance() {
        return instance;
    }

    private final SystemPropertiesSettings sysPropsSettings;
    private final UiTransport transport;
    private final UiSettings uiSettings;
    private final ProcessInfo processInfo;

    private AgentContext() {
        this.sysPropsSettings = SystemPropertiesSettings.load();
        this.processInfo = new ProcessInfo();
        this.transport = sysPropsSettings.buildUiTransport();
        this.uiSettings = new UiSettings(transport);

        Thread shutdown = new Thread(
                () -> {
                    try {
                        transport.shutdownNowAndAwaitForRecordsLogsSending(30, TimeUnit.SECONDS);
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

    public UiTransport getTransport() {
        return transport;
    }

    public AgentSettings getSysPropsSettings() {
        return sysPropsSettings;
    }
}
