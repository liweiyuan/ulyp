package com.ulyp.core;

import com.ulyp.core.printers.ObjectBinaryPrinter;
import com.ulyp.core.printers.ObjectBinaryPrinterType;
import it.unimi.dsi.fastutil.longs.LongArrayList;

import java.util.concurrent.atomic.AtomicLong;

public class MethodTraceLog {
    public static final AtomicLong idGenerator = new AtomicLong(3000000000L);

    private final long id = idGenerator.incrementAndGet();

    private final MethodDescriptionDictionary methodDescriptionDictionary;
    private final MethodEnterTraceList enterTraces = new MethodEnterTraceList();
    private final MethodExitTraceList exitTraces = new MethodExitTraceList();
    private final LongArrayList callIdsStack = new LongArrayList();
    private final long epochMillisCreatedTime;
    private final int maxDepth;

    private boolean active = true;
    private long callIdCounter = 0;

    private final long[] argClassIds = new long[256];

    public MethodTraceLog(MethodDescriptionDictionary methodDescriptionDictionary, int maxDepth) {
        this.epochMillisCreatedTime = System.currentTimeMillis();
        this.maxDepth = maxDepth;
        this.methodDescriptionDictionary = methodDescriptionDictionary;
    }

    public void onMethodEnter(long methodId, ObjectBinaryPrinter[] printers, Object[] args) {
        if (!active) {
            return;
        }

        active = false;
        try {
            long callId = callIdCounter++;
            pushCurrentMethodCallId(callId);

            if (callIdsStack.size() <= maxDepth) {
                for (int i = 0; i < args.length; i++) {
                    argClassIds[i] = args[i] != null ? methodDescriptionDictionary.get(args[i].getClass()).getId() : -1;
                }

                enterTraces.add(callId, methodId, argClassIds, printers, args);
            }
        } finally {
            active = true;
        }
    }

    public void onMethodExit(long methodId, ObjectBinaryPrinter resultPrinter, Object returnValue, Throwable thrown) {
        if (!active) {
            return;
        }

        active = false;
        try {
            long callId = popCurrentCallId();
            //log.log(() -> "Method exit, log id " + id + ", call id = " + callId + ", method id = " + methodId + ", enter traces cnt = " + enterTraces.size() + ", exit traces cnt = " + exitTraces.size());

            if (callId >= 0 && callIdsStack.size() < maxDepth) {
                if (thrown == null) {
                    long returnValueClassId = returnValue != null ? methodDescriptionDictionary.get(returnValue.getClass()).getId() : -1;
                    exitTraces.add(callId, methodId, false, returnValueClassId, resultPrinter, returnValue);
                } else {
                    long returnValueClassId = methodDescriptionDictionary.get(thrown.getClass()).getId();
                    exitTraces.add(callId, methodId, true, returnValueClassId, ObjectBinaryPrinterType.THROWABLE_PRINTER.getPrinter(), thrown);
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
