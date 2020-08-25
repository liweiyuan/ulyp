package com.ulyp.core.printers;

import com.ulyp.core.DecodingContext;
import com.ulyp.core.AgentRuntime;
import com.ulyp.core.printers.bytes.BinaryInput;
import com.ulyp.core.printers.bytes.BinaryOutput;
import com.ulyp.core.printers.bytes.BinaryOutputAppender;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ObjectArrayDebugPrinter extends ObjectBinaryPrinter {

    protected ObjectArrayDebugPrinter(byte id) {
        super(id);
    }

    @Override
    boolean supports(TypeInfo typeInfo) {
        // used manually by ObjectArrayPrinter
        return false;
    }

    @Override
    public ObjectRepresentation read(TypeInfo type, BinaryInput binaryInput, DecodingContext decodingContext) {
        int arrayLength = binaryInput.readInt();
        List<ObjectRepresentation> items = new ArrayList<>();
        int recordedItemsCount = binaryInput.readInt();
        for (int i = 0; i < recordedItemsCount; i++) {
            TypeInfo itemClassTypeInfo = decodingContext.getType(binaryInput.readInt());
            ObjectBinaryPrinter printer = ObjectBinaryPrinterType.printerForId(binaryInput.readByte());
            items.add(printer.read(itemClassTypeInfo, binaryInput, decodingContext));
        }
        return new ObjectArrayRepresentation(
                type,
                arrayLength,
                items
        );
    }

    @Override
    public void write(Object obj, TypeInfo classDescription, BinaryOutput out, AgentRuntime agentRuntime) throws Exception {
        try (BinaryOutputAppender appender = out.appender()) {
            Object[] array = (Object[]) obj;
            int length = array.length;
            appender.append(length);
            int itemsToRecord = Math.min(3, length);
            appender.append(itemsToRecord);
            for (int i = 0; i < itemsToRecord; i++) {
                Object item = array[i];
                TypeInfo itemType = agentRuntime.get(item);
                appender.append(itemType.getId());
                ObjectBinaryPrinter printer = item != null ? itemType.getSuggestedPrinter() : ObjectBinaryPrinterType.NULL_PRINTER.getInstance();
                appender.append(printer.getId());
                printer.write(item, itemType, appender, agentRuntime);
            }
        }
    }
}
