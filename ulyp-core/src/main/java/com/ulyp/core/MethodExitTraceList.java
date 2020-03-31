package com.ulyp.core;

import com.google.protobuf.ByteString;
import com.ulyp.core.printers.ObjectBinaryPrinter;
import com.ulyp.core.printers.ObjectBinaryPrinterType;
import com.ulyp.core.printers.bytes.BinaryOutputForExitTraceImpl;
import com.ulyp.transport.BooleanType;
import com.ulyp.transport.TMethodExitTraceDecoder;
import com.ulyp.transport.TMethodExitTraceEncoder;

// Flexible SBE wrapper
public class MethodExitTraceList extends AbstractSbeRecordList<TMethodExitTraceEncoder, TMethodExitTraceDecoder> {

    private final BinaryOutputForExitTraceImpl binaryOutput = new BinaryOutputForExitTraceImpl();

    public MethodExitTraceList() {
    }

    public MethodExitTraceList(ByteString bytes) {
        super(bytes);
    }

    public void add(long callId, long methodId, boolean thrown, long returnValueClassId, ObjectBinaryPrinter returnValuePrinter, Object returnValue) {
        super.add(encoder -> {
            encoder.callId(callId);
            encoder.methodId(methodId);
            encoder.thrown(thrown ? BooleanType.T : BooleanType.F);
            encoder.returnClassId(returnValueClassId);

            ObjectBinaryPrinter printer = returnValue != null ? returnValuePrinter : ObjectBinaryPrinterType.NULL_PRINTER.getPrinter();

            encoder.returnPrinterId(printer.getId());
            binaryOutput.wrap(encoder);
            try {
                printer.write(returnValue, binaryOutput);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
