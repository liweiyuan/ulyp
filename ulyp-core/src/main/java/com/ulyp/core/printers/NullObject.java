package com.ulyp.core.printers;

import com.ulyp.core.ClassDescription;

public class NullObject extends ObjectRepresentation {

    private static final NullObject INSTANCE = new NullObject();

    public static ObjectRepresentation getInstance() {
        return INSTANCE;
    }

    protected NullObject() {
        super(ClassDescription.UNKNOWN);
    }

    @Override
    public String print() {
        return "null";
    }
}
