package com.ulyp.core.printers;

import com.ulyp.core.DecodingContext;
import com.ulyp.core.AgentRuntime;
import com.ulyp.core.printers.bytes.BinaryInput;
import com.ulyp.core.printers.bytes.BinaryOutput;

public class ThrowablePrinter extends ObjectBinaryPrinter {

    private static final int MAX_LENGTH = 200;

    protected ThrowablePrinter(byte id) {
        super(id);
    }

    @Override
    boolean supports(TypeInfo type) {
        // TODO maybe implement
        return false;
    }

    @Override
    public ObjectRepresentation read(TypeInfo classDescription, BinaryInput input, DecodingContext decodingContext) {
        return new PlainObjectRepresentation(
                classDescription,
                classDescription.getName() + ": " + input.readString()
        );
    }

    @Override
    public void write(Object object, TypeInfo classDescription, BinaryOutput out, AgentRuntime runtime) throws Exception {
        Throwable t = (Throwable) object;
        out.writeString(t.getMessage());
    }
}
