package com.ulyp.core.printers;

public class ToStringPrinter extends ObjectBinaryPrinter {

    protected ToStringPrinter(int id) {
        super(id);
    }

    @Override
    public void write(Object obj, BinaryStream out) {
        out.write(obj != null ? obj.toString() : "null");
    }
}
