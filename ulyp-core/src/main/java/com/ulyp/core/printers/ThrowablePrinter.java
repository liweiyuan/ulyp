package com.ulyp.core.printers;

import com.ulyp.core.DecodingContext;
import com.ulyp.core.AgentRuntime;
import com.ulyp.core.printers.bytes.BinaryInput;
import com.ulyp.core.printers.bytes.BinaryOutput;

public class ThrowablePrinter extends ObjectBinaryPrinter {

    protected ThrowablePrinter(byte id) {
        super(id);
    }

    @Override
    boolean supports(TypeInfo typeInfo) {
        // TODO maybe implement
        return false;
    }

    @Override
    public ObjectRepresentation read(TypeInfo classDescription, BinaryInput binaryInput, DecodingContext decodingContext) {
        return new PlainObjectRepresentation(classDescription, classDescription.getSimpleName() + ": " + binaryInput.readString());
    }

    @Override
    public void write(Object obj, TypeInfo classDescription, BinaryOutput out, AgentRuntime agentRuntime) throws Exception {
        Throwable t = (Throwable) obj;
        out.write(t.getMessage());
    }
}
