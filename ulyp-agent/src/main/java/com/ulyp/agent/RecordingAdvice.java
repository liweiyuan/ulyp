package com.ulyp.agent;

import com.ulyp.agent.util.ByteBuddyAgentRuntime;
import com.ulyp.core.MethodDescriptionMap;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bytecode.assign.Assigner;

public class RecordingAdvice {

    /**
     * @param methodId injected right into bytecode unique method id. Mapping is made by
     *                 {@link MethodDescriptionFactory} class.
     */
    @Advice.OnMethodEnter
    static void enter(
            @MethodDescriptionValue int methodId,
            @Advice.This(optional = true) Object callee,
            @Advice.AllArguments Object[] arguments) {

        if (methodId < 0) {

            Recorder.getInstance().startOrContinueRecording(
                    ByteBuddyAgentRuntime.getInstance(),
                    MethodDescriptionMap.getInstance().get(methodId),
                    callee,
                    arguments
            );
        } else {
            if (Recorder.currentRecordingSessionCount.get() > 0 && Recorder.getInstance().recordingIsActiveInCurrentThread()) {
                Recorder.getInstance().onMethodEnter(MethodDescriptionMap.getInstance().get(methodId), callee, arguments);
            }
        }
    }

    /**
     * @param methodId injected right into bytecode unique method id. Mapping is made by
     *                 {@link MethodDescriptionFactory} class. Guaranteed to be the same
     *                 as for enter advice
     */
    @Advice.OnMethodExit(onThrowable = Throwable.class)
    static void exit(
            @MethodDescriptionValue int methodId,
            @Advice.Thrown Throwable throwable,
            @Advice.Return(typing = Assigner.Typing.DYNAMIC) Object returnValue) {

        if (methodId < 0) {
            Recorder.getInstance().endRecordingIfPossible(
                    ByteBuddyAgentRuntime.getInstance(),
                    MethodDescriptionMap.getInstance().get(methodId),
                    returnValue,
                    throwable
            );
        } else {
            if (Recorder.currentRecordingSessionCount.get() > 0 && Recorder.getInstance().recordingIsActiveInCurrentThread()) {
                Recorder.getInstance().onMethodExit(MethodDescriptionMap.getInstance().get(methodId), returnValue, throwable);
            }
        }
    }
}
