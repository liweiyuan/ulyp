package com.ulyp.core;

import com.ulyp.core.printers.ObjectArrayPrinter;
import com.ulyp.core.printers.ObjectBinaryPrinterType;

public class RecordingParamsUpdater {

    public RecordingParamsUpdater() {
        updateRecordCollectionItems(false);
    }

    public void updateRecordCollectionItems(boolean value) {
//        CollectionPrinter collectionPrinter = (CollectionPrinter) ObjectBinaryPrinterType.COLLECTION_PRINTER.getPrinter();
//        collectionPrinter.setShouldRecordItems(value);
        ObjectArrayPrinter objectArrayPrinter = (ObjectArrayPrinter) ObjectBinaryPrinterType.OBJECT_ARRAY_PRINTER.getInstance();
        objectArrayPrinter.setShouldRecordItems(value);
    }
}
