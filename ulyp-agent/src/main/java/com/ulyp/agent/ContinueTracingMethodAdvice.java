package com.ulyp.agent;

import com.ulyp.agent.util.ByteBuddyAgentRuntime;
import com.ulyp.core.MethodDescriptionDictionary;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bytecode.assign.Assigner;

public class ContinueTracingMethodAdvice {

    @Advice.OnMethodEnter
    static void enter(
            @MethodDescriptionValue long methodDescriptionId,
            @Advice.AllArguments Object[] arguments) {
        if (methodDescriptionId < 0) {

            CallTracer.getInstance().startOrContinueTracing(
                    ByteBuddyAgentRuntime.getInstance(),
                    MethodDescriptionDictionary.getInstance().get(methodDescriptionId),
                    arguments
            );
        } else {
            if (CallTracer.getInstance().tracingIsActiveInThisThread()) {
                CallTracer.getInstance().onMethodEnter(MethodDescriptionDictionary.getInstance().get(methodDescriptionId), arguments);
            }
        }
    }

    @Advice.OnMethodExit(onThrowable = Throwable.class)
    static void exit(
            @MethodDescriptionValue long methodDescriptionId,
            @Advice.Thrown Throwable throwable,
            @Advice.Return(typing = Assigner.Typing.DYNAMIC) Object returnValue) {
        if (methodDescriptionId < 0) {
            CallTracer.getInstance().endTracingIfPossible(
                    MethodDescriptionDictionary.getInstance().get(methodDescriptionId),
                    returnValue,
                    throwable
            );
        } else {
            if (CallTracer.getInstance().tracingIsActiveInThisThread()) {
                CallTracer.getInstance().onMethodExit(MethodDescriptionDictionary.getInstance().get(methodDescriptionId), returnValue, throwable);
            }
        }
    }
}
