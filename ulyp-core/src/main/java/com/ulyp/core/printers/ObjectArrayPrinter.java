package com.ulyp.core.printers;

import com.ulyp.core.DecodingContext;
import com.ulyp.core.AgentRuntime;
import com.ulyp.core.printers.bytes.BinaryInput;
import com.ulyp.core.printers.bytes.BinaryOutput;
import com.ulyp.core.printers.bytes.BinaryOutputAppender;

public class ObjectArrayPrinter extends ObjectBinaryPrinter {

    private static final ObjectBinaryPrinter sizeOnlyPrinter = new ObjectArraySizePrinter((byte) -1);
    private static final ObjectBinaryPrinter debugPrinter = new ObjectArrayDebugPrinter((byte) -1);

    private volatile boolean shouldRecordItems = true;

    protected ObjectArrayPrinter(byte id) {
        super(id);
    }

    @Override
    boolean supports(TypeInfo type) {
        return type.isNonPrimitveArray();
    }

    @Override
    public ObjectRepresentation read(TypeInfo classDescription, BinaryInput input, DecodingContext decodingContext) {
        boolean recordItems = input.readBoolean();
        if (recordItems) {
            return debugPrinter.read(classDescription, input, decodingContext);
        } else {
            return sizeOnlyPrinter.read(classDescription, input, decodingContext);
        }
    }

    @Override
    public void write(Object object, TypeInfo objectType, BinaryOutput out, AgentRuntime runtime) throws Exception {
        boolean recordItems = this.shouldRecordItems;
        try (BinaryOutputAppender appender = out.appender()) {
            appender.append(recordItems);
            if (recordItems) {
                debugPrinter.write(object, objectType, appender, runtime);
            } else {
                sizeOnlyPrinter.write(object, objectType, appender, runtime);
            }
        }
    }

    public void setShouldRecordItems(boolean recordItems) {
//        this.shouldRecordItems = recordItems;
    }
}
