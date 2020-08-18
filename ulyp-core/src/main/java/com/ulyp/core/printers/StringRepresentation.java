package com.ulyp.core.printers;

import com.ulyp.core.ClassDescription;

public class StringRepresentation extends ObjectRepresentation {

    private final String text;

    public StringRepresentation(ClassDescription classDescription, String text) {
        super(classDescription);
        this.text = text;
    }

    @Override
    public String print() {
        return text;
    }
}
