package com.ulyp.agent;

import com.ulyp.agent.util.Log;
import net.bytebuddy.agent.builder.AgentBuilder.Transformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;

import java.lang.reflect.Executable;

public class BbTransformer implements Transformer {

    public static final AgentContext context = new AgentContext();
    public static final Log log = context.getLog();
    public static final Settings settings = context.getSettings();
    @SuppressWarnings("unused")
    public static final Tracer tracer = new Tracer(context);

    @Override
    public DynamicType.Builder<?> transform(
            final DynamicType.Builder<?> builder,
            final TypeDescription typeDescription,
            final ClassLoader classLoader,
            final JavaModule module) {

        if (typeDescription.isInterface()) {
            return builder;
        }

        final AsmVisitorWrapper methodsStartVisitor =
                new AsmVisitorWrapper.ForDeclaredMethods()
                        .method(
                                ElementMatchers
                                        .isMethod()
                                        .and(ElementMatchers.not(ElementMatchers.isAbstract()))
                                        .and(ElementMatchers.not(ElementMatchers.isConstructor()))
                                        .and(ElementMatchers.not(ElementMatchers.isTypeInitializer()))
                                        .and(ElementMatchers.not(ElementMatchers.isToString()))
                                        .and(desc -> {
                                            boolean shouldStart = settings.shouldStartTracing(desc);
                                            if (shouldStart) {
                                                log.log(() -> "Should start tracing at " + typeDescription.getName() + "." + desc.getActualName());
                                            }
                                            return shouldStart;
                                        }),
                                Advice.to(StartTracingMethodAdvice.class));

        final AsmVisitorWrapper methodsVisitor =
                new AsmVisitorWrapper.ForDeclaredMethods()
                        .method(ElementMatchers
                                        .isMethod()
                                        .and(ElementMatchers.not(ElementMatchers.isAbstract()))
                                        .and(ElementMatchers.not(ElementMatchers.isConstructor()))
                                        .and(ElementMatchers.not(ElementMatchers.isTypeInitializer()))
                                        .and(ElementMatchers.not(ElementMatchers.isToString()))
                                        .and(desc -> {
                                            if (desc.isAbstract() || desc.isConstructor() || desc.isTypeInitializer()) {
                                                return false;
                                            }
                                            return !settings.shouldStartTracing(desc);
                                        }),
                                Advice.to(MethodAdvice.class));

        return builder
                .visit(methodsStartVisitor)
                .visit(methodsVisitor);
    }

    public static class StartTracingMethodAdvice {

        @Advice.OnMethodEnter
        static void enter(
                @Advice.Origin Executable executable,
                @Advice.AllArguments Object[] arguments) {
            tracer.startOrContinueTracing(context.getMethodCache().get(executable), arguments);
        }

        @Advice.OnMethodExit(onThrowable = Throwable.class)
        static void exit(
                @Advice.Origin Executable executable,
                @Advice.Return(typing = Assigner.Typing.DYNAMIC) Object returnValue,
                @Advice.Thrown Throwable throwable) {
            tracer.endTracingIfPossible(context.getMethodCache().get(executable), returnValue, throwable);
        }
    }

    public static class MethodAdvice {

        @Advice.OnMethodEnter
        static void enter(
                @Advice.Origin Executable executable,
                @Advice.AllArguments Object[] arguments) {
            tracer.onMethodEnter(context.getMethodCache().get(executable), arguments);
        }

        @Advice.OnMethodExit(onThrowable = Throwable.class)
        static void exit(
                @Advice.Origin Executable executable,
                @Advice.Thrown Throwable throwable,
                @Advice.Return(typing = Assigner.Typing.DYNAMIC) Object returnValue) {
            tracer.onMethodExit(context.getMethodCache().get(executable), returnValue, throwable);
        }
    }
}
