package com.ulyp.core.printers;

public class NullObjectRepresentation extends ObjectRepresentation {

    public NullObjectRepresentation(TypeInfo typeInfo) {
        // TODO
        super(typeInfo);
    }

    @Override
    public String print() {
        return "null";
    }
}
