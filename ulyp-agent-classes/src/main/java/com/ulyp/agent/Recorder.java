package com.ulyp.agent;

import com.ulyp.agent.log.AgentLogManager;
import com.ulyp.agent.log.LoggingSettings;
import com.ulyp.agent.settings.UiSettings;
import com.ulyp.agent.transport.CallRecordTreeRequest;
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
        uiSettings.getRecordCollectionItems().addListener((oldValue, newValue) -> this.recordingParamsUpdater.updateRecordCollectionItems(newValue));
    }

    public boolean recordingIsActiveInCurrentThread() {
        return threadLocalRecordsLog.get() != null;
    }

    public void startOrContinueRecording(AgentRuntime agentRuntime, MethodInfo methodInfo, Object callee, Object[] args) {
        if (!recordingIsActiveInCurrentThread() && !mayStartRecording) {
            return;
        }

        CallRecordLog recordLog = threadLocalRecordsLog.getOrCreate(() -> {
            CallRecordLog log = new CallRecordLog(
                    agentRuntime,
                    context.getSysPropsSettings().getMaxTreeDepth(),
                    context.getSysPropsSettings().getMaxCallsPerMethod());
            if (LoggingSettings.IS_TRACE_TURNED_ON) {
                logger.trace("Create new {}, method {}, args {}", log, methodInfo, args);
            }
            return log;
        });
        onMethodEnter(methodInfo, callee, args);
    }

    public void endRecordingIfPossible(AgentRuntime agentRuntime, MethodInfo methodInfo, Object result, Throwable thrown) {
        CallRecordLog recordLog = threadLocalRecordsLog.get();
        onMethodExit(methodInfo, result, thrown);

        if (recordLog != null && recordLog.isComplete()) {
            threadLocalRecordsLog.clear();
            if (recordLog.size() >= context.getSysPropsSettings().getMinRecordsCountForLog()) {
                if (LoggingSettings.IS_TRACE_TURNED_ON) {
                    logger.trace("Will send trace log {}", recordLog);
                }
                context.getTransport().uploadAsync(
                        new CallRecordTreeRequest(
                                recordLog,
                                MethodDescriptionMap.getInstance().values(),
                                agentRuntime.getAllKnownTypes(),
                                context.getProcessInfo()
                        )
                );
            }
        }
    }

    public void onMethodEnter(MethodInfo method, Object callee, Object[] args) {
        CallRecordLog log = threadLocalRecordsLog.get();
        if (log == null) {
            return;
        }
        if (LoggingSettings.IS_TRACE_TURNED_ON) {
            logger.trace("Method enter on {}, method {}, args {}", log, method, args);
        }
        log.onMethodEnter(method.getId(), method.getParamPrinters(), callee, args);
    }

    public void onMethodExit(MethodInfo method, Object result, Throwable thrown) {
        CallRecordLog log = threadLocalRecordsLog.get();
        if (log == null) return;

        if (LoggingSettings.IS_TRACE_TURNED_ON) {
            logger.trace("Method exit {}, method {}, return value {}, thrown {}", log, method, result, thrown);
        }
        log.onMethodExit(method.getId(), method.getResultPrinter(), result, thrown);
    }
}
