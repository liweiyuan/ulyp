package com.ulyp.core.printers;

import com.ulyp.core.ClassDescription;

/**
 * Deserialized object representation. Depending on the printer used for serialization
 * different amount of information can be presented
 */
public abstract class ObjectRepresentation implements Printable {

    private final ClassDescription type;

    protected ObjectRepresentation(ClassDescription type) {
        this.type = type;
    }

    public ClassDescription getType() {
        return type;
    }
}
