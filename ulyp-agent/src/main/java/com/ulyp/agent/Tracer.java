package com.ulyp.agent;

import com.ulyp.agent.util.EnhancedThreadLocal;
import com.ulyp.agent.util.Log;
import com.ulyp.core.MethodTraceLog;
import com.ulyp.transport.TMethodTraceLog;
import com.ulyp.transport.TMethodTraceLogUploadRequest;

@SuppressWarnings("unused")
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
        log.log(() -> "Tracing active, trace log id = " + traceLog.getId());

        onMethodEnter(methodDescription, args);
    }

    public void endTracingIfPossible(MethodDescription methodDescription, Object result, Throwable thrown) {
        MethodTraceLog traceLog = threadLocalTraceLog.get();

        if (traceLog != null) {
            log.log(() -> "May end tracing, trace log id = " + methodDescription.getId());
            onMethodExit(methodDescription, result, thrown);

            if (traceLog.isComplete()) {
                enqueueToPrinter(threadLocalTraceLog.pop());
            }
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

        TMethodTraceLogUploadRequest.Builder requestBuilder = TMethodTraceLogUploadRequest.newBuilder();
        for (MethodDescription description : context.getMethodCache().getMethodInfos()) {
            requestBuilder.addMethodInfos(description.getMethodInfo());
        }
        requestBuilder
                .setTraceLogId(traceLog.getId())
                .setTraceLog(log)
                .setMainClassName("?")
                .setCreateEpochMillis(traceLog.getEpochMillisCreatedTime())
                .setLifetimeMillis(System.currentTimeMillis() - traceLog.getEpochMillisCreatedTime());

        context.getTransport().upload(requestBuilder.build());
    }
}
