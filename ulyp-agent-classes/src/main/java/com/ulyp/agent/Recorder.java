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
public class Recorder {

    private static final Logger logger = AgentLogManager.getLogger(Recorder.class);

    private static final Recorder instance = new Recorder(AgentContext.getInstance());

    public static Recorder getInstance() {
        return instance;
    }

    private final EnhancedThreadLocal<CallRecordLog> threadLocalRecordsLog = new EnhancedThreadLocal<>();
    private final AgentContext context;

    private volatile boolean mayStartRecording = true;
    private final RecordingParamsUpdater recordingParamsUpdater = new RecordingParamsUpdater();

    public Recorder(AgentContext context) {
        this.context = context;

        UiSettings uiSettings = context.getUiSettings();
        uiSettings.mayStartTracing().addListener((oldValue, newValue) -> this.mayStartRecording = newValue);
        uiSettings.traceCollections().addListener((oldValue, newValue) -> this.recordingParamsUpdater.updateRecordCollectionItems(newValue));
    }

    public boolean recordingIsActiveInCurrentThread() {
        return threadLocalRecordsLog.get() != null;
    }

    public void startOrContinueRecording(AgentRuntime agentRuntime, MethodDescription methodDescription, Object callee, Object[] args) {
        if (!recordingIsActiveInCurrentThread() && !mayStartRecording) {
            return;
        }

        CallRecordLog traceLog = threadLocalRecordsLog.getOrCreate(() -> {
            CallRecordLog log = new CallRecordLog(
                    agentRuntime,
                    context.getSysPropsSettings().getMaxTreeDepth(),
                    context.getSysPropsSettings().getMaxCallsPerMethod());
            if (LoggingSettings.IS_TRACE_TURNED_ON) {
                logger.trace("Create new {}, method {}, args {}", log, methodDescription, args);
            }
            return log;
        });
        onMethodEnter(methodDescription, callee, args);
    }

    public void endRecordingIfPossible(MethodDescription methodDescription, Object result, Throwable thrown) {
        CallRecordLog traceLog = threadLocalRecordsLog.get();
        onMethodExit(methodDescription, result, thrown);

        if (traceLog != null && traceLog.isComplete()) {
            threadLocalRecordsLog.clear();
            if (traceLog.size() >= context.getSysPropsSettings().getMinTraceCount()) {
                if (LoggingSettings.IS_TRACE_TURNED_ON) {
                    logger.trace("Will send trace log {}", traceLog);
                }
                context.getTransport().uploadAsync(traceLog, MethodDescriptionDictionary.getInstance(), context.getProcessInfo());
            }
        }
    }

    public void onMethodEnter(MethodDescription method, Object callee, Object[] args) {
        CallRecordLog log = threadLocalRecordsLog.get();
        if (log == null) {
            return;
        }
        if (LoggingSettings.IS_TRACE_TURNED_ON) {
            logger.trace("Method enter on {}, method {}, args {}", log, method, args);
        }
        log.onMethodEnter(method.getId(), method.getParamPrinters(), callee, args);
    }

    public void onMethodExit(MethodDescription method, Object result, Throwable thrown) {
        CallRecordLog log = threadLocalRecordsLog.get();
        if (log == null) return;

        if (LoggingSettings.IS_TRACE_TURNED_ON) {
            logger.trace("Method exit {}, method {}, return value {}, thrown {}", log, method, result, thrown);
        }
        log.onMethodExit(method.getId(), method.getResultPrinter(), result, thrown);
    }
}
