package com.ulyp.agent;

import com.ulyp.agent.util.EnhancedThreadLocal;
import com.ulyp.agent.util.Log;
import com.ulyp.agent.vars.PrintersSupport;
import com.ulyp.transport.TMethodTraceLog;
import com.ulyp.transport.TMethodTraceLogUploadRequest;

@SuppressWarnings("unused")
public class Tracer {

    private final EnhancedThreadLocal<MethodTraceLog> threadLocalTraceLog = new EnhancedThreadLocal<>();
    private final ProgramContext context;
    private final MethodDescriptionDictionary methodCache;
    private final Log log;

    public Tracer(ProgramContext context) {
        this.context = context;
        this.methodCache = context.getMethodCache();
        this.log = context.getLog();
    }

    public void startOrContinueTracing(MethodDescription methodDescription, Object[] args) {
        threadLocalTraceLog.getOrCreate(() -> new MethodTraceLog(log));
        log.log(() -> "Tracing active, trace log id = " + threadLocalTraceLog.get().getId());

        push(methodDescription, args);
    }

    public void endTracingIfPossible(MethodDescription methodDescription, Object result, Throwable thrown) {
        MethodTraceLog traceLog = threadLocalTraceLog.get();

        if (traceLog != null) {
            log.log(() -> "May end tracing, trace log id = " + methodDescription.getId());
            pop(methodDescription, result, thrown);

            if (traceLog.isComplete()) {
                enqueueToPrinter(threadLocalTraceLog.pop());
            }
        }
    }

    public void push(MethodDescription ctMethodInfo, Object[] args) {
        MethodTraceLog methodTracesLog = threadLocalTraceLog.get();
        if (methodTracesLog == null) {
            return;
        }
        String[] argsStrings = PrintersSupport.print(ctMethodInfo.getParamPrinters(), args);
        methodTracesLog.onMethodEnter(ctMethodInfo.getId(), argsStrings);
    }

    public void pop(MethodDescription ctMethodInfo, Object result, Throwable thrown) {
        MethodTraceLog methodTracesLog = threadLocalTraceLog.get();
        if (methodTracesLog == null) return;

        methodTracesLog.onMethodExit(
                ctMethodInfo.getId(),
                result != null ? ctMethodInfo.getResultPrinter().print(result) : "",
                thrown != null ? ctMethodInfo.getExceptionPrinter().print(thrown) : "");
    }

    private void enqueueToPrinter(MethodTraceLog traceLog) {
        TMethodTraceLogUploadRequest.Builder requestBuilder = TMethodTraceLogUploadRequest.newBuilder();
        for (MethodDescription description : context.getMethodCache().getMethodInfos()) {
            requestBuilder.addMethodInfos(description.getMethodInfo());
        }
        requestBuilder
                .setTraceLogId(traceLog.getId())
                .setTraceLog(TMethodTraceLog.newBuilder()
                        .addAllEnterTraces(traceLog.getEnterTraces())
                        .addAllExitTraces(traceLog.getExitTraces()))
                .setMainClassName(context.getMainClassName())
                .setCreateEpochMillis(traceLog.getEpochMillisCreatedTime());

        context.getTransport().upload(requestBuilder.build());

        log.log(() -> "Sent trace log UI log with " + traceLog.size() + " traces");
    }
}
