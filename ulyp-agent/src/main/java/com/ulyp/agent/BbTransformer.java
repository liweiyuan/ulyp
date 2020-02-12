package com.ulyp.agent;

import com.ulyp.agent.util.Log;
import net.bytebuddy.agent.builder.AgentBuilder.Transformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.utility.JavaModule;

import java.lang.reflect.Executable;

public class BbTransformer implements Transformer {

    public static final ProgramContext context = new RuntimeAgentContext();
    public static final Log log = context.getLog();
    public static final Settings settings = context.getSettings();
    @SuppressWarnings("unused")
    public static final Tracer stack = new Tracer(context);

    @Override
    public DynamicType.Builder<?> transform(
            final DynamicType.Builder<?> builder,
            final TypeDescription typeDescription,
            final ClassLoader classLoader,
            final JavaModule module) {

        if (typeDescription.isInterface()) {
            return builder;
        }
        /*log.log(() -> "Will instrument the following class " + typeDescription.getName());*/

        final AsmVisitorWrapper methodsStartVisitor =
                new AsmVisitorWrapper.ForDeclaredMethods()
                        .method(desc -> {
                            if (desc.isConstructor() || desc.isTypeInitializer()) {
                                return false;
                            }
                            boolean shouldStart = !settings.shouldStartAndLogArguments(desc) && settings.shouldStartTracing(desc);
                            if (shouldStart) {
                                log.log(() -> "Should start tracing at " + typeDescription.getName() + "." + desc.getActualName());
                            }
                            return shouldStart;
                        }, Advice.to(StartMethodAdvice.class));

        final AsmVisitorWrapper methodsLogStartVisitor =
                new AsmVisitorWrapper.ForDeclaredMethods()
                        .method(desc -> {
                            if (desc.isConstructor() || desc.isTypeInitializer()) {
                                return false;
                            }
                            return settings.shouldStartTracing(desc) && settings.shouldStartAndLogArguments(desc);
                        }, Advice.to(StartMethodAdviceWithLoggedArg.class));

        final AsmVisitorWrapper methodsVisitor =
                new AsmVisitorWrapper.ForDeclaredMethods()
                        .method(desc -> {
                            if (desc.isConstructor() || desc.isTypeInitializer()) {
                                return false;
                            }
                            return !settings.shouldStartAndLogArguments(desc) && !settings.shouldStartTracing(desc);
                        }, Advice.to(MethodAdvice.class));

        final AsmVisitorWrapper methodsWithLogVisitor =
                new AsmVisitorWrapper.ForDeclaredMethods()
                        .method(desc -> {
                            if (desc.isConstructor() || desc.isTypeInitializer()) {
                                return false;
                            }
                            boolean shouldPush = !settings.shouldStartTracing(desc) && settings.shouldStartAndLogArguments(desc);
                            /*if (shouldPush) {
                                log.log(() -> "Should LOG upon " + typeDescription.getName() + "." + desc.getActualName());
                            }*/
                            return shouldPush;
                        }, Advice.to(MethodAdviceWithLoggedArg.class));

        return builder
                .visit(methodsStartVisitor)
                .visit(methodsLogStartVisitor)
                .visit(methodsVisitor)
                .visit(methodsWithLogVisitor);
    }

    public static class StartMethodAdviceWithLoggedArg {

        @Advice.OnMethodEnter
        static void enter(
                @Advice.Origin Executable executable,
                @Advice.AllArguments Object[] arguments) {
            stack.startOrContinueTracing(context.getMethodCache().get(executable), arguments);
        }

        @Advice.OnMethodExit(onThrowable = Throwable.class)
        static void exit(
                @Advice.Origin Executable executable,
                @Advice.Return(typing = Assigner.Typing.DYNAMIC) Object returnValue,
                @Advice.Thrown Throwable throwable) {
            stack.endTracingIfPossible(context.getMethodCache().get(executable), returnValue, throwable);
        }
    }

    public static class StartMethodAdvice {

        @Advice.OnMethodEnter
        static void enter(
                @Advice.Origin Executable executable,
                @Advice.AllArguments Object[] arguments) {
            stack.startOrContinueTracing(context.getMethodCache().get(executable), arguments);
        }

        @Advice.OnMethodExit(onThrowable = Throwable.class)
        static void exit(
                @Advice.Origin Executable executable,
                @Advice.Return(typing = Assigner.Typing.DYNAMIC) Object returnValue,
                @Advice.Thrown Throwable throwable) {
            stack.endTracingIfPossible(context.getMethodCache().get(executable), returnValue, throwable);
        }
    }

    public static class MethodAdviceWithLoggedArg {

        @Advice.OnMethodEnter
        static void enter(
                @Advice.Origin Executable executable,
                @Advice.AllArguments Object[] arguments) {
            stack.push(context.getMethodCache().get(executable), arguments);
        }

        @Advice.OnMethodExit(onThrowable = Throwable.class)
        static void exit(
                @Advice.Origin Executable executable,
                @Advice.Thrown Throwable throwable,
                @Advice.Return(typing = Assigner.Typing.DYNAMIC) Object returnValue) {
            stack.pop(context.getMethodCache().get(executable), returnValue, throwable);
        }
    }

    public static class MethodAdvice {

        @Advice.OnMethodEnter
        static void enter(
                @Advice.Origin Executable executable,
                @Advice.AllArguments Object[] arguments) {
            stack.push(context.getMethodCache().get(executable), arguments);
        }

        @Advice.OnMethodExit(onThrowable = Throwable.class)
        static void exit(
                @Advice.Origin Executable executable,
                @Advice.Thrown Throwable throwable,
                @Advice.Return(typing = Assigner.Typing.DYNAMIC) Object returnValue) {
            stack.pop(context.getMethodCache().get(executable), returnValue, throwable);
        }
    }
}
