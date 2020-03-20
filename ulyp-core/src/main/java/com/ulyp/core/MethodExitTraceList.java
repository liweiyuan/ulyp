package com.ulyp.core;

import com.google.protobuf.ByteString;
import com.ulyp.core.printers.ObjectBinaryPrinter;
import com.ulyp.transport.BooleanType;
import com.ulyp.transport.TMethodExitTraceDecoder;
import com.ulyp.transport.TMethodExitTraceEncoder;

// Flexible SBE wrapper
public class MethodExitTraceList extends AbstractSbeRecordList<TMethodExitTraceEncoder, TMethodExitTraceDecoder> {

    public MethodExitTraceList() {
    }

    public MethodExitTraceList(ByteString bytes) {
        super(bytes);
    }

    public void add(long callId, long methodId, boolean thrown, ObjectBinaryPrinter printer, Object result) {
        super.add(encoder -> {
            encoder.callId(callId);
            encoder.methodId(methodId);
            encoder.thrown(thrown ? BooleanType.T : BooleanType.F);
            printer.write(result, encoder::returnValue);
        });
    }
}
