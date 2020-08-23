package com.ulyp.core;

import com.ulyp.core.printers.ObjectBinaryPrinter;
import com.ulyp.core.printers.ObjectBinaryPrinterType;
import it.unimi.dsi.fastutil.booleans.BooleanArrayList;
import org.agrona.collections.IntArrayList;

import java.util.concurrent.atomic.AtomicLong;

public class CallRecordLog {
    public static final AtomicLong idGenerator = new AtomicLong(3000000000L);

    private final long id = idGenerator.incrementAndGet();

    private final AgentRuntime agentRuntime;
    private final CallEnterRecordList enterRecords = new CallEnterRecordList();
    private final CallExitRecordList exitRecords = new CallExitRecordList();
    private final IntArrayList callIdsStack = new IntArrayList();
    private final BooleanArrayList recordEnterCallStack = new BooleanArrayList();
    private final IntArrayList callCountStack = new IntArrayList();

    private final long epochMillisCreatedTime;
    private final String threadName;
    private final int maxDepth;
    // TODO name?
    private final int maxCallsPerDepth;

    private boolean inProcessOfTracing = true;
    private int callIdCounter = 0;

    public CallRecordLog(AgentRuntime agentRuntime, int maxDepth, int maxCallsPerDepth) {
        this.epochMillisCreatedTime = System.currentTimeMillis();
        this.maxDepth = maxDepth;
        this.maxCallsPerDepth = maxCallsPerDepth;
        this.agentRuntime = agentRuntime;
        callCountStack.addInt(0);
        this.threadName = Thread.currentThread().getName();
    }

    public void onMethodEnter(int methodId, ObjectBinaryPrinter[] printers, Object callee, Object[] args) {
        if (!inProcessOfTracing) {
            return;
        }

        inProcessOfTracing = false;
        try {
            int callsMadeInCurrentMethod = callCountStack.getInt(callCountStack.size() - 1);

            int callId = callIdCounter++;
            boolean canRecord = callIdsStack.size() <= maxDepth && callsMadeInCurrentMethod < maxCallsPerDepth;
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
            int callId = popCurrentCallId();

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

    private void pushCurrentMethodCallId(int callId, boolean canRecord) {
        callIdsStack.pushInt(callId);
        /*
        * If current method call is not recorded, then children (i.e. calls within this method) should be recorded as well, otherwise
        * the tree will lose it's form. We prohibit it by setting number of calls already made to maximum.
        */
        callCountStack.pushInt(canRecord ? 0 : Integer.MAX_VALUE);
        recordEnterCallStack.push(canRecord);
    }

    private int popCurrentCallId() {
        if (!callIdsStack.isEmpty()) {
            callCountStack.popInt();
            return callIdsStack.popInt();
        } else {
            System.err.println("Inconsistency found, no method stamp in stack");
            return -1;
        }
    }

    public String getThreadName() {
        return threadName;
    }

    public CallEnterRecordList getEnterRecords() {
        return enterRecords;
    }

    public CallExitRecordList getExitRecords() {
        return exitRecords;
    }

    public long getId() {
        return id;
    }

    public long getEpochMillisCreatedTime() {
        return epochMillisCreatedTime;
    }

    @Override
    public String toString() {
        return "CallRecordLog{" +
                "id=" + id +
                ", calls=" + callIdsStack.size() +
                '}';
    }
}
