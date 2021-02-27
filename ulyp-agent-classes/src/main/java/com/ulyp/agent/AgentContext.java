package com.ulyp.agent;

import com.ulyp.agent.settings.SystemPropertiesSettings;
import com.ulyp.core.process.ProcessInfo;
import com.ulyp.database.Database;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class AgentContext {

    private static final AgentContext instance;

    static {
        try {
            instance = new AgentContext();
        } catch (IOException e) {
            // TODO proper message
            throw new RuntimeException(e);
        }
    }

    private static boolean agentLoaded = false;

    public static synchronized boolean isLoaded() {
        return agentLoaded;
    }

    public static synchronized void load() {
        agentLoaded = true;
    }

    public static AgentContext getInstance() {
        return instance;
    }

    private final SystemPropertiesSettings sysPropsSettings;
    private final Database.Writer dbWriter;
    private final ProcessInfo processInfo;

    private AgentContext() throws IOException {
        this.sysPropsSettings = SystemPropertiesSettings.load();
        this.processInfo = new ProcessInfo();
        this.dbWriter = sysPropsSettings.buildDbWriter();

        Thread shutdown = new Thread(
                () -> {
                    dbWriter.shutdownNowAndAwaitForRecordsLogsSending(30, TimeUnit.SECONDS);
                }
        );
        Runtime.getRuntime().addShutdownHook(shutdown);
    }

    public ProcessInfo getProcessInfo() {
        return processInfo;
    }

    public Database.Writer getDbWriter() {
        return dbWriter;
    }

    public SystemPropertiesSettings getSysPropsSettings() {
        return sysPropsSettings;
    }
}
