package com.ulyp.core.printers;

import com.ulyp.core.util.ClassUtils;

public class ThrowablePrinter extends ObjectBinaryPrinter {

    protected ThrowablePrinter(int id) {
        super(id);
    }

    @Override
    boolean supports(Class<?> clazz) {
        return false;
    }

    @Override
    public void write(Object obj, BinaryOutput out) {
        Throwable t = (Throwable) obj;
        /*StringPrinter.instance.print(*//*)*/
        out.write(ClassUtils.getSimpleName(t.getClass()) + ": " + t.getMessage());
    }
}
