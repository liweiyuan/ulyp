package com.ulyp.core.printers;

public class NumberObjectRepresentation extends ObjectRepresentation {

    private final String numberPrinted;

    public NumberObjectRepresentation(TypeInfo typeInfo, String numberPrinted) {
        super(typeInfo);
        this.numberPrinted = numberPrinted;
    }

    @Override
    public String print() {
        return numberPrinted;
    }
}
