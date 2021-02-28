package com.ulyp.core.printers;

public class IdentityObjectRepresentation extends ObjectRepresentation {

    private final int hashCode;

    public IdentityObjectRepresentation(TypeInfo typeInfo, int hashCode) {
        super(typeInfo);
        this.hashCode = hashCode;
    }

    public int getHashCode() {
        return hashCode;
    }
}
