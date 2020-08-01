package com.ulyp.core;

import com.google.protobuf.ByteString;
import com.ulyp.core.printers.ObjectBinaryPrinter;
import com.ulyp.core.printers.ObjectBinaryPrinterType;
import com.ulyp.core.printers.bytes.BinaryOutputForExitTraceImpl;
import com.ulyp.transport.BooleanType;
import com.ulyp.transport.TCallExitTraceDecoder;
import com.ulyp.transport.TCallExitTraceEncoder;

// Flexible SBE wrapper
public class CallExitTraceList extends AbstractSbeRecordList<TCallExitTraceEncoder, TCallExitTraceDecoder> {

    private final BinaryOutputForExitTraceImpl binaryOutput = new BinaryOutputForExitTraceImpl();

    public CallExitTraceList() {
    }

    public CallExitTraceList(ByteString bytes) {
        super(bytes);
    }

    public void add(
            long callId,
            long methodId,
            AgentRuntime agentRuntime,
            boolean thrown,
            long returnValueClassId,
            ObjectBinaryPrinter returnValuePrinter,
            Object returnValue)
    {
        super.add(encoder -> {
            encoder.callId(callId);
            encoder.methodId(methodId);
            encoder.thrown(thrown ? BooleanType.T : BooleanType.F);
            encoder.returnClassId(returnValueClassId);

            ObjectBinaryPrinter printer = returnValue != null ? returnValuePrinter : ObjectBinaryPrinterType.NULL_PRINTER.getPrinter();

            encoder.returnPrinterId(printer.getId());
            binaryOutput.wrap(encoder);
            try {
                printer.write(returnValue, binaryOutput, agentRuntime);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
