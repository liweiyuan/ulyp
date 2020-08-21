package com.ulyp.core.printers;

public class StringObjectRepresentation extends ObjectRepresentation {

    private final String text;

    public StringObjectRepresentation(TypeInfo classDescription, String text) {
        super(classDescription);
        this.text = text;
    }

    @Override
    public String print() {
        return text;
    }
}
