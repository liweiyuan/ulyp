package com.ulyp.agent;

import com.ulyp.agent.settings.TracingStartMethodList;
import com.ulyp.agent.util.MethodRepresentationBuilder;
import com.ulyp.core.MethodDescriptionDictionary;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.bytecode.assign.Assigner;

import java.util.concurrent.atomic.AtomicLong;

public class MethodDescriptionFactory implements Advice.OffsetMapping.Factory<MethodDescriptionValue> {

    static final MethodDescriptionDictionary methodDescriptionDictionary = MethodDescriptionDictionary.getInstance();
    private static final AtomicLong counter = new AtomicLong(0);
    private static final AtomicLong startTraceCounter = new AtomicLong(0);

    private final ForMethodDescription instance;

    public MethodDescriptionFactory(TracingStartMethodList tracingStartMethodList) {
        this.instance = new ForMethodDescription(tracingStartMethodList);
    }

    @Override
    public Class<MethodDescriptionValue> getAnnotationType() {
        return MethodDescriptionValue.class;
    }

    @Override
    public Advice.OffsetMapping make(ParameterDescription.InDefinedShape target, AnnotationDescription.Loadable<MethodDescriptionValue> annotation, AdviceType adviceType) {
        return instance;
    }

    static class ForMethodDescription implements Advice.OffsetMapping {

        private final TracingStartMethodList tracingStartMethodList;

        ForMethodDescription(TracingStartMethodList tracingStartMethodList) {
            this.tracingStartMethodList = tracingStartMethodList;
        }

        public Target resolve(TypeDescription instrumentedType,
                              MethodDescription instrumentedMethod,
                              Assigner assigner,
                              Advice.ArgumentHandler argumentHandler,
                              Sort sort) {
            long id;
            com.ulyp.core.MethodDescription methodDescription = MethodRepresentationBuilder.newMethodDescription(instrumentedMethod);
            if (tracingStartMethodList.shouldStartTracing(methodDescription)) {
                id = startTraceCounter.decrementAndGet();
            } else {
                id = counter.incrementAndGet();
            }
            methodDescriptionDictionary.put(id, methodDescription);
            return Target.ForStackManipulation.of(id);
        }
    }
}
