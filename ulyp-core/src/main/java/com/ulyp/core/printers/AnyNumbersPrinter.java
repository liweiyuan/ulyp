package com.ulyp.core.printers;

import com.ulyp.core.AgentRuntime;
import com.ulyp.core.DecodingContext;
import com.ulyp.core.printers.bytes.BinaryInput;
import com.ulyp.core.printers.bytes.BinaryOutput;

public class AnyNumbersPrinter extends ObjectBinaryPrinter {

    protected AnyNumbersPrinter(byte id) {
        super(id);
    }

    @Override
    boolean supports(TypeInfo typeInfo) {
        return typeInfo.getTraits().contains(TypeTrait.NUMBER) || typeInfo.getTraits().contains(TypeTrait.PRIMITIVE);
    }

    @Override
    public ObjectRepresentation read(TypeInfo typeInfo, BinaryInput binaryInput, DecodingContext decodingContext) {
        return new NumberObjectRepresentation(typeInfo, binaryInput.readString());
    }

    @Override
    public void write(Object obj, TypeInfo typeInfo, BinaryOutput out, AgentRuntime agentRuntime) throws Exception {
        out.write(obj.toString());
    }
}
