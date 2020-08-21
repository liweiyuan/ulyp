package com.ulyp.core;

import com.google.protobuf.ByteString;
import com.ulyp.core.printers.ObjectBinaryPrinter;
import com.ulyp.core.printers.ObjectBinaryPrinterType;
import com.ulyp.core.printers.TypeInfo;
import com.ulyp.core.printers.bytes.BinaryOutputForEnterRecordImpl;
import com.ulyp.transport.TCallEnterRecordDecoder;
import com.ulyp.transport.TCallEnterRecordEncoder;

// Flexible SBE wrapper
public class CallEnterRecordList extends AbstractSbeRecordList<TCallEnterRecordEncoder, TCallEnterRecordDecoder> {

    private final BinaryOutputForEnterRecordImpl binaryOutput = new BinaryOutputForEnterRecordImpl();

    public CallEnterRecordList() {
    }

    public CallEnterRecordList(ByteString bytes) {
        super(bytes);
    }

    public void add(
            int callId,
            int methodId,
            AgentRuntime agentRuntime,
            ObjectBinaryPrinter[] printers,
            Object callee,
            Object[] args)
    {
        super.add(encoder -> {
            encoder.callId(callId);
            encoder.methodId(methodId);

            TCallEnterRecordEncoder.ArgumentsEncoder argumentsEncoder = encoder.argumentsCount(args.length);

            for (int i = 0; i < args.length; i++) {
                ObjectBinaryPrinter printer = args[i] != null ? printers[i] : ObjectBinaryPrinterType.NULL_PRINTER.getPrinter();

                TypeInfo argTypeInfo = agentRuntime.get(args[i]);

                argumentsEncoder = argumentsEncoder.next();
                argumentsEncoder.classId(argTypeInfo.getId());
                argumentsEncoder.printerId(printer.getId());
                binaryOutput.wrap(encoder);
                try {
                    printer.write(args[i], binaryOutput, agentRuntime);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            ObjectBinaryPrinter printer = callee != null ? ObjectBinaryPrinterType.IDENTITY_PRINTER.getPrinter() : ObjectBinaryPrinterType.NULL_PRINTER.getPrinter();

            encoder.calleeClassId(agentRuntime.get(callee).getId());
            encoder.calleePrinterId(printer.getId());
            binaryOutput.wrap(encoder);
            try {
                printer.write(callee, binaryOutput, agentRuntime);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
