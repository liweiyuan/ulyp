package com.ulyp.core;

import com.ulyp.core.printers.ObjectBinaryPrinter;
import com.ulyp.core.printers.ObjectBinaryPrinterType;
import com.ulyp.database.Database;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;

public class CallRecordLog {

    public static final AtomicLong idGenerator = new AtomicLong(3000000000L);

    private final Database.Writer dbWriter;
    private final long recordingSessionId;
    private final AgentRuntime agentRuntime;
    private final long epochMillisCreatedTime = System.currentTimeMillis();
    private final String threadName;
    private final long threadId;
    private final StackTraceElement[] stackTrace;
    private final int maxDepth;
    private final int maxCallsToRecordPerMethod;

    private boolean inProcessOfTracing = true;
    private long lastExitCallId = -1;
    private long callIdCounter = 0;

    public CallRecordLog(Database.Writer dbWriter, AgentRuntime agentRuntime, int maxDepth, int maxCallsToRecordPerMethod) {
        this.dbWriter = dbWriter;
        this.recordingSessionId = idGenerator.incrementAndGet();
        this.maxDepth = maxDepth;
        this.maxCallsToRecordPerMethod = maxCallsToRecordPerMethod;
        this.agentRuntime = agentRuntime;

        StackTraceElement[] wholeStackTrace = new Exception().getStackTrace();

        // If code changed, there should be a readjustement
        this.stackTrace = Arrays.copyOfRange(wholeStackTrace, 4, wholeStackTrace.length);
        this.threadName = Thread.currentThread().getName();
        this.threadId = Thread.currentThread().getId();
    }

    private CallRecordLog(
            Database.Writer dbWriter,
            long recordingSessionId,
            AgentRuntime agentRuntime,
            String threadName,
            long threadId,
            StackTraceElement[] stackTrace,
            int maxDepth,
            int maxCallsToRecordPerMethod,
            boolean inProcessOfTracing,
            long callIdCounter)
    {
        this.dbWriter = dbWriter;
        this.recordingSessionId = recordingSessionId;
        this.agentRuntime = agentRuntime;
        this.threadName = threadName;
        this.threadId = threadId;
        this.stackTrace = stackTrace;
        this.maxDepth = maxDepth;
        this.maxCallsToRecordPerMethod = maxCallsToRecordPerMethod;
        this.inProcessOfTracing = inProcessOfTracing;
        this.callIdCounter = callIdCounter;
    }

    public CallRecordLog cloneWithoutData() {
        return new CallRecordLog(this.dbWriter, this.recordingSessionId, this.agentRuntime, this.threadName, this.threadId, this.stackTrace, this.maxDepth, this.maxCallsToRecordPerMethod, this.inProcessOfTracing, this.callIdCounter);
    }

    public long onMethodEnter(int methodId, ObjectBinaryPrinter[] printers, @Nullable Object callee, Object[] args) {
        if (!inProcessOfTracing) {
            return -1;
        }
        inProcessOfTracing = false;
        try {

            long callId = callIdCounter++;
            dbWriter.writeEnterRecord(recordingSessionId, callId, methodId, agentRuntime, printers, callee, args);
            return callId;
        } finally {
            inProcessOfTracing = true;
        }
    }

    public void onMethodExit(int methodId, ObjectBinaryPrinter resultPrinter, Object returnValue, Throwable thrown, long callId) {
        if (!inProcessOfTracing) {
            return;
        }

        inProcessOfTracing = false;
        try {
            if (callId >= 0) {
                if (thrown == null) {
                    dbWriter.writeExitRecord(recordingSessionId, callId, methodId, agentRuntime, false, resultPrinter, returnValue);
                } else {
                    dbWriter.writeExitRecord(recordingSessionId, callId, methodId, agentRuntime, true, ObjectBinaryPrinterType.THROWABLE_PRINTER.getInstance(), thrown);
                }
                lastExitCallId = callId;
            }
        } finally {
            inProcessOfTracing = true;
        }
    }

    public boolean isComplete() {
        return lastExitCallId == 0;
    }

    public String getThreadName() {
        return threadName;
    }

    public long getThreadId() {
        return threadId;
    }

    public StackTraceElement[] getStackTrace() {
        return stackTrace;
    }

    public long getRecordingSessionId() {
        return recordingSessionId;
    }

    public long getEpochMillisCreatedTime() {
        return epochMillisCreatedTime;
    }

    @Override
    public String toString() {
        return "CallRecordLog{" +
                "id=" + recordingSessionId + '}';
    }
}
