package com.ulyp.core;

import com.google.protobuf.ByteString;
import com.ulyp.transport.BooleanType;
import com.ulyp.transport.TMethodDescriptionDecoder;
import com.ulyp.transport.TMethodDescriptionEncoder;

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
            encoder.className(methodDescription.getClassName());
            encoder.methodName(methodDescription.getMethodName());
            encoder.returnsSomething(methodDescription.returnsSomething() ? BooleanType.T : BooleanType.F);
        });
    }
}
