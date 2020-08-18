package com.ulyp.core.printers;

import com.ulyp.core.ClassDescription;

public class IdentityObjectRepresentation extends ObjectRepresentation {

    private final long hashCode;

    public IdentityObjectRepresentation(ClassDescription type, long hashCode) {
        super(type);
        this.hashCode = hashCode;
    }

    @Override
    public String print() {
        return this.getType().getSimpleName() + "@" + hashCode;
    }
}
