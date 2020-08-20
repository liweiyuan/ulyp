package com.ulyp.core.printers;

import com.ulyp.core.ClassDescription;

public class NumberObject extends ObjectRepresentation {

    private final String numberPrinted;

    public NumberObject(ClassDescription classDescription, String numberPrinted) {
        super(classDescription);
        this.numberPrinted = numberPrinted;
    }

    @Override
    public String print() {
        return numberPrinted;
    }
}
