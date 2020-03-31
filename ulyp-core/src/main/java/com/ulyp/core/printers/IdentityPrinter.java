package com.ulyp.core.printers;

import com.ulyp.core.ClassDescription;
import com.ulyp.core.printers.bytes.BinaryInput;
import com.ulyp.core.printers.bytes.BinaryOutput;

public class IdentityPrinter extends ObjectBinaryPrinter {

    protected IdentityPrinter(int id) {
        super(id);
    }

    @Override
    boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    public String read(ClassDescription classDescription, BinaryInput binaryInput) {
        return classDescription.getSimpleName() + "@" + binaryInput.readInt();
    }

    @Override
    public void write(Object obj, BinaryOutput out) throws Exception {
        out.write(System.identityHashCode(obj));
    }
}
