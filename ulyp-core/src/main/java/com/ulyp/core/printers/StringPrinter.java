package com.ulyp.core.printers;

import com.ulyp.core.AgentRuntime;
import com.ulyp.core.DecodingContext;
import com.ulyp.core.printers.bytes.BinaryInput;
import com.ulyp.core.printers.bytes.BinaryOutput;

public class StringPrinter extends ObjectBinaryPrinter {

    private static final int MAX_LENGTH = 400;

    protected StringPrinter(byte id) {
        super(id);
    }

    @Override
    boolean supports(TypeInfo typeInfo) {
        return typeInfo.isExactlyJavaLangString();
    }

    @Override
    public ObjectRepresentation read(TypeInfo typeInfo, BinaryInput binaryInput, DecodingContext decodingContext) {
        return new StringObjectRepresentation(typeInfo, binaryInput.readString());
    }

    @Override
    public void write(Object obj, TypeInfo classDescription, BinaryOutput out, AgentRuntime agentRuntime) throws Exception {
        String text = (String) obj;
        String printed;
        if (text.length() > MAX_LENGTH) {
            printed = text.substring(0, MAX_LENGTH) + "...(" + text.length() + ")";
        } else {
            printed = text;
        }
        out.write(printed);
    }
}
