package com.ulyp.agent;

import com.ulyp.agent.settings.TracingStartMethodList;
import com.ulyp.agent.util.MethodRepresentationBuilder;
import net.bytebuddy.agent.builder.AgentBuilder.Transformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;

public class BbTransformer implements Transformer {

    private final Class<?> startTracingAdvice;
    private final Class<?> continueOnlyTracingAdvice;
    private final TracingStartMethodList tracingStartMethodList;

    public BbTransformer(
            Class<?> startTracingAdvice,
            Class<?> continueOnlyTracingAdvice,
            TracingStartMethodList tracingStartMethodList) {
        this.startTracingAdvice = startTracingAdvice;
        this.continueOnlyTracingAdvice = continueOnlyTracingAdvice;
        this.tracingStartMethodList = tracingStartMethodList;
    }

    @Override
    public DynamicType.Builder<?> transform(
            final DynamicType.Builder<?> builder,
            final TypeDescription typeDescription,
            final ClassLoader classLoader,
            final JavaModule module)
    {
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
                                            boolean shouldStartTracing = tracingStartMethodList.shouldStartTracing(
                                                    MethodRepresentationBuilder.newMethodDescription(desc));
//                                            if (shouldStartTracing) {
//                                                logger.debug("Should start tracing at {}.{}", typeDescription.getName(), desc.getActualName());
//                                            }
                                            return shouldStartTracing;
                                        }),

                                Advice.withCustomMapping()
                                        .bind(new MethodDescriptionFactory())
                                        .to(startTracingAdvice));

        final AsmVisitorWrapper methodsVisitor =
                new AsmVisitorWrapper.ForDeclaredMethods()
                        .method(ElementMatchers
                                        .isMethod()
                                        .and(ElementMatchers.not(ElementMatchers.isAbstract()))
                                        .and(ElementMatchers.not(ElementMatchers.isConstructor()))
                                        .and(ElementMatchers.not(ElementMatchers.isTypeInitializer()))
                                        .and(ElementMatchers.not(ElementMatchers.isToString()))
                                        .and(desc -> {
                                            boolean shouldTrace = !tracingStartMethodList.shouldStartTracing(
                                                    MethodRepresentationBuilder.newMethodDescription(desc)
                                            );
//                                            if (shouldTrace) {
//                                                logger.debug("Should trace at {}.{}", typeDescription.getName(), desc.getActualName());
//                                            }
                                            return shouldTrace;
                                        }),
                                Advice.withCustomMapping()
                                        .bind(new MethodDescriptionFactory())
                                        .to(continueOnlyTracingAdvice));

        return builder
                .visit(methodsStartVisitor)
                .visit(methodsVisitor);
    }
}
