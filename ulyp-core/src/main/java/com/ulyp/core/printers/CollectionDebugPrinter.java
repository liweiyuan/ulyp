package com.ulyp.core.printers;

import com.ulyp.core.AgentRuntime;
import com.ulyp.core.DecodingContext;
import com.ulyp.core.printers.bytes.BinaryInput;
import com.ulyp.core.printers.bytes.BinaryOutput;
import com.ulyp.core.printers.bytes.BinaryOutputAppender;

import java.util.Collection;
import java.util.Iterator;

public class CollectionDebugPrinter extends ObjectBinaryPrinter {

    protected CollectionDebugPrinter(byte id) {
        super(id);
    }

    @Override
    boolean supports(TypeInfo type) {
        // used manually
        return false;
    }

    @Override
    public ObjectRepresentation read(TypeInfo classDescription, BinaryInput input, DecodingContext decodingContext) {
        /*long totalElements = input.readLong();
        List<Printable> elements = new ArrayList<>();
        long writtenElements = input.readLong();
        for (int i = 0; i < writtenElements; i++) {
            TypeInfo itemDescription = decodingContext.getType(input.readLong());
            ObjectBinaryPrinter printer = ObjectBinaryPrinterType.printerForId(input.readByte());
            elements.add(printer.read(itemDescription, input, decodingContext));
        }
        int notShownElementsCount = (int) (totalElements - writtenElements);
        return new PlainObjectRepresentation(classDescription, "[" +
                elements.stream().map(Printable::print).collect(Collectors.joining()) +
                (notShownElementsCount > 0 ? ", " + notShownElementsCount + " more..." : "") +
                "]"
        );*/
        return null;
    }

    @Override
    public void write(Object object, TypeInfo classDescription, BinaryOutput out, AgentRuntime runtime) throws Exception {
        try (BinaryOutputAppender appender = out.appender()) {
            Collection<?> collection = (Collection<?>) object;
            int size = collection.size();

            appender.append(size);
            int elementsToWrite = Math.min(3, size);
            int count = 0;
            Iterator<?> iterator = collection.iterator();
            appender.append(elementsToWrite);
            while (iterator.hasNext() && count <= elementsToWrite) {
                Object element = iterator.next();
                appender.append(runtime.get(element).getId());
                ObjectBinaryPrinter printer = element != null ? ObjectBinaryPrinterType.DYNAMIC_OBJECT_PRINTER.getInstance() : ObjectBinaryPrinterType.NULL_PRINTER.getInstance();
                appender.append(printer.getId());
                printer.write(element, appender, runtime);
                count++;
            }
        }
    }
}
