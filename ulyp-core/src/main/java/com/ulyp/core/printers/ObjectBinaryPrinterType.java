package com.ulyp.core.printers;

public enum ObjectBinaryPrinterType {
    IDENTITY_PRINTER(new IdentityPrinter(0), Integer.MAX_VALUE / 2),
    CLASS_OBJECT_PRINTER(new ClassObjectPrinter(1), 20),
    STRING_PRINTER(new StringPrinter(2), 0),
    COLLECTION(new CollectionPrinter(3), 1),
    TO_STRING_PRINTER(new ToStringPrinter(4), 10),
    THROWABLE_PRINTER(new ThrowablePrinter(5), 20),
    ENUM_PRINTER(new EnumPrinter(6), 20),
    DYNAMIC_OBJECT_PRINTER(new DynamicObjectBinaryPrinter(7), 100),
    NUMBER_PRINTER(new NumbersPrinter(8), 0),
    // null printer should come last and it support no type
    NULL_PRINTER(new NullObjectPrinter(9), Integer.MAX_VALUE);

    public static final ObjectBinaryPrinter[] printers = new ObjectBinaryPrinter[256];

    static {
        for (ObjectBinaryPrinterType printerType : values()) {
            if (printers[printerType.getPrinter().getId()] != null) {
                throw new RuntimeException("Duplicate id");
            }
            printers[printerType.getPrinter().getId()] = printerType.getPrinter();
        }
    }

    public static ObjectBinaryPrinter printerForId(int id) {
        return printers[id];
    }

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
