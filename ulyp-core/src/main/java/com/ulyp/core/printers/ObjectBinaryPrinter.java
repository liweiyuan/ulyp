package com.ulyp.core.printers;

import com.ulyp.core.ClassDescription;
import com.ulyp.core.DecodingContext;
import com.ulyp.core.TracingContext;
import com.ulyp.core.printers.bytes.BinaryInput;
import com.ulyp.core.printers.bytes.BinaryOutput;

public abstract class ObjectBinaryPrinter {

    // todo change to long
    private final long id;

    protected ObjectBinaryPrinter(int id) {
        this.id = id;
    }

    public final long getId() {
        return id;
    }

    public Printable read(ClassDescription classDescription, BinaryInput binaryInput, DecodingContext decodingContext) {
        return binaryInput.readString();
    }

    abstract boolean supports(Class<?> clazz);

    /**
     * @param obj object to print
     * @param out target binary stream to print to
     * @param tracingContext
     */
    public abstract void write(Object obj, BinaryOutput out, TracingContext tracingContext) throws Exception;
}
