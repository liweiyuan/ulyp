package com.ulyp.core.printers;

import com.ulyp.core.ClassDescription;
import com.ulyp.core.printers.bytes.BinaryInput;
import com.ulyp.core.printers.bytes.BinaryOutput;

public class NullObjectPrinter extends ObjectBinaryPrinter {

    protected NullObjectPrinter(int id) {
        super(id);
    }

    @Override
    boolean supports(Class<?> clazz) {
        return false;
    }

    @Override
    public String read(ClassDescription classDescription, BinaryInput binaryInput) {
        return "null";
    }

    @Override
    public void write(Object obj, BinaryOutput out) {
        out.write(0);
    }
}
