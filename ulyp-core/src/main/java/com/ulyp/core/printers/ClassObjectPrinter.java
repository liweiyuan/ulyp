package com.ulyp.core.printers;

public class ClassObjectPrinter extends ObjectBinaryPrinter {

    protected ClassObjectPrinter(int id) {
        super(id);
    }

    @Override
    boolean supports(Class<?> clazz) {
        return clazz == Class.class;
    }

    @Override
    public void write(Object obj, BinaryStream out) {
        if (obj == null) {
            out.write("null");
        } else {
            Class<?> clazz = (Class<?>) obj;
            out.write("Class{" + clazz.getName() + "}");
        }
    }
}
