package com.ulyp.core.printers;

import com.ulyp.core.ClassDescription;
import com.ulyp.core.DecodingContext;
import com.ulyp.core.TracingContext;
import com.ulyp.core.printers.bytes.BinaryInput;
import com.ulyp.core.printers.bytes.BinaryOutput;

public class ClassObjectPrinter extends ObjectBinaryPrinter {

    protected ClassObjectPrinter(int id) {
        super(id);
    }

    @Override
    boolean supports(Type type) {
        return type.isClassObject();
    }

    @Override
    public Printable read(ClassDescription classDescription, BinaryInput binaryInput, DecodingContext decodingContext) {
        long typeId = binaryInput.readLong();
        return () -> "Class{" + decodingContext.getClass(typeId).getName() + "}";
    }

    @Override
    public void write(Object obj, BinaryOutput out, TracingContext tracingContext) throws Exception {
        Class<?> clazz = (Class<?>) obj;
        out.write(tracingContext.getClassId(clazz));
    }
}
