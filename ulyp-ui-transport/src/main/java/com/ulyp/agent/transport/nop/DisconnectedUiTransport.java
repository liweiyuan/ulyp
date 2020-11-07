package com.ulyp.agent.transport.nop;

import com.ulyp.agent.transport.CallRecordTreeRequest;
import com.ulyp.agent.transport.UiTransport;
import com.ulyp.transport.Settings;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Used
 */
public class DisconnectedUiTransport implements UiTransport {

    private final Settings settings;

    public DisconnectedUiTransport(Settings settingsResponse) {
        this.settings = settingsResponse;
    }

    @Override
    public Settings getSettingsBlocking(Duration duration) {
        return settings;
    }

    @Override
    public void uploadAsync(CallRecordTreeRequest request) {
        // NOP
        System.out.println("Won't send " + request.getRecordLog().size() + " records");
    }

    @Override
    public void shutdownNowAndAwaitForRecordsLogsSending(long time, TimeUnit timeUnit) throws InterruptedException {

        // NOP
    }
}
