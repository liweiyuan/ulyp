package com.ulyp.storage;

import com.ulyp.core.ClassDescription;

public class ObjectValue {

    private final String printedText;
    private final ClassDescription classDescription;

    public ObjectValue(String printedText, ClassDescription classDescription) {
        this.printedText = printedText;
        this.classDescription = classDescription != null ? classDescription : ClassDescription.UNKNOWN_CLASS_DESCRIPTION;
    }

    public String getPrintedText() {
        return printedText;
    }

    public ClassDescription getClassDescription() {
        return classDescription;
    }

    @Override
    public String toString() {
        return printedText;
    }
}
