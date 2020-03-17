package com.ulyp.core.printers;

public class EnumPrinter extends ObjectBinaryPrinter {

    protected EnumPrinter(int id) {
        super(id);
    }

    @Override
    public void write(Object obj, BinaryStream out) {
        if (obj == null) {
            out.write("null");
        } else {
            out.write(((Enum<?>) obj).name());
        }
    }
}
