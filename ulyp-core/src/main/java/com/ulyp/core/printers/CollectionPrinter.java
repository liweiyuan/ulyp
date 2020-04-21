package com.ulyp.core.printers;

import com.ulyp.core.ClassDescription;
import com.ulyp.core.DecodingContext;
import com.ulyp.core.TracingContext;
import com.ulyp.core.printers.bytes.BinaryInput;
import com.ulyp.core.printers.bytes.BinaryOutput;
import com.ulyp.core.printers.bytes.BinaryOutputAppender;

import java.util.Collection;

public class CollectionPrinter extends ObjectBinaryPrinter {

    private static final ObjectBinaryPrinter collectionSizeOnlyPrinter = new CollectionSizeOnlyPrinter(-1);
    private static final ObjectBinaryPrinter collectionDebugPrinter = new CollectionDebugPrinter(-1);

    private volatile boolean fullTraceMode = false;

    protected CollectionPrinter(int id) {
        super(id);
    }

    @Override
    boolean supports(Class<?> clazz) {
        return isCollection(clazz);
    }

    private boolean isCollection(Class<?> clazz) {
        for (Class<?> interfce : clazz.getInterfaces()) {
            if(interfce == Collection.class) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Printable read(ClassDescription classDescription, BinaryInput binaryInput, DecodingContext decodingContext) {
        boolean fullTrace = binaryInput.readBoolean();
        if (fullTrace) {
            return collectionDebugPrinter.read(classDescription, binaryInput, decodingContext);
        } else {
            return collectionSizeOnlyPrinter.read(classDescription, binaryInput, decodingContext);
        }
    }

    @Override
    public void write(Object obj, BinaryOutput out, TracingContext tracingContext) throws Exception {
        boolean fullTrace = this.fullTraceMode;
        try (BinaryOutputAppender appender = out.appender()) {
            appender.append(fullTrace);
            if (fullTrace) {
                collectionDebugPrinter.write(obj, appender, tracingContext);
            } else {
                collectionSizeOnlyPrinter.write(obj, appender, tracingContext);
            }
        }
    }

    public void setFullTraceMode(boolean fullTraceOfCollections) {
        this.fullTraceMode = fullTraceOfCollections;
    }
}
