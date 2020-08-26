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
    boolean supports(TypeInfo type) {
        // used manually
        return false;
    }

    @Override
    public ObjectRepresentation read(TypeInfo classDescription, BinaryInput input, DecodingContext decodingContext) {
        int itemsCount = input.readInt();
        if (itemsCount > 0) {
            return new PlainObjectRepresentation(classDescription, classDescription.getSimpleName() + "[" + itemsCount + " items ]");
        } else {
            return new PlainObjectRepresentation(classDescription, classDescription.getSimpleName());
        }
    }

    @Override
    public void write(Object object, TypeInfo classDescription, BinaryOutput out, AgentRuntime runtime) throws Exception {
        Object[] array = (Object[]) object;
        out.writeInt(array.length);
    }
}
