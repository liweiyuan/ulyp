package com.ulyp.core;

import com.ulyp.core.printers.ObjectBinaryPrinter;
import com.ulyp.core.printers.ObjectBinaryPrinterType;
import it.unimi.dsi.fastutil.booleans.BooleanArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import org.agrona.collections.IntArrayList;
import org.agrona.collections.LongArrayList;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;

public class CallRecordLog {
    public static final AtomicLong idGenerator = new AtomicLong(3000000000L);

    private final long recordingSessionId;
    private final long chunkId;
    private final AgentRuntime agentRuntime;
    private final CallEnterRecordList enterRecords = new CallEnterRecordList();
    private final CallExitRecordList exitRecords = new CallExitRecordList();
    private final LongArrayList callIdsStack;
    private final BooleanArrayList recordEnterCallStack;
    private final IntArrayList callCountStack;
    private final long epochMillisCreatedTime;
    private final String threadName;
    private final StackTraceElement[] stackTrace;
    private final int maxDepth;
    private final int maxCallsToRecordPerMethod;

    private boolean inProcessOfTracing = true;
    private long callIdCounter = 0;

    public CallRecordLog(AgentRuntime agentRuntime, int maxDepth, int maxCallsToRecordPerMethod) {
        this.chunkId = 0;
        this.callIdsStack = new LongArrayList();
        this.recordEnterCallStack = new BooleanArrayList();
        this.callCountStack = new IntArrayList();
        this.recordingSessionId = idGenerator.incrementAndGet();
        this.epochMillisCreatedTime = System.currentTimeMillis();
        this.maxDepth = maxDepth;
        this.maxCallsToRecordPerMethod = maxCallsToRecordPerMethod;
        this.agentRuntime = agentRuntime;
        callCountStack.addInt(0);

        StackTraceElement[] wholeStackTrace = new Exception().getStackTrace();

        // filter out CallRecordLog.<init>/Recorder.lambda$startOrContinueRecording$2/EnhancedThreadLocal.getOrCreate/Recorder.startOrContinueRecording
        // from stack trace
        // If code changed, there should be a readjustement, but don't worry as this is tested
        this.stackTrace = Arrays.copyOfRange(wholeStackTrace, 5, wholeStackTrace.length);
        this.threadName = Thread.currentThread().getName();
    }

    private CallRecordLog(
            long chunkId,
            LongArrayList callIdsStack,
            BooleanArrayList recordEnterCallStack,
            IntArrayList callCountStack,
            long recordingSessionId,
            AgentRuntime agentRuntime,
            long epochMillisCreatedTime,
            String threadName,
            StackTraceElement[] stackTrace,
            int maxDepth,
            int maxCallsToRecordPerMethod,
            boolean inProcessOfTracing,
            long callIdCounter)
    {
        this.chunkId = chunkId;
        this.callIdsStack = callIdsStack;
        this.callCountStack = callCountStack;
        this.recordEnterCallStack = recordEnterCallStack;
        this.recordingSessionId = recordingSessionId;
        this.agentRuntime = agentRuntime;
        this.epochMillisCreatedTime = epochMillisCreatedTime;
        this.threadName = threadName;
        this.stackTrace = stackTrace;
        this.maxDepth = maxDepth;
        this.maxCallsToRecordPerMethod = maxCallsToRecordPerMethod;
        this.inProcessOfTracing = inProcessOfTracing;
        this.callIdCounter = callIdCounter;
    }

    public CallRecordLog cloneWithoutData() {
        return new CallRecordLog(this.chunkId + 1, this.callIdsStack, this.recordEnterCallStack, this.callCountStack, this.recordingSessionId, this.agentRuntime, this.epochMillisCreatedTime, this.threadName, this.stackTrace, this.maxDepth, this.maxCallsToRecordPerMethod, this.inProcessOfTracing, this.callIdCounter);
    }

    public long estimateBytesSize() {
        return enterRecords.buffer.capacity() + exitRecords.buffer.capacity();
    }

    public void onMethodEnter(int methodId, ObjectBinaryPrinter[] printers, Object callee, Object[] args) {
        if (!inProcessOfTracing) {
            return;
        }

        inProcessOfTracing = false;
        try {
            int callsMadeInCurrentMethod = callCountStack.getInt(callCountStack.size() - 1);

            long callId = callIdCounter++;
            boolean canRecord = callIdsStack.size() <= maxDepth && callsMadeInCurrentMethod < maxCallsToRecordPerMethod;
            pushCurrentMethodCallId(callId, canRecord);

            if (canRecord) {
                enterRecords.add(callId, methodId, agentRuntime, printers, callee, args);
                callCountStack.setInt(callCountStack.size() - 2, callsMadeInCurrentMethod + 1);
            }
        } finally {
            inProcessOfTracing = true;
        }
    }

    public void onMethodExit(int methodId, ObjectBinaryPrinter resultPrinter, Object returnValue, Throwable thrown) {
        if (!inProcessOfTracing) {
            return;
        }

        inProcessOfTracing = false;
        try {
            boolean recordedEnterCall = recordEnterCallStack.popBoolean();
            long callId = popCurrentCallId();

            if (recordedEnterCall && callId >= 0) {
                if (thrown == null) {
                    exitRecords.add(callId, methodId, agentRuntime, false, resultPrinter, returnValue);
                } else {
                    exitRecords.add(callId, methodId, agentRuntime, true, ObjectBinaryPrinterType.THROWABLE_PRINTER.getInstance(), thrown);
                }
            }
        } finally {
            inProcessOfTracing = true;
        }
    }

    public boolean isComplete() {
        return callIdsStack.isEmpty();
    }

    public long size() {
        return enterRecords.size();
    }

    private void pushCurrentMethodCallId(long callId, boolean canRecord) {
        callIdsStack.pushLong(callId);
        /*
        * If current method call is not recorded, then children (i.e. calls within this method) should be recorded as well, otherwise
        * the tree will lose it's form. We prohibit it by setting number of calls already made to maximum.
        */
        callCountStack.pushInt(canRecord ? 0 : Integer.MAX_VALUE);
        recordEnterCallStack.push(canRecord);
    }

    private long popCurrentCallId() {
        if (!callIdsStack.isEmpty()) {
            callCountStack.popInt();
            return callIdsStack.popLong();
        } else {
            System.err.println("Inconsistency found, no method stamp in stack");
            return -1;
        }
    }

    public long getChunkId() {
        return chunkId;
    }

    public String getThreadName() {
        return threadName;
    }

    public StackTraceElement[] getStackTrace() {
        return stackTrace;
    }

    public CallEnterRecordList getEnterRecords() {
        return enterRecords;
    }

    public CallExitRecordList getExitRecords() {
        return exitRecords;
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
                "id=" + recordingSessionId +
                ", calls=" + callIdsStack.size() +
                '}';
    }
}
