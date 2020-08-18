package com.ulyp.core.printers;

import com.ulyp.core.ClassDescription;
import com.ulyp.core.DecodingContext;
import com.ulyp.core.AgentRuntime;
import com.ulyp.core.printers.bytes.BinaryInput;
import com.ulyp.core.printers.bytes.BinaryOutput;
import com.ulyp.core.printers.bytes.BinaryOutputAppender;

public class ObjectArrayPrinter extends ObjectBinaryPrinter {

    private static final ObjectBinaryPrinter sizeOnlyPrinter = new ObjectArraySizePrinter(-1);
    private static final ObjectBinaryPrinter debugPrinter = new ObjectArrayDebugPrinter(-1);

    private volatile boolean shouldRecordItems = false;

    protected ObjectArrayPrinter(int id) {
        super(id);
    }

    @Override
    boolean supports(Type type) {
        return type.isNonPrimitveArray();
    }

    @Override
    public ObjectRepresentation read(ClassDescription classDescription, BinaryInput binaryInput, DecodingContext decodingContext) {
        boolean recordItems = binaryInput.readBoolean();
        if (recordItems) {
            return debugPrinter.read(classDescription, binaryInput, decodingContext);
        } else {
            return sizeOnlyPrinter.read(classDescription, binaryInput, decodingContext);
        }
    }

    @Override
    public void write(Object obj, BinaryOutput out, AgentRuntime agentRuntime) throws Exception {
        boolean recordItems = this.shouldRecordItems;
        try (BinaryOutputAppender appender = out.appender()) {
            appender.append(recordItems);
            if (recordItems) {
                debugPrinter.write(obj, appender, agentRuntime);
            } else {
                sizeOnlyPrinter.write(obj, appender, agentRuntime);
            }
        }
    }

    public void setShouldRecordItems(boolean recordItems) {
        this.shouldRecordItems = recordItems;
    }
}
