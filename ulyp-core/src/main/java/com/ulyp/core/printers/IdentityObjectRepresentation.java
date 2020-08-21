package com.ulyp.core.printers;

public class IdentityObjectRepresentation extends ObjectRepresentation {

    private final long hashCode;

    public IdentityObjectRepresentation(TypeInfo typeInfo, long hashCode) {
        super(typeInfo);
        this.hashCode = hashCode;
    }

    @Override
    public String print() {
        return this.getType().getSimpleName() + "@" + Long.toHexString(hashCode);
    }
}
