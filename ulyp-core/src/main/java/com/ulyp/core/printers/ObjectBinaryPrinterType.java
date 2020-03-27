package com.ulyp.core.printers;

public enum ObjectBinaryPrinterType {
    IDENTITY_PRINTER(new IdentityPrinter(0), Integer.MAX_VALUE),
    CLASS_OBJECT_PRINTER(new ClassObjectPrinter(1), 20),
    STRING_PRINTER(new StringPrinter(2), 0),
    COLLECTION(new CollectionPrinter(3), 1),
    TO_STRING_PRINTER(new ToStringPrinter(4), 10),
    THROWABLE_PRINTER(new ThrowablePrinter(5), 20),
    ENUM_PRINTER(new EnumPrinter(6), 20);

    private final ObjectBinaryPrinter printer;
    private final int order;

    ObjectBinaryPrinterType(ObjectBinaryPrinter printer, int order) {
        this.printer = printer;
        this.order = order;
    }

    public ObjectBinaryPrinter getPrinter() {
        return printer;
    }

    public int getOrder() {
        return order;
    }
}
