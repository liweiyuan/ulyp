package com.ulyp.core.printers;

import com.ulyp.core.printers.bytes.BinaryOutput;

public class ClassObjectPrinter extends ObjectBinaryPrinter {

    protected ClassObjectPrinter(int id) {
        super(id);
    }

    @Override
    boolean supports(Class<?> clazz) {
        return clazz == Class.class;
    }

    @Override
    public void write(Object obj, BinaryOutput out) {
        if (obj == null) {
            out.write("null");
        } else {
            Class<?> clazz = (Class<?>) obj;
            out.write("Class{" + clazz.getName() + "}");
        }
    }
}
