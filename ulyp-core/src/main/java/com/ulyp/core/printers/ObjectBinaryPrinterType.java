package com.ulyp.core.printers;

public enum ObjectBinaryPrinterType {
    CLASS_OBJECT_PRINTER(new ClassObjectPrinter(1), 20),
    STRING_PRINTER(new StringPrinter(2), 0),
    COLLECTION_PRINTER(new CollectionPrinter(3), 1),
    TO_STRING_PRINTER(new ToStringPrinter(4), 10),
    THROWABLE_PRINTER(new ThrowablePrinter(5), 20),
    ENUM_PRINTER(new EnumPrinter(6), 5),
    DYNAMIC_OBJECT_PRINTER(new DynamicObjectBinaryPrinter(7), 100),
    NUMBER_PRINTER(new NumbersPrinter(8), 0),
    OBJECT_ARRAY_PRINTER(new ObjectArrayPrinter(11), 1),

    // identity can be used for any objects
    IDENTITY_PRINTER(new IdentityPrinter(0), Integer.MAX_VALUE / 2),

    // printers which have Integer.MAX_VALUE order can only be used manually
    COLLECTION_DEBUG_PRINTER(new CollectionDebugPrinter(10), Integer.MAX_VALUE),
    NULL_PRINTER(new NullObjectPrinter(9), Integer.MAX_VALUE);

    public static final ObjectBinaryPrinter[] printers = new ObjectBinaryPrinter[256];

    static {
        for (ObjectBinaryPrinterType printerType : values()) {
            if (printers[(int) printerType.getPrinter().getId()] != null) {
                throw new RuntimeException("Duplicate id");
            }
            printers[(int) printerType.getPrinter().getId()] = printerType.getPrinter();
        }
    }

    public static ObjectBinaryPrinter printerForId(long id) {
        return printers[(int) id];
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
