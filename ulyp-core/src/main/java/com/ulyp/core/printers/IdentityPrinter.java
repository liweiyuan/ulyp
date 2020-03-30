package com.ulyp.core.printers;

import com.ulyp.core.ClassDescription;
import com.ulyp.core.printers.bytes.BinaryInput;
import com.ulyp.core.printers.bytes.BinaryOutput;
import com.ulyp.core.util.ClassUtils;

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
    public void write(Object obj, BinaryOutput out) {
        out.write(System.identityHashCode(obj));
    }
}
