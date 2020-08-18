package com.ulyp.core.printers;

import com.ulyp.core.ClassDescription;
import com.ulyp.core.DecodingContext;
import com.ulyp.core.AgentRuntime;
import com.ulyp.core.printers.bytes.BinaryInput;
import com.ulyp.core.printers.bytes.BinaryOutput;
import com.ulyp.core.printers.bytes.BinaryOutputAppender;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class CollectionDebugPrinter extends ObjectBinaryPrinter {

    protected CollectionDebugPrinter(int id) {
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
            ClassDescription itemDescription = decodingContext.getClass(binaryInput.readLong());
            ObjectBinaryPrinter printer = ObjectBinaryPrinterType.printerForId(binaryInput.readLong());
            elements.add(printer.read(itemDescription, binaryInput, decodingContext));
        }
        int notShownElementsCount = (int) (totalElements - writtenElements);
        return new PlainObject(classDescription, "[" +
                elements.stream().map(Printable::print).collect(Collectors.joining()) +
                (notShownElementsCount > 0 ? ", " + notShownElementsCount + " more..." : "") +
                "]"
        );
    }

    @Override
    public void write(Object obj, BinaryOutput out, AgentRuntime agentRuntime) throws Exception {
        try (BinaryOutputAppender appender = out.appender()) {
            Collection<?> collection = (Collection<?>) obj;
            int size = collection.size();

            appender.append(size);
            int elementsToWrite = Math.min(3, size);
            int count = 0;
            Iterator<?> iterator = collection.iterator();
            appender.append(elementsToWrite);
            while (iterator.hasNext() && count <= elementsToWrite) {
                Object element = iterator.next();
                appender.append(agentRuntime.getClassId(element));
                ObjectBinaryPrinter printer = element != null ? ObjectBinaryPrinterType.DYNAMIC_OBJECT_PRINTER.getPrinter() : ObjectBinaryPrinterType.NULL_PRINTER.getPrinter();
                appender.append(printer.getId());
                printer.write(element, appender, agentRuntime);
                count++;
            }
        }
    }
}
