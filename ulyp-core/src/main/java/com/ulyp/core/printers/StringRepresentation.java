package com.ulyp.core.printers;

public class StringRepresentation implements ObjectRepresentation {

    private final String text;

    public StringRepresentation(String text) {
        this.text = text;
    }

    @Override
    public String print() {
        return text;
    }
}
