package com.ulyp.core.printers;

/**
 * Deserialized object representation. Depending on the printer used for serialization
 * different amount of information can be presented
 */
public abstract class ObjectRepresentation implements Printable {

    private final TypeInfo typeInfo;

    protected ObjectRepresentation(TypeInfo typeInfo) {
        this.typeInfo = typeInfo;
    }

    public TypeInfo getType() {
        return typeInfo;
    }
}
