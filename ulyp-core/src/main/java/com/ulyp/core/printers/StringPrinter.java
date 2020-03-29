package com.ulyp.core.printers;

public class StringPrinter extends ObjectBinaryPrinter {

    private static final int MAX_LENGTH = 800;

    protected StringPrinter(int id) {
        super(id);
    }

    @Override
    boolean supports(Class<?> clazz) {
        return clazz.getName().equals("java.lang.String");
    }

    @Override
    public void write(Object obj, BinaryOutput out) {
        String s = (String) obj;
        String printed;
        if(s == null) {
            printed = "null";
        } else if (s.length() > MAX_LENGTH) {
            printed = "'" + s.substring(0, MAX_LENGTH) + "...'(" + s.length() + ")";
        } else {
            printed = "'" + s + "'";
        }
        out.write(printed);
    }
}
