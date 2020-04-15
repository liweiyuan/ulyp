package com.ulyp.core.printers;

import com.ulyp.core.printers.bytes.BinaryOutput;

public class StringPrinter extends ObjectBinaryPrinter {

    private static final int MAX_LENGTH = 400;

    protected StringPrinter(int id) {
        super(id);
    }

    @Override
    boolean supports(Class<?> clazz) {
        return clazz == String.class;
    }

    @Override
    public void write(Object obj, BinaryOutput out) throws Exception {
        String text = (String) obj;
        String printed;
        if (text.length() > MAX_LENGTH) {
            printed = text.substring(0, MAX_LENGTH) + "...(" + text.length() + ")";
        } else {
            printed = text;
        }
        out.write(printed);
    }
}
