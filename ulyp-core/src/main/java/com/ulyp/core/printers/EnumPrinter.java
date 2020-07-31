package com.ulyp.core.printers;

import com.ulyp.core.TracingContext;
import com.ulyp.core.printers.bytes.BinaryOutput;

public class EnumPrinter extends ObjectBinaryPrinter {

    protected EnumPrinter(int id) {
        super(id);
    }

    @Override
    boolean supports(Type type) {
        return type.isEnum();
    }

    @Override
    public void write(Object obj, BinaryOutput out, TracingContext tracingContext) throws Exception {
        out.write(((Enum<?>) obj).name());
    }
}
