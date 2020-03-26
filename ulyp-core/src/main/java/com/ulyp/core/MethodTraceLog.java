package com.ulyp.core;

import com.ulyp.core.printers.ObjectBinaryPrinter;
import com.ulyp.core.printers.ObjectBinaryPrinterType;
import it.unimi.dsi.fastutil.longs.LongArrayList;

import java.util.concurrent.atomic.AtomicLong;

public class MethodTraceLog {
    public static final AtomicLong idGenerator = new AtomicLong(3000000000L);

    private final long id = idGenerator.incrementAndGet();

    private final MethodEnterTraceList enterTraces = new MethodEnterTraceList();
    private final MethodExitTraceList exitTraces = new MethodExitTraceList();
    private final LongArrayList callIdsStack = new LongArrayList();
    private final long epochMillisCreatedTime;
    private final int maxDepth;

    private boolean active = true;
    private long callIdCounter = 0;

    public MethodTraceLog(int maxDepth) {
        this.epochMillisCreatedTime = System.currentTimeMillis();
        this.maxDepth = maxDepth;
    }

    public void onMethodEnter(long methodId, ObjectBinaryPrinter[] printers, Object[] args) {
        if (!active) {
            return;
        }

        active = false;
        try {
            long callId = callIdCounter++;
            //log.log(() -> "Method enter, log id " + id + ", call id = " + callId + ", method id = " + methodId + ", enter traces cnt = " + enterTraces.size() + ", exit traces cnt = " + exitTraces.size());
            pushCurrentMethodCallId(callId);

            if (callIdsStack.size() <= maxDepth) {
                enterTraces.add(callId, methodId, printers, args);
            }
        } finally {
            active = true;
        }
    }

    public void onMethodExit(long methodId, ObjectBinaryPrinter resultPrinter, Object result, Throwable thrown) {
        if (!active) {
            return;
        }

        active = false;
        try {
            long callId = popCurrentCallId();
            //log.log(() -> "Method exit, log id " + id + ", call id = " + callId + ", method id = " + methodId + ", enter traces cnt = " + enterTraces.size() + ", exit traces cnt = " + exitTraces.size());

            if (callId >= 0 && callIdsStack.size() < maxDepth) {
                if (thrown == null) {
                    exitTraces.add(callId, methodId, false, resultPrinter, result);
                } else {
                    exitTraces.add(callId, methodId, true, ObjectBinaryPrinterType.THROWABLE_PRINTER.getPrinter(), thrown);
                }
            }
        } finally {
            active = true;
        }
    }

    public boolean isComplete() {
        return callIdsStack.isEmpty();
    }

    public long size() {
        return enterTraces.size();
    }

    private void pushCurrentMethodCallId(long callId) {
        callIdsStack.push(callId);
    }

    private long popCurrentCallId() {
        if (!callIdsStack.isEmpty()) {
            return callIdsStack.popLong();
        } else {
            System.err.println("Inconsistency found, no method stamp in stack");
            return -1;
        }
    }

    public MethodEnterTraceList getEnterTraces() {
        return enterTraces;
    }

    public MethodExitTraceList getExitTraces() {
        return exitTraces;
    }

    public long getId() {
        return id;
    }

    public long getEpochMillisCreatedTime() {
        return epochMillisCreatedTime;
    }
}
