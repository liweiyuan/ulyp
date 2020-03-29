package com.ulyp.core;

import com.google.protobuf.ByteString;
import com.ulyp.core.printers.ObjectBinaryPrinter;
import com.ulyp.transport.TMethodEnterTraceDecoder;
import com.ulyp.transport.TMethodEnterTraceEncoder;

// Flexible SBE wrapper
public class MethodEnterTraceList extends AbstractSbeRecordList<TMethodEnterTraceEncoder, TMethodEnterTraceDecoder> {

    public MethodEnterTraceList() {
    }

    public MethodEnterTraceList(ByteString bytes) {
        super(bytes);
    }

    public void add(long callId, long methodId, long[] argsClassIds, ObjectBinaryPrinter[] printers, Object[] args) {
        super.add(encoder -> {
            encoder.callId(callId);
            encoder.methodId(methodId);

            TMethodEnterTraceEncoder.ArgumentsEncoder argumentsEncoder = encoder.argumentsCount(args.length);

            for (int i = 0; i < args.length; i++) {
                argumentsEncoder = argumentsEncoder.next();
                argumentsEncoder.classId(argsClassIds[i]);
                printers[i].write(args[i], argumentsEncoder::value);
            }
        });
    }
}
