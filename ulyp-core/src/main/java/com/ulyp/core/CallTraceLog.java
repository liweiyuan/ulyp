package com.ulyp.core;

import com.ulyp.core.printers.ObjectBinaryPrinter;
import com.ulyp.core.printers.ObjectBinaryPrinterType;
import it.unimi.dsi.fastutil.booleans.BooleanArrayList;
import org.agrona.collections.IntArrayList;

import java.util.concurrent.atomic.AtomicLong;

public class CallTraceLog {
    public static final AtomicLong idGenerator = new AtomicLong(3000000000L);

    private final long id = idGenerator.incrementAndGet();

    private final TracingContext tracingContext;
    private final CallEnterTraceList enterTraces = new CallEnterTraceList();
    private final CallExitTraceList exitTraces = new CallExitTraceList();
    private final IntArrayList callIdsStack = new IntArrayList();
    private final BooleanArrayList traceFlagStack = new BooleanArrayList();
    private final IntArrayList callCountStack = new IntArrayList();

    private final long epochMillisCreatedTime;
    private final int maxDepth;
    private final int maxCallsPerDepth;

    private boolean inProcessOfTracing = true;
    private int callIdCounter = 0;

    public CallTraceLog(TracingContext tracingContext, int maxDepth, int maxCallsPerDepth) {
        this.epochMillisCreatedTime = System.currentTimeMillis();
        this.maxDepth = maxDepth;
        this.maxCallsPerDepth = maxCallsPerDepth;
        this.tracingContext = tracingContext;
        callCountStack.addInt(0);
    }

    public void onMethodEnter(long methodId, ObjectBinaryPrinter[] printers, Object[] args) {
        if (!inProcessOfTracing) {
            return;
        }

        inProcessOfTracing = false;
        try {
            int callsMadeInCurrentMethod = callCountStack.getInt(callCountStack.size() - 1);

            int callId = callIdCounter++;
            boolean canTrace = callIdsStack.size() <= maxDepth && callsMadeInCurrentMethod < maxCallsPerDepth;
            pushCurrentMethodCallId(callId, canTrace);

            if (canTrace) {
                enterTraces.add(callId, methodId, tracingContext, printers, args);
                callCountStack.setInt(callCountStack.size() - 2, callsMadeInCurrentMethod + 1);
            }
        } finally {
            inProcessOfTracing = true;
        }
    }

    public void onMethodExit(long methodId, ObjectBinaryPrinter resultPrinter, Object returnValue, Throwable thrown) {
        if (!inProcessOfTracing) {
            return;
        }

        inProcessOfTracing = false;
        try {
            boolean traced = traceFlagStack.popBoolean();
            long callId = popCurrentCallId();

            if (traced && callId >= 0) {
                if (thrown == null) {
                    exitTraces.add(callId, methodId, tracingContext, false, tracingContext.getClassId(returnValue), resultPrinter, returnValue);
                } else {
                    exitTraces.add(callId, methodId, tracingContext, true, tracingContext.getClassId(thrown), ObjectBinaryPrinterType.THROWABLE_PRINTER.getPrinter(), thrown);
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
        return enterTraces.size();
    }

    private void pushCurrentMethodCallId(int callId, boolean canTrace) {
        callIdsStack.pushInt(callId);
        /*
        * If current method call is not traced, then children (i.e. calls within this method) should be traced as well, otherwise
        * the tree will lose it's form. We prohibit it by setting number of calls already made to maximum.
        */
        callCountStack.pushInt(canTrace ? 0 : Integer.MAX_VALUE);
        traceFlagStack.push(canTrace);
    }

    private long popCurrentCallId() {
        if (!callIdsStack.isEmpty()) {
            callCountStack.popInt();
            return callIdsStack.popInt();
        } else {
            System.err.println("Inconsistency found, no method stamp in stack");
            return -1;
        }
    }

    public CallEnterTraceList getEnterTraces() {
        return enterTraces;
    }

    public CallExitTraceList getExitTraces() {
        return exitTraces;
    }

    public long getId() {
        return id;
    }

    public long getEpochMillisCreatedTime() {
        return epochMillisCreatedTime;
    }

    @Override
    public String toString() {
        return "CallTraceLog{" +
                "id=" + id +
                ", calls=" + callIdsStack.size() +
                '}';
    }
}
