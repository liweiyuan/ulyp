package com.ulyp.core.printers;

import com.ulyp.core.ClassDescription;
import com.ulyp.core.DecodingContext;
import com.ulyp.core.AgentRuntime;
import com.ulyp.core.printers.bytes.BinaryInput;
import com.ulyp.core.printers.bytes.BinaryOutput;
import com.ulyp.core.printers.bytes.BinaryOutputAppender;

public class CollectionPrinter extends ObjectBinaryPrinter {

    private static final ObjectBinaryPrinter collectionSizeOnlyPrinter = new CollectionSizeOnlyPrinter(-1);
    private static final ObjectBinaryPrinter collectionDebugPrinter = new CollectionDebugPrinter(-1);

    private volatile boolean fullTraceMode = false;

    protected CollectionPrinter(int id) {
        super(id);
    }

    @Override
    boolean supports(Type type) {
        return type.isCollection();
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
    public void write(Object obj, BinaryOutput out, AgentRuntime agentRuntime) throws Exception {
        boolean fullTrace = this.fullTraceMode;
        try (BinaryOutputAppender appender = out.appender()) {
            appender.append(fullTrace);
            if (fullTrace) {
                collectionDebugPrinter.write(obj, appender, agentRuntime);
            } else {
                collectionSizeOnlyPrinter.write(obj, appender, agentRuntime);
            }
        }
    }

    public void setFullTraceMode(boolean fullTraceOfCollections) {
        this.fullTraceMode = fullTraceOfCollections;
    }
}
