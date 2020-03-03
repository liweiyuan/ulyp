package com.ulyp.agent;

import com.ulyp.agent.printer.Printer;
import com.ulyp.agent.util.Log;
import com.ulyp.transport.TMethodEnterTrace;
import com.ulyp.transport.TMethodExitTrace;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongStack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class MethodTraceLog {
    public static final AtomicLong idGenerator = new AtomicLong(3000000000L);

    private final Log log;
    private final long id = idGenerator.incrementAndGet();
    private final List<TMethodEnterTrace> enterTraces = new ArrayList<>();
    private final List<TMethodExitTrace> exitTraces = new ArrayList<>();
    private final LongArrayList callIdsStack = new LongArrayList();
    private final long epochMillisCreatedTime;
    private final int maxDepth;

    private long callIdCounter = 0;

    MethodTraceLog(Log log, int maxDepth) {
        this.epochMillisCreatedTime = System.currentTimeMillis();
        this.log = log;
        this.maxDepth = maxDepth;
    }

    public void onMethodEnter(long methodId, Printer[] printers, Object[] args) {
        long callId = callIdCounter++;
        log.log(() -> "Method enter, log id " + id + ", call id = " + callId + ", method id = " + methodId + ", enter traces cnt = " + enterTraces.size() + ", exit traces cnt = " + exitTraces.size());
        pushCurrentMethodCallId(callId);

        if (callIdsStack.size() <= maxDepth) {
            TMethodEnterTrace.Builder enterTraceBuilder = TMethodEnterTrace.newBuilder().setCallId(callId).setMethodId(methodId);
            for (int i = 0; i < args.length; i++) {
                enterTraceBuilder.addArgs(printers[i].print(args[i]));
            }
            enterTraces.add(enterTraceBuilder.build());
        }
    }

    public void onMethodExit(long method, String result, String exception) {
        long callId = popCurrentCallId();
        log.log(() -> "Method exit, log id " + id + ", call id = " + callId + ", method id = " + method + ", enter traces cnt = " + enterTraces.size() + ", exit traces cnt = " + exitTraces.size());

        if (callIdsStack.size() < maxDepth) {
            exitTraces.add(
                    TMethodExitTrace.newBuilder()
                            .setCallId(callId)
                            .setMethodId(method)
                            .setReturnValue(result)
                            .setThrown(exception)
                            .build()
            );
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

    public List<TMethodEnterTrace> getEnterTraces() {
        return enterTraces;
    }

    public List<TMethodExitTrace> getExitTraces() {
        return exitTraces;
    }

    public long getId() {
        return id;
    }

    public long getEpochMillisCreatedTime() {
        return epochMillisCreatedTime;
    }
}
