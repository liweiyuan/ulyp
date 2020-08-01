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

            List<String> argumentNames = methodDescription.getParameterNames();
            TMethodDescriptionEncoder.ParameterNamesEncoder paramNamesEncoder = encoder.parameterNamesCount(argumentNames.size());
            for (String argumentName : argumentNames) {
                paramNamesEncoder = paramNamesEncoder.next();
                paramNamesEncoder.value(argumentName);
            }

            encoder.className(methodDescription.getDeclaringType().getName());
            encoder.methodName(methodDescription.getMethodName());
        });
    }
}
