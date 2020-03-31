package com.ulyp.core.printers;

import com.ulyp.core.ClassDescription;
import com.ulyp.core.printers.bytes.BinaryInput;
import com.ulyp.core.printers.bytes.BinaryOutput;

public class ThrowablePrinter extends ObjectBinaryPrinter {

    protected ThrowablePrinter(int id) {
        super(id);
    }

    @Override
    boolean supports(Class<?> clazz) {
        return false;
    }

    @Override
    public String read(ClassDescription classDescription, BinaryInput binaryInput) {
        return classDescription.getSimpleName() + ": " + binaryInput.readString();
    }

    @Override
    public void write(Object obj, BinaryOutput out) throws Exception {
        Throwable t = (Throwable) obj;
        out.write(t.getMessage());
    }
}
