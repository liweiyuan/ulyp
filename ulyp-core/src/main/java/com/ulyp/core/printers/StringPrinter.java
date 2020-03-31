package com.ulyp.core.printers;

import com.ulyp.core.printers.bytes.BinaryOutput;

public class StringPrinter extends ObjectBinaryPrinter {

    private static final int MAX_LENGTH = 800;

    protected StringPrinter(int id) {
        super(id);
    }

    @Override
    boolean supports(Class<?> clazz) {
        return clazz == String.class;
    }

    @Override
    public void write(Object obj, BinaryOutput out) throws Exception {
        String s = (String) obj;
        String printed;
        if (s.length() > MAX_LENGTH) {
            printed = "'" + s.substring(0, MAX_LENGTH) + "...'(" + s.length() + ")";
        } else {
            printed = "'" + s + "'";
        }
        out.write(printed);
    }
}
