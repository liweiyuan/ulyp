package com.ulyp.core.printers;

import com.ulyp.core.util.ClassUtils;

public class ThrowablePrinter extends ObjectBinaryPrinter {

    protected ThrowablePrinter(int id) {
        super(id);
    }

    @Override
    public void write(Object obj, BinaryStream out) {
        Throwable t = (Throwable) obj;
        /*StringPrinter.instance.print(*//*)*/
        out.write(ClassUtils.getSimpleName(t.getClass()) + ": " + t.getMessage());
    }
}
