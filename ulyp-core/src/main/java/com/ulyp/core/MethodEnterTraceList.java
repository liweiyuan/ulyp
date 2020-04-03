package com.ulyp.core;

import com.google.protobuf.ByteString;
import com.ulyp.core.printers.ObjectBinaryPrinterType;
import com.ulyp.core.printers.bytes.BinaryOutputForEnterTraceImpl;
import com.ulyp.core.printers.ObjectBinaryPrinter;
import com.ulyp.transport.TMethodEnterTraceDecoder;
import com.ulyp.transport.TMethodEnterTraceEncoder;
import org.agrona.LangUtil;

// Flexible SBE wrapper
public class MethodEnterTraceList extends AbstractSbeRecordList<TMethodEnterTraceEncoder, TMethodEnterTraceDecoder> {

    private final BinaryOutputForEnterTraceImpl binaryOutput = new BinaryOutputForEnterTraceImpl();

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
                ObjectBinaryPrinter printer = args[i] != null ? printers[i] : ObjectBinaryPrinterType.NULL_PRINTER.getPrinter();

                argumentsEncoder = argumentsEncoder.next();
                argumentsEncoder.classId(argsClassIds[i]);
                argumentsEncoder.printerId(printer.getId());
                binaryOutput.wrap(encoder);
                try {
                    printer.write(args[i], binaryOutput);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
