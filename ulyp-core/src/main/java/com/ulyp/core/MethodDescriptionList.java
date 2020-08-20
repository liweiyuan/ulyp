package com.ulyp.core;

import com.google.protobuf.ByteString;
import com.ulyp.transport.BooleanType;
import com.ulyp.transport.TMethodDescriptionDecoder;
import com.ulyp.transport.TMethodDescriptionEncoder;

import java.util.List;

// Flexible SBE wrapper
public class MethodDescriptionList extends AbstractSbeRecordList<TMethodDescriptionEncoder, TMethodDescriptionDecoder> {

    public MethodDescriptionList() {
    }

    public MethodDescriptionList(ByteString bytes) {
        super(bytes);
    }

    public void add(MethodDescription methodDescription) {
        super.add(encoder -> {
            encoder.id(methodDescription.getId());
            encoder.returnsSomething(methodDescription.returnsSomething() ? BooleanType.T : BooleanType.F);
            encoder.staticFlag(methodDescription.isStatic() ? BooleanType.T : BooleanType.F);

            encoder.parameterNamesCount(0);

            encoder.className(methodDescription.getDeclaringType().getName());
            encoder.methodName(methodDescription.getMethodName());
        });
    }
}
