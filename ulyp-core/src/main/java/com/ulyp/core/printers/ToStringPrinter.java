package com.ulyp.core.printers;

import com.ulyp.core.printers.bytes.BinaryOutput;
import com.ulyp.core.util.ClassUtils;

import java.lang.reflect.Method;

public class ToStringPrinter extends ObjectBinaryPrinter {

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
    public void write(Object obj, BinaryOutput out) {
        String s;
        try {
            s = obj.toString();
            out.write(s);
        } catch (Exception e) {
            ObjectBinaryPrinterType.IDENTITY_PRINTER.getPrinter().write(obj, out);
        }
    }
}
