package com.ulyp.core;

import com.google.protobuf.ByteString;
import com.ulyp.core.printers.ObjectBinaryPrinter;
import com.ulyp.transport.SMethodEnterTraceDecoder;
import com.ulyp.transport.SMethodEnterTraceEncoder;

// Flexible SBE wrapper
public class MethodEnterTraceList extends AbstractSbeRecordList<SMethodEnterTraceEncoder, SMethodEnterTraceDecoder> {

    public MethodEnterTraceList() {
    }

    public MethodEnterTraceList(ByteString bytes) {
        super(bytes);
    }

    public void add(long callId, long methodId, ObjectBinaryPrinter[] printers, Object[] args) {
        super.add(encoder -> {
            encoder.callId(callId);
            encoder.methodId(methodId);

            SMethodEnterTraceEncoder.ArgumentsEncoder argumentsEncoder = encoder.argumentsCount(args.length);

            for (int i = 0; i < args.length; i++) {
                argumentsEncoder = argumentsEncoder.next();
                printers[i].write(args[i], argumentsEncoder::value);
            }
        });
    }
}
