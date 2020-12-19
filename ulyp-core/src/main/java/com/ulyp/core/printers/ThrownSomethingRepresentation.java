package com.ulyp.core.printers;

public class ThrownSomethingRepresentation extends ObjectRepresentation {

    public ThrownSomethingRepresentation() {
        super(UnknownTypeInfo.getInstance());
    }

    @Override
    public String print() {
        return "?";
    }
}
