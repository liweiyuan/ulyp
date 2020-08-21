package com.ulyp.core.printers;

import com.ulyp.core.DecodingContext;
import com.ulyp.core.AgentRuntime;
import com.ulyp.core.printers.bytes.BinaryInput;
import com.ulyp.core.printers.bytes.BinaryOutput;

public class ObjectArraySizePrinter extends ObjectBinaryPrinter {

    protected ObjectArraySizePrinter(byte id) {
        super(id);
    }

    @Override
    boolean supports(TypeInfo typeInfo) {
        // used manually
        return false;
    }

    @Override
    public ObjectRepresentation read(TypeInfo classDescription, BinaryInput binaryInput, DecodingContext decodingContext) {
        long itemsCount = binaryInput.readLong();
        if (itemsCount > 0) {
            return new PlainObjectRepresentation(classDescription, classDescription.getSimpleName() + "[" + itemsCount + " items ]");
        } else {
            return new PlainObjectRepresentation(classDescription, classDescription.getSimpleName());
        }
    }

    @Override
    public void write(Object obj, TypeInfo classDescription, BinaryOutput out, AgentRuntime agentRuntime) throws Exception {
        Object[] array = (Object[]) obj;
        out.write(array.length);
    }
}
