package com.ulyp.core.printers;

public class EnumPrinter extends ObjectBinaryPrinter {

    protected EnumPrinter(int id) {
        super(id);
    }

    @Override
    boolean supports(Class<?> clazz) {
        return clazz.isEnum();
    }

    @Override
    public void write(Object obj, BinaryOutput out) {
        if (obj == null) {
            out.write("null");
        } else {
            out.write(((Enum<?>) obj).name());
        }
    }
}
