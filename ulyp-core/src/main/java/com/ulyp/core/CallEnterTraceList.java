package com.ulyp.core;

import com.google.protobuf.ByteString;
import com.ulyp.core.printers.ObjectBinaryPrinterType;
import com.ulyp.core.printers.bytes.BinaryOutputForEnterTraceImpl;
import com.ulyp.core.printers.ObjectBinaryPrinter;
import com.ulyp.transport.TCallEnterTraceDecoder;
import com.ulyp.transport.TCallEnterTraceEncoder;

// Flexible SBE wrapper
public class CallEnterTraceList extends AbstractSbeRecordList<TCallEnterTraceEncoder, TCallEnterTraceDecoder> {

    private final BinaryOutputForEnterTraceImpl binaryOutput = new BinaryOutputForEnterTraceImpl();

    public CallEnterTraceList() {
    }

    public CallEnterTraceList(ByteString bytes) {
        super(bytes);
    }

    public void add(
            long callId,
            long methodId,
            AgentRuntime agentRuntime,
            ObjectBinaryPrinter[] printers,
            Object[] args)
    {
        super.add(encoder -> {
            encoder.callId(callId);
            encoder.methodId(methodId);

            TCallEnterTraceEncoder.ArgumentsEncoder argumentsEncoder = encoder.argumentsCount(args.length);

            for (int i = 0; i < args.length; i++) {
                ObjectBinaryPrinter printer = args[i] != null ? printers[i] : ObjectBinaryPrinterType.NULL_PRINTER.getPrinter();

                argumentsEncoder = argumentsEncoder.next();
                argumentsEncoder.classId(agentRuntime.getClassId(args[i]));
                argumentsEncoder.printerId(printer.getId());
                binaryOutput.wrap(encoder);
                try {
                    printer.write(args[i], binaryOutput, agentRuntime);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
