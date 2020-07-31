package com.ulyp.core.printers;

import com.ulyp.core.TracingContext;
import com.ulyp.core.printers.bytes.BinaryOutput;

public class StringPrinter extends ObjectBinaryPrinter {

    private static final int MAX_LENGTH = 400;

    protected StringPrinter(int id) {
        super(id);
    }

    @Override
    boolean supports(Type type) {
        return type.isExactlyJavaLangString();
    }

    @Override
    public void write(Object obj, BinaryOutput out, TracingContext tracingContext) throws Exception {
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
