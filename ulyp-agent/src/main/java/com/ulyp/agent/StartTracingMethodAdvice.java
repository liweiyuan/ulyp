package com.ulyp.agent;

import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bytecode.assign.Assigner;

import java.lang.reflect.Executable;

public class StartTracingMethodAdvice {

    @Advice.OnMethodEnter
    static void enter(
            @Advice.Origin Executable executable,
            @InstrumentationId long instrumentationId,
            @Advice.AllArguments Object[] arguments) {
        System.out.println(instrumentationId);
        CallTracer.getInstance().startOrContinueTracing(AgentContext.getInstance().getMethodDescriptionDictionary().get(executable), arguments);
    }

    @Advice.OnMethodExit(onThrowable = Throwable.class)
    static void exit(
            @Advice.Origin Executable executable,
            @Advice.Return(typing = Assigner.Typing.DYNAMIC) Object returnValue,
            @Advice.Thrown Throwable throwable) {
        CallTracer.getInstance().endTracingIfPossible(AgentContext.getInstance().getMethodDescriptionDictionary().get(executable), returnValue, throwable);
    }
}
