package com.ulyp.core.printers;

import com.ulyp.core.DecodingContext;
import com.ulyp.core.AgentRuntime;
import com.ulyp.core.printers.bytes.BinaryInput;
import com.ulyp.core.printers.bytes.BinaryOutput;

import java.util.Collection;

public class CollectionSizeOnlyPrinter extends ObjectBinaryPrinter {

    protected CollectionSizeOnlyPrinter(byte id) {
        super(id);
    }

    @Override
    boolean supports(TypeInfo typeInfo) {
        // used manually
        return false;
    }

    @Override
    public ObjectRepresentation read(TypeInfo classDescription, BinaryInput binaryInput, DecodingContext decodingContext) {
        long size = binaryInput.readLong();
        if (size == 0) {
            return new PlainObjectRepresentation(classDescription, classDescription.getSimpleName() + "{}");
        } else {
            return new PlainObjectRepresentation(classDescription, classDescription.getSimpleName() + "{" + size + "}");
        }
    }

    @Override
    public void write(Object obj, TypeInfo classDescription, BinaryOutput out, AgentRuntime agentRuntime) throws Exception {
        Collection<?> collection = (Collection<?>) obj;
        out.write(collection.size());
    }
}
