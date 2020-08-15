package com.ulyp.agent;

import com.ulyp.agent.util.ByteBuddyAgentRuntime;
import com.ulyp.core.MethodDescriptionDictionary;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bytecode.assign.Assigner;

public class MethodAdvice {

    @Advice.OnMethodEnter
    static void enter(
            @MethodDescriptionValue long methodDescriptionId,
            @Advice.This(optional = true) Object callee,
            @Advice.AllArguments Object[] arguments) {
        if (methodDescriptionId < 0) {

            Recorder.getInstance().startOrContinueRecording(
                    ByteBuddyAgentRuntime.getInstance(),
                    MethodDescriptionDictionary.getInstance().get(methodDescriptionId),
                    callee,
                    arguments
            );
        } else {
            if (Recorder.getInstance().recordingIsActiveInCurrentThread()) {
                Recorder.getInstance().onMethodEnter(MethodDescriptionDictionary.getInstance().get(methodDescriptionId), callee, arguments);
            }
        }
    }

    @Advice.OnMethodExit(onThrowable = Throwable.class)
    static void exit(
            @MethodDescriptionValue long methodDescriptionId,
            @Advice.Thrown Throwable throwable,
            @Advice.Return(typing = Assigner.Typing.DYNAMIC) Object returnValue) {
        if (methodDescriptionId < 0) {
            Recorder.getInstance().endRecordingIfPossible(
                    MethodDescriptionDictionary.getInstance().get(methodDescriptionId),
                    returnValue,
                    throwable
            );
        } else {
            if (Recorder.getInstance().recordingIsActiveInCurrentThread()) {
                Recorder.getInstance().onMethodExit(MethodDescriptionDictionary.getInstance().get(methodDescriptionId), returnValue, throwable);
            }
        }
    }
}
