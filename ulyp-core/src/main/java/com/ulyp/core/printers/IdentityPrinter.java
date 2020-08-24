package com.ulyp.core.printers;

import com.ulyp.core.DecodingContext;
import com.ulyp.core.AgentRuntime;
import com.ulyp.core.printers.bytes.BinaryInput;
import com.ulyp.core.printers.bytes.BinaryOutput;

public class IdentityPrinter extends ObjectBinaryPrinter {

    protected IdentityPrinter(byte id) {
        super(id);
    }

    @Override
    boolean supports(TypeInfo typeInfo) {
        return true;
    }

    @Override
    public ObjectRepresentation read(TypeInfo typeInfo, BinaryInput binaryInput, DecodingContext decodingContext) {
        int identityHashCode = binaryInput.readInt();
        return new IdentityObjectRepresentation(typeInfo, identityHashCode);
    }

    @Override
    public void write(Object obj, TypeInfo typeInfo, BinaryOutput out, AgentRuntime agentRuntime) throws Exception {
        out.writeInt(System.identityHashCode(obj));
    }
}
