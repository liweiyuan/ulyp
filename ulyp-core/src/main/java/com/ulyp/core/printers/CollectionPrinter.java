package com.ulyp.core.printers;

import com.ulyp.core.DecodingContext;
import com.ulyp.core.AgentRuntime;
import com.ulyp.core.printers.bytes.BinaryInput;
import com.ulyp.core.printers.bytes.BinaryOutput;
import com.ulyp.core.printers.bytes.BinaryOutputAppender;

public class CollectionPrinter extends ObjectBinaryPrinter {

    private static final ObjectBinaryPrinter collectionSizeOnlyPrinter = new CollectionSizeOnlyPrinter((byte) -1);
    private static final ObjectBinaryPrinter collectionDebugPrinter = new CollectionDebugPrinter((byte) -1);

    private volatile boolean shouldRecordItems = false;

    protected CollectionPrinter(byte id) {
        super(id);
    }

    @Override
    boolean supports(TypeInfo type) {
        return type.isCollection();
    }

    @Override
    public ObjectRepresentation read(TypeInfo classDescription, BinaryInput input, DecodingContext decodingContext) {
        boolean recordItems = input.readBoolean();
        if (recordItems) {
            return collectionDebugPrinter.read(classDescription, input, decodingContext);
        } else {
            return collectionSizeOnlyPrinter.read(classDescription, input, decodingContext);
        }
    }

    @Override
    public void write(Object object, TypeInfo classDescription, BinaryOutput out, AgentRuntime runtime) throws Exception {
        boolean recordItems = this.shouldRecordItems;
        try (BinaryOutputAppender appender = out.appender()) {
            appender.append(recordItems);
            if (recordItems) {
                collectionDebugPrinter.write(object, appender, runtime);
            } else {
                collectionSizeOnlyPrinter.write(object, appender, runtime);
            }
        }
    }

    public void setShouldRecordItems(boolean recordItems) {
        this.shouldRecordItems = recordItems;
    }
}
