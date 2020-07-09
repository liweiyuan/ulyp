package com.ulyp.core;

import com.ulyp.core.printers.CollectionPrinter;
import com.ulyp.core.printers.ObjectArrayPrinter;
import com.ulyp.core.printers.ObjectBinaryPrinterType;

public class TracingParams {

    private volatile boolean traceCallee;
    private volatile boolean traceIdentityHashCode;
    private volatile boolean traceCollections;

    public TracingParams(boolean traceCallee, boolean traceIdentityHashCode, boolean traceCollections) {
        this.traceCallee = traceCallee;
        this.traceIdentityHashCode = traceIdentityHashCode;
        this.traceCollections = traceCollections;
    }

    public void updateTraceCollections(boolean value) {
        this.traceCollections = value;
        CollectionPrinter collectionPrinter = (CollectionPrinter) ObjectBinaryPrinterType.COLLECTION_PRINTER.getPrinter();
        collectionPrinter.setFullTraceMode(value);
        ObjectArrayPrinter objectArrayPrinter = (ObjectArrayPrinter) ObjectBinaryPrinterType.OBJECT_ARRAY_PRINTER.getPrinter();
        objectArrayPrinter.setFullTraceMode(value);
    }

    public boolean traceCallee() {
        return traceCallee;
    }

    public boolean traceIdentityHashCode() {
        return traceIdentityHashCode;
    }

    public boolean traceCollections() {
        return traceCollections;
    }
}
