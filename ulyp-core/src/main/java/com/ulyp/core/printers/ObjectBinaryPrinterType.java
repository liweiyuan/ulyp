package com.ulyp.core.printers;

public enum ObjectBinaryPrinterType {
    IDENTITY_PRINTER(new IdentityPrinter(0)),
    CLASS_PRINTER(new ClassPrinter(1)),
    STRING_PRINTER(new StringPrinter(2)),
    COLLECTION(new CollectionPrinter(3)),
    TO_STRING_PRINTER(new ToStringPrinter(4)),
    THROWABLE_PRINTER(new ThrowablePrinter(5)),
    ENUM_PRINTER(new EnumPrinter(6));

    private final ObjectBinaryPrinter printer;

    ObjectBinaryPrinterType(ObjectBinaryPrinter printer) {
        this.printer = printer;
    }

    public ObjectBinaryPrinter getPrinter() {
        return printer;
    }
}
