package com.ulyp.core.printers;

import com.ulyp.core.ClassDescription;

public class IdentityObjectRepresentation implements ObjectRepresentation {

    private final ClassDescription type;
    private final long hashCode;

    public IdentityObjectRepresentation(ClassDescription type, long hashCode) {
        this.type = type;
        this.hashCode = hashCode;
    }

    @Override
    public String print() {
        return this.type.getSimpleName() + "@" + hashCode;
    }
}
