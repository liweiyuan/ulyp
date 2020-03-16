package com.ulyp.core.printers;

public class ClassPrinter extends ObjectBinaryPrinter {

    protected ClassPrinter(int id) {
        super(id);
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
