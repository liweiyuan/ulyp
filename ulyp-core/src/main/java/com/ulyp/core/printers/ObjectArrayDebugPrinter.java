package com.ulyp.core.printers;

import com.ulyp.core.ClassDescription;
import com.ulyp.core.DecodingContext;
import com.ulyp.core.AgentRuntime;
import com.ulyp.core.printers.bytes.BinaryInput;
import com.ulyp.core.printers.bytes.BinaryOutput;
import com.ulyp.core.printers.bytes.BinaryOutputAppender;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ObjectArrayDebugPrinter extends ObjectBinaryPrinter {

    protected ObjectArrayDebugPrinter(int id) {
        super(id);
    }

    @Override
    boolean supports(Type type) {
        // used manually
        return false;
    }

    @Override
    public ObjectRepresentation read(ClassDescription classDescription, BinaryInput binaryInput, DecodingContext decodingContext) {
        long totalElements = binaryInput.readLong();
        List<Printable> elements = new ArrayList<>();
        long writtenElements = binaryInput.readLong();
        for (int i = 0; i < writtenElements; i++) {
            ClassDescription itemClassType = decodingContext.getClass(binaryInput.readLong());
            ObjectBinaryPrinter printer = ObjectBinaryPrinterType.printerForId(binaryInput.readLong());
            elements.add(printer.read(itemClassType, binaryInput, decodingContext));
        }
        int notWrittenItemsCount = (int) (totalElements - writtenElements);
        return new PlainObjectRepresentation(
                classDescription,
                elements.stream().map(Printable::print).collect(Collectors.toList()) +
                (notWrittenItemsCount > 0 ? ", " + notWrittenItemsCount + " more..." : "")
        );
    }

    @Override
    public void write(Object obj, BinaryOutput out, AgentRuntime agentRuntime) throws Exception {
        try (BinaryOutputAppender appender = out.appender()) {
            Object[] array = (Object[]) obj;
            int length = array.length;
            appender.append(length);
            int elementsToWrite = Math.min(3, length);
            appender.append(elementsToWrite);
            for (int i = 0; i < elementsToWrite; i++) {
                Object element = array[i];
                appender.append(agentRuntime.getClassId(element));
                ObjectBinaryPrinter printer = element != null ? ObjectBinaryPrinterType.DYNAMIC_OBJECT_PRINTER.getPrinter() : ObjectBinaryPrinterType.NULL_PRINTER.getPrinter();
                appender.append(printer.getId());
                printer.write(element, appender, agentRuntime);
            }
        }
    }
}
