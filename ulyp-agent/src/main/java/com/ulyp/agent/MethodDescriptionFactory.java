package com.ulyp.agent;

import com.ulyp.agent.settings.RecordingStartMethodList;
import com.ulyp.agent.util.MethodInfoBuilder;
import com.ulyp.core.MethodDescriptionMap;
import com.ulyp.core.MethodInfo;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.bytecode.assign.Assigner;

public class MethodDescriptionFactory implements Advice.OffsetMapping.Factory<MethodDescriptionValue> {

    static final MethodDescriptionMap methodDescriptionMap = MethodDescriptionMap.getInstance();

    private final ForMethodIdOffsetMapping instance;

    public MethodDescriptionFactory(RecordingStartMethodList recordingStartMethodList) {
        this.instance = new ForMethodIdOffsetMapping(recordingStartMethodList);
    }

    @Override
    public Class<MethodDescriptionValue> getAnnotationType() {
        return MethodDescriptionValue.class;
    }

    @Override
    public Advice.OffsetMapping make(ParameterDescription.InDefinedShape target, AnnotationDescription.Loadable<MethodDescriptionValue> annotation, AdviceType adviceType) {
        return instance;
    }

    private static class IdMapping {

        private final MethodDescription instrumentedMethod;
        private final int methodId;

        IdMapping(MethodDescription instrumentedMethod, int methodId) {
            this.instrumentedMethod = instrumentedMethod;
            this.methodId = methodId;
        }
    }

    static class ForMethodIdOffsetMapping implements Advice.OffsetMapping {

        private final ThreadLocal<IdMapping> pa = new ThreadLocal<>();

        private final RecordingStartMethodList recordingStartMethodList;

        ForMethodIdOffsetMapping(RecordingStartMethodList recordingStartMethodList) {
            this.recordingStartMethodList = recordingStartMethodList;
        }

        public Target resolve(TypeDescription instrumentedType,
                              MethodDescription instrumentedMethod,
                              Assigner assigner,
                              Advice.ArgumentHandler argumentHandler,
                              Sort sort) {
            /*
             * Bytebuddy calls this method for enter and exit advice methods. Which means mapping to id and building to method info
             * could be done only once for enter advice. So we store last mapped instrumented method and reuse id if possible.
             * This gives small, but noticeable ~5% overall performance boost.
             */
            IdMapping idMapping = pa.get();
            int id;
            if (idMapping != null && idMapping.instrumentedMethod == instrumentedMethod) {
                pa.set(null);
                id = idMapping.methodId;
            } else {
                MethodInfo methodInfo = MethodInfoBuilder.newMethodDescription(instrumentedMethod);
                id = methodDescriptionMap.putAndGetId(methodInfo, recordingStartMethodList.shouldStartRecording(methodInfo));
                pa.set(new IdMapping(instrumentedMethod, id));
            }

            return Target.ForStackManipulation.of(id);
        }
    }
}
