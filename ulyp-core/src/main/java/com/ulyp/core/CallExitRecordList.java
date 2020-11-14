package com.ulyp.core;

import com.google.protobuf.ByteString;
import com.ulyp.core.printers.ObjectBinaryPrinter;
import com.ulyp.core.printers.ObjectBinaryPrinterType;
import com.ulyp.core.printers.TypeInfo;
import com.ulyp.core.printers.bytes.BinaryOutputForExitRecordImpl;
import com.ulyp.transport.BooleanType;
import com.ulyp.transport.TCallExitRecordDecoder;
import com.ulyp.transport.TCallExitRecordEncoder;

// Flexible SBE wrapper
public class CallExitRecordList extends AbstractBinaryEncodedList<TCallExitRecordEncoder, TCallExitRecordDecoder> {

    private final BinaryOutputForExitRecordImpl binaryOutput = new BinaryOutputForExitRecordImpl();

    public CallExitRecordList() {
    }

    public CallExitRecordList(ByteString bytes) {
        super(bytes);
    }

    public void add(
            long callId,
            int methodId,
            AgentRuntime agentRuntime,
            boolean thrown,
            ObjectBinaryPrinter returnValuePrinter,
            Object returnValue)
    {
        super.add(encoder -> {
            encoder.callId(callId);
            encoder.methodId(methodId);
            encoder.thrown(thrown ? BooleanType.T : BooleanType.F);
            TypeInfo classDescription = agentRuntime.get(returnValue);
            encoder.returnClassId(classDescription.getId());

            ObjectBinaryPrinter printer = returnValue != null ? returnValuePrinter : ObjectBinaryPrinterType.NULL_PRINTER.getInstance();

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
