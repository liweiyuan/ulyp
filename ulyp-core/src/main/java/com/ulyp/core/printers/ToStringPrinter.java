package com.ulyp.core.printers;

import com.ulyp.core.ClassDescription;
import com.ulyp.core.DecodingContext;
import com.ulyp.core.TracingContext;
import com.ulyp.core.printers.bytes.BinaryInput;
import com.ulyp.core.printers.bytes.BinaryOutput;
import com.ulyp.core.printers.bytes.BinaryOutputAppender;
import com.ulyp.core.printers.bytes.StringView;

import java.lang.reflect.Method;

public class ToStringPrinter extends ObjectBinaryPrinter {

    private StringView NULL_STRING = new StringView("null");

    private static final int TO_STRING_CALL_SUCCESS = 1;
    private static final int TO_STRING_CALL_NULL = 2;
    private static final int TO_STRING_CALL_FAIL = 0;

    protected ToStringPrinter(int id) {
        super(id);
    }

    @Override
    boolean supports(Class<?> clazz) {
        if (clazz == Object.class) {
            return false;
        }

        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals("toString") && method.getReturnType() == String.class && method.getParameterCount() == 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Printable read(ClassDescription classDescription, BinaryInput binaryInput, DecodingContext decodingContext) {
        long result = binaryInput.readLong();
        if (result == TO_STRING_CALL_SUCCESS) {
            return ObjectBinaryPrinterType.STRING_PRINTER.getPrinter().read(classDescription, binaryInput, decodingContext);
        } else if (result == TO_STRING_CALL_NULL) {
            return NULL_STRING;
        } else {
            return ObjectBinaryPrinterType.IDENTITY_PRINTER.getPrinter().read(classDescription, binaryInput, decodingContext);
        }
    }

    @Override
    public void write(Object obj, BinaryOutput out, TracingContext tracingContext) throws Exception {
        try {
            String printed = obj.toString();
            if (printed != null) {
                try (BinaryOutputAppender appender = out.appender()) {
                    appender.append(TO_STRING_CALL_SUCCESS);
                    ObjectBinaryPrinterType.STRING_PRINTER.getPrinter().write(printed, appender, tracingContext);
                }
            } else {
                try (BinaryOutputAppender appender = out.appender()) {
                    appender.append(TO_STRING_CALL_NULL);
                }
            }
        } catch (Throwable e) {
            try (BinaryOutputAppender appender = out.appender()) {
                appender.append(TO_STRING_CALL_FAIL);
                ObjectBinaryPrinterType.IDENTITY_PRINTER.getPrinter().write(obj, appender, tracingContext);
            }
        }
    }
}
