package com.ulyp.agent;

import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bytecode.assign.Assigner;

public class StartTracingMethodAdvice {

    @Advice.OnMethodEnter
    static void enter(@MethodDescriptionValue long methodDescriptionId, @Advice.AllArguments Object[] arguments) {
        CallTracer.getInstance().startOrContinueTracing(
                AgentContext.getInstance().getMethodDescriptionDictionary().get(methodDescriptionId),
                arguments
        );
    }

    @Advice.OnMethodExit(onThrowable = Throwable.class)
    static void exit(@MethodDescriptionValue long methodDescriptionId,
                     @Advice.Return(typing = Assigner.Typing.DYNAMIC) Object returnValue,
                     @Advice.Thrown Throwable throwable) {
        CallTracer.getInstance().endTracingIfPossible(
                AgentContext.getInstance().getMethodDescriptionDictionary().get(methodDescriptionId),
                returnValue,
                throwable
        );
    }
}
