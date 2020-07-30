package com.ulyp.agent;

import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.implementation.bytecode.constant.LongConstant;

import java.util.concurrent.atomic.AtomicLong;

public class InstrumentationIdFactory implements Advice.OffsetMapping.Factory<InstrumentationId> {

    private static final AtomicLong counter = new AtomicLong();

    @Override
    public Class<InstrumentationId> getAnnotationType() {
        return InstrumentationId.class;
    }

    @Override
    public Advice.OffsetMapping make(ParameterDescription.InDefinedShape target, AnnotationDescription.Loadable<InstrumentationId> annotation, AdviceType adviceType) {

        return new Advice.OffsetMapping.ForStackManipulation(
                LongConstant.forValue(counter.incrementAndGet()),
                TypeDescription.ForLoadedType.of(long.class).asGenericType(),
                target.getType(),
                Assigner.Typing.STATIC
        );
    }
}
