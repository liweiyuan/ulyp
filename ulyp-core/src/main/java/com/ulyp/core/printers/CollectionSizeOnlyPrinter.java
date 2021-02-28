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
    boolean supports(TypeInfo type) {
        // used manually
        return false;
    }

    @Override
    public ObjectRepresentation read(TypeInfo classDescription, BinaryInput input, DecodingContext decodingContext) {
        // TODO
        /*long size = input.readInt();
        if (size == 0) {
            return new PlainObjectRepresentation(classDescription, classDescription.getSimpleName() + "{}");
        } else {
            return new PlainObjectRepresentation(classDescription, classDescription.getSimpleName() + "{" + size + "}");
        }*/
        return null;
    }

    @Override
    public void write(Object object, TypeInfo classDescription, BinaryOutput out, AgentRuntime runtime) throws Exception {
        Collection<?> collection = (Collection<?>) object;
        out.writeInt(collection.size());
    }
}
