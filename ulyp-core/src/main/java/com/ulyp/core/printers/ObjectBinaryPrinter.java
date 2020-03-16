package com.ulyp.core.printers;

public abstract class ObjectBinaryPrinter {

    private final int id;

    protected ObjectBinaryPrinter(int id) {
        this.id = id;
    }

    public final int getId() {
        return id;
    }

    /**
     * @param obj object to print
     * @param out target binary stream to print to
     */
    public abstract void write(Object obj, BinaryStream out);
}
