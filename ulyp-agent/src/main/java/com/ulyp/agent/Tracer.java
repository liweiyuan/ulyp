package com.ulyp.agent;

import com.ulyp.agent.util.EnhancedThreadLocal;
import com.ulyp.agent.util.Log;
import com.ulyp.core.MethodDescription;
import com.ulyp.core.MethodDescriptionList;
import com.ulyp.core.MethodTraceLog;
import com.ulyp.transport.TMethodDescriptionList;
import com.ulyp.transport.TMethodTraceLog;
import com.ulyp.transport.TMethodTraceLogUploadRequest;

import javax.annotation.concurrent.ThreadSafe;

@SuppressWarnings("unused")
@ThreadSafe
public class Tracer {

    private final EnhancedThreadLocal<MethodTraceLog> threadLocalTraceLog = new EnhancedThreadLocal<>();
    private final AgentContext context;
    private final Log log;

    public Tracer(AgentContext context) {
        this.context = context;
        this.log = context.getLog();
    }

    public void startOrContinueTracing(MethodDescription methodDescription, Object[] args) {
        MethodTraceLog traceLog = threadLocalTraceLog.getOrCreate(() -> new MethodTraceLog(context.getSettings().getMaxTreeDepth()));
        onMethodEnter(methodDescription, args);
    }

    public void endTracingIfPossible(MethodDescription methodDescription, Object result, Throwable thrown) {
        MethodTraceLog traceLog = threadLocalTraceLog.get();

        log.log(() -> "May end tracing, trace log id = " + methodDescription.getId());
        onMethodExit(methodDescription, result, thrown);

        if (traceLog.isComplete()) {
            enqueueToPrinter(threadLocalTraceLog.pop());
        }
    }

    public void onMethodEnter(MethodDescription method, Object[] args) {
        MethodTraceLog methodTracesLog = threadLocalTraceLog.get();
        if (methodTracesLog == null) {
            return;
        }
        methodTracesLog.onMethodEnter(method.getId(), method.getParamPrinters(), args);
    }

    public void onMethodExit(MethodDescription method, Object result, Throwable thrown) {
        MethodTraceLog methodTracesLog = threadLocalTraceLog.get();
        if (methodTracesLog == null) return;

        methodTracesLog.onMethodExit(method.getId(), method.getResultPrinter(), result, thrown);
    }

    private void enqueueToPrinter(MethodTraceLog traceLog) {
        TMethodTraceLog log = TMethodTraceLog.newBuilder()
                .setEnterTraces(traceLog.getEnterTraces().toByteString())
                .setExitTraces(traceLog.getExitTraces().toByteString())
                .build();

        MethodDescriptionList methodDescriptionList = new MethodDescriptionList();
        for (MethodDescription description : context.getMethodCache().getMethodInfos()) {
            methodDescriptionList.add(description);
        }

        TMethodTraceLogUploadRequest.Builder requestBuilder = TMethodTraceLogUploadRequest.newBuilder();

        requestBuilder
                .setTraceLogId(traceLog.getId())
                .setTraceLog(log)
                .setMethodDescriptionList(TMethodDescriptionList.newBuilder().setData(methodDescriptionList.toByteString()).build())
                .setMainClassName(context.getProcessInfo().getMainClassName())
                .setCreateEpochMillis(traceLog.getEpochMillisCreatedTime())
                .setLifetimeMillis(System.currentTimeMillis() - traceLog.getEpochMillisCreatedTime());

        context.getTransport().upload(requestBuilder.build());
    }
}
