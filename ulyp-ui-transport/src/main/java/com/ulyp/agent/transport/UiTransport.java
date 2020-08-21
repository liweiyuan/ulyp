package com.ulyp.agent.transport;

import com.ulyp.transport.Settings;

import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public interface UiTransport {

    Settings getSettingsBlocking(Duration duration) throws InterruptedException, ExecutionException, TimeoutException;

    void uploadAsync(CallRecordTreeRequest request);

    void shutdownNowAndAwaitForRecordsLogsSending(long time, TimeUnit timeUnit) throws InterruptedException;
}
