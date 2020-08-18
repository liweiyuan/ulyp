package com.ulyp.core.printers;

import com.ulyp.core.ClassDescription;

public class IdentityObject extends ObjectRepresentation {

    private final long hashCode;

    public IdentityObject(ClassDescription type, long hashCode) {
        super(type);
        this.hashCode = hashCode;
    }

    @Override
    public String print() {
        return this.getType().getSimpleName() + "@" + hashCode;
    }
}
