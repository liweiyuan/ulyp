package com.ulyp.core.printers;

public enum ObjectBinaryPrinterType {
    IDENTITY(new IdentityPrinter(0)),
    CLASS(new ClassPrinter(1)),
    STRING(new StringPrinter(2)),
    COLLECTION(new CollectionPrinter(3)),
    TO_STRING_PRINTER(new ToStringPrinter(4)),
    THROWABLE(new ThrowablePrinter(5));

    private final ObjectBinaryPrinter printer;

    ObjectBinaryPrinterType(ObjectBinaryPrinter printer) {
        this.printer = printer;
    }

    public ObjectBinaryPrinter getPrinter() {
        return printer;
    }
}
