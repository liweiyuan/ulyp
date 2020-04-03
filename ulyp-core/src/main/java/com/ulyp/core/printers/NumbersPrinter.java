package com.ulyp.core.printers;

import com.ulyp.core.printers.bytes.BinaryOutput;

public class NumbersPrinter extends ObjectBinaryPrinter {

    protected NumbersPrinter(int id) {
        super(id);
    }

    @Override
    boolean supports(Class<?> clazz) {
        return clazz.isPrimitive() || isBoxedNumber(clazz);
    }

    @Override
    public void write(Object obj, BinaryOutput out) throws Exception {
        out.write(obj.toString());
    }

    private boolean isBoxedNumber(Class<?> clazz) {
        if(clazz == null || clazz.getName().equals("java.lang.Object")) {
            return false;
        }

        Class<?> superClazz = clazz.getSuperclass();
        if(superClazz != null && superClazz.getName().equals("java.lang.Number")) {
            return true;
        } else {
            return isBoxedNumber(superClazz);
        }
    }

}
