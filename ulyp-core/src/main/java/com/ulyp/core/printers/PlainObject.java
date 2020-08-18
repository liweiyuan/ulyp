package com.ulyp.core.printers;

import com.ulyp.core.ClassDescription;

/**
 * Plain object representation which can only printed. Usually should not be used (clarify)
 */
public class PlainObject extends ObjectRepresentation {

    private final String text;

    protected PlainObject(ClassDescription type, String text) {
        super(type);

        this.text = text;
    }

    @Override
    public String print() {
        return text;
    }
}
