package com.ulyp.core.printers;

import com.ulyp.core.ClassDescription;
import com.ulyp.core.printers.bytes.BinaryInput;
import com.ulyp.core.printers.bytes.BinaryOutput;
import com.ulyp.core.printers.bytes.BinaryOutputAppender;

public class DynamicObjectBinaryPrinter extends ObjectBinaryPrinter {

    protected DynamicObjectBinaryPrinter(int id) {
        super(id);
    }

    @Override
    boolean supports(Class<?> clazz) {
        return clazz.isInterface() || clazz == Object.class;
    }

    @Override
    public String read(ClassDescription classDescription, BinaryInput binaryInput) {
        int printerId = binaryInput.readInt();
        return ObjectBinaryPrinterType.printerForId(printerId).read(classDescription, binaryInput);
    }

    @Override
    public void write(Object obj, BinaryOutput out) throws Exception {
        Class<?> type = obj.getClass();
        ObjectBinaryPrinter printer = type != Object.class ? Printers.getInstance().determinePrinterForType(obj.getClass()) : ObjectBinaryPrinterType.IDENTITY_PRINTER.getPrinter();
        try (BinaryOutputAppender appender = out.appender()) {
            appender.append(printer.getId());
            printer.write(obj, appender);
        }
    }
}
