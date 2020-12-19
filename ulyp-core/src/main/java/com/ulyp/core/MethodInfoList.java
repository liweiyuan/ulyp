package com.ulyp.core;

import com.google.protobuf.ByteString;
import com.ulyp.transport.BooleanType;
import com.ulyp.transport.TMethodInfoDecoder;
import com.ulyp.transport.TMethodInfoEncoder;

// Flexible SBE wrapper
public class MethodInfoList extends AbstractBinaryEncodedList<TMethodInfoEncoder, TMethodInfoDecoder> {

    public MethodInfoList() {
    }

    public MethodInfoList(ByteString bytes) {
        super(bytes);
    }

    public void add(MethodInfo methodInfo) {
        super.add(encoder -> {
            encoder.id(methodInfo.getId());
            encoder.returnsSomething(methodInfo.returnsSomething() ? BooleanType.T : BooleanType.F);
            encoder.staticFlag(methodInfo.isStatic() ? BooleanType.T : BooleanType.F);
            encoder.constructor(methodInfo.isConstructor() ? BooleanType.T : BooleanType.F);

            // TODO delete
            encoder.parameterNamesCount(0);

            encoder.className(methodInfo.getDeclaringType().getName());
            encoder.methodName(methodInfo.getMethodName());
        });
    }
}
