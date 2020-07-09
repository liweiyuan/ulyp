package com.ulyp.core.printers;

import com.ulyp.core.ClassDescription;
import com.ulyp.core.DecodingContext;
import com.ulyp.core.TracingContext;
import com.ulyp.core.printers.bytes.BinaryInput;
import com.ulyp.core.printers.bytes.BinaryOutput;
import com.ulyp.core.printers.bytes.BinaryOutputAppender;

public class ObjectArrayPrinter extends ObjectBinaryPrinter {

    private static final ObjectBinaryPrinter sizeOnlyPrinter = new ObjectArraySizePrinter(-1);
    private static final ObjectBinaryPrinter debugPrinter = new ObjectArrayDebugPrinter(-1);

    private volatile boolean fullTraceMode = false;

    protected ObjectArrayPrinter(int id) {
        super(id);
    }

    @Override
    boolean supports(Class<?> clazz) {
        return clazz.isArray() && !clazz.getComponentType().isPrimitive();
    }

    @Override
    public Printable read(ClassDescription classDescription, BinaryInput binaryInput, DecodingContext decodingContext) {
        boolean fullTrace = binaryInput.readBoolean();
        if (fullTrace) {
            return debugPrinter.read(classDescription, binaryInput, decodingContext);
        } else {
            return sizeOnlyPrinter.read(classDescription, binaryInput, decodingContext);
        }
    }

    @Override
    public void write(Object obj, BinaryOutput out, TracingContext tracingContext) throws Exception {
        boolean fullTrace = this.fullTraceMode;
        try (BinaryOutputAppender appender = out.appender()) {
            appender.append(fullTrace);
            if (fullTrace) {
                debugPrinter.write(obj, appender, tracingContext);
            } else {
                sizeOnlyPrinter.write(obj, appender, tracingContext);
            }
        }
    }

    public void setFullTraceMode(boolean fullTraceOfCollections) {
        this.fullTraceMode = fullTraceOfCollections;
    }
}
