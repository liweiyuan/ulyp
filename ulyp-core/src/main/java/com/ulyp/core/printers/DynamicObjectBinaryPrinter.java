package com.ulyp.core.printers;

import com.ulyp.core.DecodingContext;
import com.ulyp.core.AgentRuntime;
import com.ulyp.core.printers.bytes.BinaryInput;
import com.ulyp.core.printers.bytes.BinaryOutput;
import com.ulyp.core.printers.bytes.BinaryOutputAppender;

public class DynamicObjectBinaryPrinter extends ObjectBinaryPrinter {

    protected DynamicObjectBinaryPrinter(byte id) {
        super(id);
    }

    @Override
    boolean supports(TypeInfo typeInfo) {
        return typeInfo.isInterface() || typeInfo.isExactlyJavaLangObject() || typeInfo.isTypeVar();
    }

    @Override
    public ObjectRepresentation read(TypeInfo typeInfo, BinaryInput binaryInput, DecodingContext decodingContext) {
        byte printerId = binaryInput.readByte();
        return ObjectBinaryPrinterType.printerForId(printerId).read(typeInfo, binaryInput, decodingContext);
    }

    @Override
    public void write(Object obj, TypeInfo typeInfo, BinaryOutput out, AgentRuntime agentRuntime) throws Exception {
        ObjectBinaryPrinter printer = typeInfo.getSuggestedPrinter();
        if (printer.getId() == getId()) {
            printer = ObjectBinaryPrinterType.IDENTITY_PRINTER.getInstance();
        }

        try (BinaryOutputAppender appender = out.appender()) {
            appender.append(printer.getId());
            printer.write(obj, appender, agentRuntime);
        }
    }
}
