package com.ulyp.core;

import com.ulyp.core.printers.Printable;

public class ObjectValue {

    private final Printable printedText;
    private final ClassDescription classDescription;

    public ObjectValue(Printable printedText, ClassDescription classDescription) {
        this.printedText = printedText;
        this.classDescription = classDescription != null ? classDescription : ClassDescription.UNKNOWN_CLASS_DESCRIPTION;
    }

    public String getPrintedText() {
        return printedText.print();
    }

    public Printable asPrintable() {
        return printedText;
    }

    public ClassDescription getClassDescription() {
        return classDescription;
    }

    @Override
    public String toString() {
        return printedText.toString();
    }
}
