package com.ulyp.core.printers;

import com.ulyp.core.AgentRuntime;
import com.ulyp.core.DecodingContext;
import com.ulyp.core.printers.bytes.BinaryInput;
import com.ulyp.core.printers.bytes.BinaryOutput;

// Handles everything including byte/short/int/long
public class IntegralPrinter extends ObjectBinaryPrinter {

    protected IntegralPrinter(byte id) {
        super(id);
    }

    @Override
    boolean supports(TypeInfo typeInfo) {
        return typeInfo.getTraits().contains(TypeTrait.INTEGRAL);
    }

    @Override
    public ObjectRepresentation read(TypeInfo typeInfo, BinaryInput binaryInput, DecodingContext decodingContext) {
        return new NumberObjectRepresentation(typeInfo, String.valueOf(binaryInput.readLong()));
    }

    @Override
    public void write(Object obj, TypeInfo typeInfo, BinaryOutput out, AgentRuntime agentRuntime) throws Exception {
        Number number = (Number) obj;
        out.write(number.longValue());
    }
}
