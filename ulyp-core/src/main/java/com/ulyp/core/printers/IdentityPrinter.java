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
    boolean supports(TypeInfo type) {
        return true;
    }

    @Override
    public ObjectRepresentation read(TypeInfo objectType, BinaryInput input, DecodingContext decodingContext) {
        int identityHashCode = input.readInt();
        return new IdentityObjectRepresentation(objectType, identityHashCode);
    }

    @Override
    public void write(Object object, TypeInfo objectType, BinaryOutput out, AgentRuntime runtime) throws Exception {
        out.writeInt(System.identityHashCode(object));
    }
}
