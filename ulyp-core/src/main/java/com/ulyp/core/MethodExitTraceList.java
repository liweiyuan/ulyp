package com.ulyp.core;

import com.google.protobuf.ByteString;
import com.ulyp.core.printers.ObjectBinaryPrinter;
import com.ulyp.transport.BooleanType;
import com.ulyp.transport.SMethodExitTraceDecoder;
import com.ulyp.transport.SMethodExitTraceEncoder;

// Flexible SBE wrapper
public class MethodExitTraceList extends AbstractSbeRecordList<SMethodExitTraceEncoder, SMethodExitTraceDecoder> {

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
