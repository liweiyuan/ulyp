package com.ulyp.core.printers;

import com.ulyp.core.ClassDescription;
import com.ulyp.core.DecodingContext;
import com.ulyp.core.AgentRuntime;
import com.ulyp.core.printers.bytes.BinaryInput;
import com.ulyp.core.printers.bytes.BinaryOutput;

import java.util.Collection;

public class CollectionSizeOnlyPrinter extends ObjectBinaryPrinter {

    protected CollectionSizeOnlyPrinter(int id) {
        super(id);
    }

    @Override
    boolean supports(Type type) {
        // used manually
        return false;
    }

    @Override
    public Printable read(ClassDescription classDescription, BinaryInput binaryInput, DecodingContext decodingContext) {
        long size = binaryInput.readLong();
        if (size == 0) {
            return () -> classDescription.getSimpleName() + "{}";
        } else {
            return () -> classDescription.getSimpleName() + "{" + size + "}";
        }
    }

    @Override
    public void write(Object obj, BinaryOutput out, AgentRuntime agentRuntime) throws Exception {
        Collection<?> collection = (Collection<?>) obj;
        out.write(collection.size());
    }
}
