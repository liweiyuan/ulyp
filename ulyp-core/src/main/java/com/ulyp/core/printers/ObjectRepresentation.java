package com.ulyp.core.printers;

import com.ulyp.core.ClassDescription;

public abstract class ObjectRepresentation implements Printable {

    private final ClassDescription type;

    protected ObjectRepresentation(ClassDescription type) {
        this.type = type;
    }

    public ClassDescription getType() {
        return type;
    }
}
