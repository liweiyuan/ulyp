package com.ulyp.agent;

import com.ulyp.agent.log.AgentLogManager;
import com.ulyp.agent.log.LoggingSettings;
import com.ulyp.agent.settings.UiSettings;
import com.ulyp.agent.util.EnhancedThreadLocal;
import com.ulyp.core.*;
import org.apache.logging.log4j.Logger;

import javax.annotation.concurrent.ThreadSafe;

@SuppressWarnings("unused")
@ThreadSafe
public class CallTracer {

    private static final Logger logger = AgentLogManager.getLogger(CallTracer.class);

    private static final CallTracer instance = new CallTracer(AgentContext.getInstance());

    public static CallTracer getInstance() {
        return instance;
    }

    private final EnhancedThreadLocal<CallTraceLog> threadLocalTraceLog = new EnhancedThreadLocal<>();
    private final AgentContext context;

    private volatile boolean mayStartTracing = true;
    private final TracingParams tracingParams = new TracingParams(false, false, false);

    public CallTracer(AgentContext context) {
        this.context = context;

        UiSettings uiSettings = context.getUiSettings();
        uiSettings.mayStartTracing().addListener((oldValue, newValue) -> this.mayStartTracing = newValue);
        uiSettings.traceCollections().addListener((oldValue, newValue) -> this.tracingParams.updateTraceCollections(newValue));
    }

    public boolean tracingIsActiveInThisThread() {
        return threadLocalTraceLog.get() != null;
    }

    public void startOrContinueTracing(MethodDescription methodDescription, Object[] args) {
        if (!tracingIsActiveInThisThread() && !mayStartTracing) {
            return;
        }

        CallTraceLog traceLog = threadLocalTraceLog.getOrCreate(() -> {
            CallTraceLog log = new CallTraceLog(
                    MethodDescriptionDictionary.getInstance(),
                    context.getSysPropsSettings().getMaxTreeDepth(),
                    context.getSysPropsSettings().getMaxCallsPerMethod());
            if (LoggingSettings.IS_TRACE_TURNED_ON) {
                logger.trace("Create new {}, method {}, args {}", log, methodDescription, args);
            }
            return log;
        });
        onMethodEnter(methodDescription, args);
    }

    public void endTracingIfPossible(MethodDescription methodDescription, Object result, Throwable thrown) {
        CallTraceLog traceLog = threadLocalTraceLog.get();
        onMethodExit(methodDescription, result, thrown);

        if (traceLog != null && traceLog.isComplete()) {
            threadLocalTraceLog.clear();
            if (traceLog.size() >= context.getSysPropsSettings().getMinTraceCount()) {
                if (LoggingSettings.IS_TRACE_TURNED_ON) {
                    logger.trace("Will send trace log {}", traceLog);
                }
                context.getTransport().uploadAsync(traceLog, MethodDescriptionDictionary.getInstance(), context.getProcessInfo());
            }
        }
    }

    public void onMethodEnter(MethodDescription method, Object[] args) {
        CallTraceLog callTraces = threadLocalTraceLog.get();
        if (callTraces == null) {
            return;
        }
        if (LoggingSettings.IS_TRACE_TURNED_ON) {
            logger.trace("Method enter on {}, method {}, args {}", callTraces, method, args);
        }
        callTraces.onMethodEnter(method.getId(), tracingParams, method.getParamPrinters(), args);
    }

    public void onMethodExit(MethodDescription method, Object result, Throwable thrown) {
        CallTraceLog callTracesLog = threadLocalTraceLog.get();
        if (callTracesLog == null) return;

        if (LoggingSettings.IS_TRACE_TURNED_ON) {
            logger.trace("Method exit {}, method {}, return value {}, thrown {}", callTracesLog, method, result, thrown);
        }
        callTracesLog.onMethodExit(method.getId(), tracingParams, method.getResultPrinter(), result, thrown);
    }
}
