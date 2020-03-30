package com.ulyp.core.printers;

import com.ulyp.core.ClassDescription;
import com.ulyp.core.printers.bytes.BinaryInput;
import com.ulyp.core.printers.bytes.BinaryOutput;
import com.ulyp.transport.TClassDescriptionDecoder;

public abstract class ObjectBinaryPrinter {

    private final int id;

    protected ObjectBinaryPrinter(int id) {
        this.id = id;
    }

    public final int getId() {
        return id;
    }

    public String read(ClassDescription classDescription, BinaryInput binaryInput) {
        return binaryInput.readString();
    }

    abstract boolean supports(Class<?> clazz);

    /**
     * @param obj object to print
     * @param out target binary stream to print to
     */
    public abstract void write(Object obj, BinaryOutput out);
}
