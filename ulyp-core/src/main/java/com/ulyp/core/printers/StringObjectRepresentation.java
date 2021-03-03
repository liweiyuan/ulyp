package com.ulyp.core.printers;

public class StringObjectRepresentation extends ObjectRepresentation {

    private final String value;

    public StringObjectRepresentation(TypeInfo classDescription, String value) {
        super(classDescription);
        this.value = value;
    }

    @Override
    public String print() {
        return value;
    }

    public String getValue() {
        return value;
    }
}
