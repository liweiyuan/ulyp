package com.ulyp.agent;

import com.ulyp.core.MethodDescriptionDictionary;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bytecode.assign.Assigner;

public class ContinueTracingMethodAdvice {

    @Advice.OnMethodEnter
    static void enter(
            @MethodDescriptionValue long methodDescriptionId,
            @Advice.AllArguments Object[] arguments) {
        if (CallTracer.getInstance().tracingIsActiveInThisThread()) {
            CallTracer.getInstance().onMethodEnter(MethodDescriptionDictionary.getInstance().get(methodDescriptionId), arguments);
        }
    }

    @Advice.OnMethodExit(onThrowable = Throwable.class)
    static void exit(
            @MethodDescriptionValue long methodDescriptionId,
            @Advice.Thrown Throwable throwable,
            @Advice.Return(typing = Assigner.Typing.DYNAMIC) Object returnValue) {
        if (CallTracer.getInstance().tracingIsActiveInThisThread()) {
            CallTracer.getInstance().onMethodExit(MethodDescriptionDictionary.getInstance().get(methodDescriptionId), returnValue, throwable);
        }
    }
}
