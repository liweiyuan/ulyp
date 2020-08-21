package com.ulyp.core.printers;

import com.ulyp.core.DecodingContext;
import com.ulyp.core.AgentRuntime;
import com.ulyp.core.printers.bytes.BinaryInput;
import com.ulyp.core.printers.bytes.BinaryOutput;
import com.ulyp.core.printers.bytes.BinaryOutputAppender;

public class ToStringPrinter extends ObjectBinaryPrinter {

    private static final int TO_STRING_CALL_SUCCESS = 1;
    private static final int TO_STRING_CALL_NULL = 2;
    private static final int TO_STRING_CALL_FAIL = 0;

    protected ToStringPrinter(byte id) {
        super(id);
    }

    @Override
    boolean supports(TypeInfo typeInfo) {
        if (typeInfo.isExactlyJavaLangObject()) {
            return false;
        }

        return typeInfo.hasToStringMethod();
    }

    @Override
    public ObjectRepresentation read(TypeInfo typeInfo, BinaryInput binaryInput, DecodingContext decodingContext) {
        long result = binaryInput.readLong();
        if (result == TO_STRING_CALL_SUCCESS) {
            // if StringObject representation is returned, then it will look as String literal in UI (green text with double quotes)
            StringObjectRepresentation string = (StringObjectRepresentation) ObjectBinaryPrinterType.STRING_PRINTER.getPrinter().read(typeInfo, binaryInput, decodingContext);
            return new PlainObjectRepresentation(typeInfo, string.getPrintedText());
        } else if (result == TO_STRING_CALL_NULL) {
            return new NullObjectRepresentation(typeInfo);
        } else {
            return ObjectBinaryPrinterType.IDENTITY_PRINTER.getPrinter().read(typeInfo, binaryInput, decodingContext);
        }
    }

    @Override
    public void write(Object obj, TypeInfo classDescription, BinaryOutput out, AgentRuntime agentRuntime) throws Exception {
        try {
            String printed = obj.toString();
            if (printed != null) {
                try (BinaryOutputAppender appender = out.appender()) {
                    appender.append(TO_STRING_CALL_SUCCESS);
                    ObjectBinaryPrinterType.STRING_PRINTER.getPrinter().write(printed, appender, agentRuntime);
                }
            } else {
                try (BinaryOutputAppender appender = out.appender()) {
                    appender.append(TO_STRING_CALL_NULL);
                }
            }
        } catch (Throwable e) {
            try (BinaryOutputAppender appender = out.appender()) {
                appender.append(TO_STRING_CALL_FAIL);
                ObjectBinaryPrinterType.IDENTITY_PRINTER.getPrinter().write(obj, appender, agentRuntime);
            }
        }
    }
}
