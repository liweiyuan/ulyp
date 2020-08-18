package com.ulyp.core.printers;

import com.ulyp.core.ClassDescription;

public class StringObject extends ObjectRepresentation {

    private final String text;

    public StringObject(ClassDescription classDescription, String text) {
        super(classDescription);
        this.text = text;
    }

    @Override
    public String print() {
        return text;
    }
}
