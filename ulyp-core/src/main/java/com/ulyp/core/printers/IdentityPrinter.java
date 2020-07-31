package com.ulyp.core.printers;

import com.ulyp.core.ClassDescription;
import com.ulyp.core.DecodingContext;
import com.ulyp.core.TracingContext;
import com.ulyp.core.printers.bytes.BinaryInput;
import com.ulyp.core.printers.bytes.BinaryOutput;

public class IdentityPrinter extends ObjectBinaryPrinter {

    protected IdentityPrinter(int id) {
        super(id);
    }

    @Override
    boolean supports(Type type) {
        return true;
    }

    @Override
    public Printable read(ClassDescription classDescription, BinaryInput binaryInput, DecodingContext decodingContext) {
        long identityHashCode = binaryInput.readLong();
        return () -> classDescription.getSimpleName() + "@" + identityHashCode;
    }

    @Override
    public void write(Object obj, BinaryOutput out, TracingContext tracingContext) throws Exception {
        out.write(System.identityHashCode(obj));
    }
}
