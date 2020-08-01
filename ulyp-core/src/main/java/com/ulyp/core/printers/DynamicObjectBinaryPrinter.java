package com.ulyp.core.printers;

import com.ulyp.core.ClassDescription;
import com.ulyp.core.DecodingContext;
import com.ulyp.core.TracingContext;
import com.ulyp.core.printers.bytes.BinaryInput;
import com.ulyp.core.printers.bytes.BinaryOutput;
import com.ulyp.core.printers.bytes.BinaryOutputAppender;

public class DynamicObjectBinaryPrinter extends ObjectBinaryPrinter {

    protected DynamicObjectBinaryPrinter(int id) {
        super(id);
    }

    @Override
    boolean supports(Type type) {
        return type.isInterface() || type.isExactlyJavaLangObject();
    }

    @Override
    public Printable read(ClassDescription classDescription, BinaryInput binaryInput, DecodingContext decodingContext) {
        long printerId = binaryInput.readLong();
        return ObjectBinaryPrinterType.printerForId(printerId).read(classDescription, binaryInput, decodingContext);
    }

    @Override
    public void write(Object obj, BinaryOutput out, TracingContext tracingContext) throws Exception {
        Class<?> type = obj.getClass();
        ObjectBinaryPrinter printer = (type != Object.class)
                ? Printers.getInstance().determinePrinterForType(tracingContext.toType(obj.getClass())) :
                ObjectBinaryPrinterType.IDENTITY_PRINTER.getPrinter();

        try (BinaryOutputAppender appender = out.appender()) {
            appender.append(printer.getId());
            printer.write(obj, appender, tracingContext);
        }
    }
}
