package com.ulyp.agent.transport;

import com.ulyp.core.CallRecordLog;
import com.ulyp.core.MethodDescriptionDictionary;
import com.ulyp.core.process.ProcessInfo;
import com.ulyp.transport.SettingsResponse;

import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public interface UiTransport {

    SettingsResponse getSettingsBlocking(Duration duration) throws InterruptedException, ExecutionException, TimeoutException;

    void uploadAsync(CallRecordLog recordLog, MethodDescriptionDictionary methodDescriptionDictionary, ProcessInfo processInfo);

    void shutdownNowAndAwaitForRecordsLogsSending(long time, TimeUnit timeUnit) throws InterruptedException;
}
