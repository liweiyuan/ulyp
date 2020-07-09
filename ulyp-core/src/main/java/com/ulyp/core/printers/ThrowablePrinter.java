package com.ulyp.core.printers;

import com.ulyp.core.ClassDescription;
import com.ulyp.core.DecodingContext;
import com.ulyp.core.TracingContext;
import com.ulyp.core.printers.bytes.BinaryInput;
import com.ulyp.core.printers.bytes.BinaryOutput;
import com.ulyp.core.printers.bytes.StringView;

public class ThrowablePrinter extends ObjectBinaryPrinter {

    protected ThrowablePrinter(int id) {
        super(id);
    }

    @Override
    boolean supports(Class<?> clazz) {
        return false;
    }

    @Override
    public Printable read(ClassDescription classDescription, BinaryInput binaryInput, DecodingContext decodingContext) {
        StringView msg = binaryInput.readString();
        return () -> classDescription.getSimpleName() + ": " + msg;
    }

    @Override
    public void write(Object obj, BinaryOutput out, TracingContext tracingContext) throws Exception {
        Throwable t = (Throwable) obj;
        out.write(t.getMessage());
    }
}
