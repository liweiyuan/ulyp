package com.ulyp.core.printers;

import com.ulyp.core.ClassDescription;
import com.ulyp.core.printers.bytes.BinaryInput;
import com.ulyp.core.printers.bytes.BinaryOutput;
import com.ulyp.core.printers.bytes.BinaryOutputAppender;

import java.lang.reflect.Method;

public class ToStringPrinter extends ObjectBinaryPrinter {

    private static final int TO_STRING_CALL_SUCCESS = 1;
    private static final int TO_STRING_CALL_FAIL = 0;

    protected ToStringPrinter(int id) {
        super(id);
    }

    @Override
    boolean supports(Class<?> clazz) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals("toString") && method.getReturnType() == String.class && method.getParameterCount() == 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String read(ClassDescription classDescription, BinaryInput binaryInput) {
        int result = binaryInput.readInt();
        if (result == TO_STRING_CALL_SUCCESS) {
            return binaryInput.readString();
        } else {
            return ObjectBinaryPrinterType.IDENTITY_PRINTER.getPrinter().read(classDescription, binaryInput);
        }
    }

    @Override
    public void write(Object obj, BinaryOutput out) throws Exception {
        try {
            String s = obj.toString();
            if (s != null && s.length() > 400) {
                s = s.substring(0, 400) + "...";
            }
            try (BinaryOutputAppender appender = out.appender()) {
                appender.append(TO_STRING_CALL_SUCCESS);
                appender.append(s);
            }
        } catch (Exception e) {
            try (BinaryOutputAppender appender = out.appender()) {
                appender.append(TO_STRING_CALL_FAIL);
                ObjectBinaryPrinterType.IDENTITY_PRINTER.getPrinter().write(obj, appender);
            }
        }
    }
}
