package com.ulyp.core.printers;

import com.ulyp.core.printers.bytes.BinaryOutput;

public class EnumPrinter extends ObjectBinaryPrinter {

    protected EnumPrinter(int id) {
        super(id);
    }

    @Override
    boolean supports(Class<?> clazz) {
        return clazz.isEnum();
    }

    @Override
    public void write(Object obj, BinaryOutput out) throws Exception {
        out.write(((Enum<?>) obj).name());
    }
}
