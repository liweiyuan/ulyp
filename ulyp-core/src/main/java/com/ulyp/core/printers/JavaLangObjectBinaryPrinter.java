package com.ulyp.core.printers;

import com.ulyp.core.printers.bytes.BinaryOutput;

public class JavaLangObjectBinaryPrinter extends ObjectBinaryPrinter {

    protected JavaLangObjectBinaryPrinter(int id) {
        super(id);
    }

    @Override
    boolean supports(Class<?> clazz) {
        return clazz == Object.class;
    }

    @Override
    public void write(Object obj, BinaryOutput out) {
        if (obj != null) {
            Class<?> type = obj.getClass();
            if (type != Object.class) {
                ObjectBinaryPrinterType.IDENTITY_PRINTER.getPrinter().write(obj, out);
            } else {
                ObjectBinaryPrinter printer = Printers.getInstance().determinePrinterForType(obj.getClass());
                printer.write(obj, out);
            }
        } else {
            out.write("null");
        }
    }
}
