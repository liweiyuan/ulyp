package com.ulyp.core.printers;

/**
 * Plain object representation which can only printed. Usually should not be used (clarify)
 */
public class PlainObjectRepresentation extends ObjectRepresentation {

    private final String text;

    protected PlainObjectRepresentation(TypeInfo typeInfo, String text) {
        super(typeInfo);

        this.text = text;
    }

    @Override
    public String print() {
        return text;
    }
}
