package com.ulyp.core.printers;

public enum ObjectBinaryPrinterType {
    CLASS_OBJECT_PRINTER(new ClassObjectPrinter((byte) 1), 20),
    STRING_PRINTER(new StringPrinter((byte) 2), 0),
    /*COLLECTION_PRINTER(new CollectionPrinter((byte) 3), 1),*/
    /*TO_STRING_PRINTER(new ToStringPrinter((byte) 4), 10),*/
    THROWABLE_PRINTER(new ThrowablePrinter((byte) 5), 20),
    ENUM_PRINTER(new EnumPrinter((byte) 6), 5),
    DYNAMIC_OBJECT_PRINTER(new DynamicObjectBinaryPrinter((byte) 7), 100),
    INTEGRAL_PRINTER(new IntegralPrinter((byte) 12), 0),
    ANY_NUMBER_PRINTER(new AnyNumbersPrinter((byte) 8), 1),
    OBJECT_ARRAY_PRINTER(new ObjectArrayPrinter((byte) 11), 1),

    // identity can be used for any objects
    IDENTITY_PRINTER(new IdentityPrinter((byte) 0), Integer.MAX_VALUE / 2),

    // printers which have Integer.MAX_VALUE order can only be used manually
    COLLECTION_DEBUG_PRINTER(new CollectionDebugPrinter((byte) 10), Integer.MAX_VALUE),
    NULL_PRINTER(new NullObjectPrinter((byte) 9), Integer.MAX_VALUE);

    public static final ObjectBinaryPrinter[] printers = new ObjectBinaryPrinter[256];

    static {
        for (ObjectBinaryPrinterType printerType : values()) {
            if (printers[printerType.getPrinter().getId()] != null) {
                throw new RuntimeException("Duplicate id");
            }
            printers[printerType.getPrinter().getId()] = printerType.getPrinter();
        }
    }

    public static ObjectBinaryPrinter printerForId(byte id) {
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
