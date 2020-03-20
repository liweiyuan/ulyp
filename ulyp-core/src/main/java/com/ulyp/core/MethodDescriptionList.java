package com.ulyp.core;

import com.google.protobuf.ByteString;
import com.ulyp.transport.BooleanType;
import com.ulyp.transport.SMethodDescriptionDecoder;
import com.ulyp.transport.SMethodDescriptionEncoder;

// Flexible SBE wrapper
public class MethodDescriptionList extends AbstractSbeRecordList<SMethodDescriptionEncoder, SMethodDescriptionDecoder> {

    public MethodDescriptionList() {
    }

    public MethodDescriptionList(ByteString bytes) {
        super(bytes);
    }

    public void add(MethodDescription methodDescription) {
        super.add(encoder -> {
            encoder.id(methodDescription.getId());
            encoder.className(methodDescription.getClassName());
            encoder.methodName(methodDescription.getMethodName());
            encoder.returnsSomething(methodDescription.returnsSomething() ? BooleanType.T : BooleanType.F);
        });
    }
}
