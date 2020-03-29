package com.ulyp.core.printers;

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
    public void write(Object obj, BinaryOutput out) {
        if (obj == null) {
            out.write("null");
        } else {
            out.write(ClassUtils.getSimpleName(obj.getClass()) + "@" + System.identityHashCode(obj));
        }
    }
}
