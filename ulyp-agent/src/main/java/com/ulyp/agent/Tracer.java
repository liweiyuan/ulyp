package com.ulyp.agent;

import com.ulyp.agent.log.AgentLogManager;
import com.ulyp.agent.log.LoggingSettings;
import com.ulyp.agent.util.EnhancedThreadLocal;
import com.ulyp.core.*;
import com.ulyp.transport.TClassDescriptionList;
import com.ulyp.transport.TMethodDescriptionList;
import com.ulyp.transport.TMethodTraceLog;
import com.ulyp.transport.TMethodTraceLogUploadRequest;
import org.apache.logging.log4j.Logger;

import javax.annotation.concurrent.ThreadSafe;

@SuppressWarnings("unused")
@ThreadSafe
public class Tracer {

    private static final Logger logger = AgentLogManager.getLogger(BbTransformer.class);

    private final EnhancedThreadLocal<MethodTraceLog> threadLocalTraceLog = new EnhancedThreadLocal<>();
    private final AgentContext context;

    public Tracer(AgentContext context) {
        this.context = context;
    }

    public void startOrContinueTracing(MethodDescription methodDescription, Object[] args) {
        MethodTraceLog traceLog = threadLocalTraceLog.getOrCreate(() -> {
            MethodTraceLog log = new MethodTraceLog(
                    context.getMethodDescriptionDictionary(),
                    context.getSettings().getMaxTreeDepth());
            if (LoggingSettings.IS_TRACE_TURNED_ON) {
                logger.trace("Create new {}, method {}, args {}", log, methodDescription, args);
            }
            return log;
        });
        onMethodEnter(methodDescription, args);
    }

    public void endTracingIfPossible(MethodDescription methodDescription, Object result, Throwable thrown) {
        MethodTraceLog traceLog = threadLocalTraceLog.get();
        onMethodExit(methodDescription, result, thrown);

        if (traceLog.isComplete()) {
            threadLocalTraceLog.pop();
            if (traceLog.size() >= context.getSettings().getMinTraceCount()) {
                if (LoggingSettings.IS_TRACE_TURNED_ON) {
                    logger.trace("Will send trace log {}", traceLog);
                }
                enqueueToPrinter(traceLog);
            }
        }
    }

    public void onMethodEnter(MethodDescription method, Object[] args) {
        MethodTraceLog methodTracesLog = threadLocalTraceLog.get();
        if (methodTracesLog == null) {
            return;
        }
        if (LoggingSettings.IS_TRACE_TURNED_ON) {
            logger.trace("Method enter on {}, method {}, args {}", methodTracesLog, method, args);
        }
        methodTracesLog.onMethodEnter(method.getId(), method.getParamPrinters(), args);
    }

    public void onMethodExit(MethodDescription method, Object result, Throwable thrown) {
        MethodTraceLog methodTracesLog = threadLocalTraceLog.get();
        if (methodTracesLog == null) return;

        if (LoggingSettings.IS_TRACE_TURNED_ON) {
            logger.trace("Method exit {}, method {}, return value {}, thrown {}", methodTracesLog, method, result, thrown);
        }
        methodTracesLog.onMethodExit(method.getId(), method.getResultPrinter(), result, thrown);
    }

    private void enqueueToPrinter(MethodTraceLog traceLog) {
        TMethodTraceLog log = TMethodTraceLog.newBuilder()
                .setEnterTraces(traceLog.getEnterTraces().toByteString())
                .setExitTraces(traceLog.getExitTraces().toByteString())
                .build();

        MethodDescriptionList methodDescriptionList = new MethodDescriptionList();
        for (MethodDescription description : context.getMethodDescriptionDictionary().getMethodDescriptions()) {
            methodDescriptionList.add(description);
        }
        ClassDescriptionList classDescriptionList = new ClassDescriptionList();
        for (ClassDescription classDescription : context.getMethodDescriptionDictionary().getClassDescriptions()) {
            classDescriptionList.add(classDescription);
        }

        TMethodTraceLogUploadRequest.Builder requestBuilder = TMethodTraceLogUploadRequest.newBuilder();

        requestBuilder
                .setTraceLogId(traceLog.getId())
                .setTraceLog(log)
                .setMethodDescriptionList(TMethodDescriptionList.newBuilder().setData(methodDescriptionList.toByteString()).build())
                .setClassDescriptionList(TClassDescriptionList.newBuilder().setData(classDescriptionList.toByteString()).build())
                .setMainClassName(context.getProcessInfo().getMainClassName())
                .setCreateEpochMillis(traceLog.getEpochMillisCreatedTime())
                .setLifetimeMillis(System.currentTimeMillis() - traceLog.getEpochMillisCreatedTime());

        context.getTransport().upload(requestBuilder.build());
    }
}
