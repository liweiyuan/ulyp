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
    public void write(Object obj, BinaryOutput out) throws Exception {
        Class<?> clazz = (Class<?>) obj;
        out.write("Class{" + clazz.getName() + "}");
    }
}
