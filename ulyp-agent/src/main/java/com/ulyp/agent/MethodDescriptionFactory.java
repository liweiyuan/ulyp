package com.ulyp.agent;

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

    static final MethodDescriptionDictionary methodDescriptionDictionary = AgentContext.getInstance().getMethodDescriptionDictionary();
    private static final AtomicLong counter = new AtomicLong();

    @Override
    public Class<MethodDescriptionValue> getAnnotationType() {
        return MethodDescriptionValue.class;
    }

    @Override
    public Advice.OffsetMapping make(ParameterDescription.InDefinedShape target, AnnotationDescription.Loadable<MethodDescriptionValue> annotation, AdviceType adviceType) {
        return ForMethodDescription.INSTANCE;
    }

    static class ForMethodDescription implements Advice.OffsetMapping {

        private static final ForMethodDescription INSTANCE = new ForMethodDescription();

        public Target resolve(TypeDescription instrumentedType,
                              MethodDescription instrumentedMethod,
                              Assigner assigner,
                              Advice.ArgumentHandler argumentHandler,
                              Sort sort) {
            long id = counter.incrementAndGet();
            methodDescriptionDictionary.initialize(id, MethodRepresentationBuilder.newMethodDescription(instrumentedMethod));
            return Target.ForStackManipulation.of(id);
        }
    }
}
