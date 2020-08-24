package com.ulyp.core.printers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Printers {

    private static final Printers instance = new Printers();
    private static final ObjectBinaryPrinter[] empty = new ObjectBinaryPrinter[0];
    private static final ObjectBinaryPrinter[] printers;

    static {
        printers = new ObjectBinaryPrinter[ObjectBinaryPrinterType.values().length];

        List<ObjectBinaryPrinterType> printerTypes = new ArrayList<>();
        printerTypes.addAll(Arrays.asList(ObjectBinaryPrinterType.values()));
        printerTypes.sort(Comparator.comparing(ObjectBinaryPrinterType::getOrder));

        for (int i = 0; i < printerTypes.size(); i++) {
            printers[i] = printerTypes.get(i).getInstance();
        }
    }

    public static Printers getInstance() {
        return instance;
    }

    public ObjectBinaryPrinter[] determinePrintersForParameterTypes(List<TypeInfo> paramsTypeInfos) {
        try {
            if (paramsTypeInfos.isEmpty()) {
                return empty;
            }
            ObjectBinaryPrinter[] convs = new ObjectBinaryPrinter[paramsTypeInfos.size()];
            for (int i = 0; i < convs.length; i++) {
                convs[i] = determinePrinterForType(paramsTypeInfos.get(i));
            }
            return convs;
        } catch (Exception e) {
            throw new RuntimeException("Could not prepare converters for method params " + paramsTypeInfos, e);
        }
    }

    public ObjectBinaryPrinter determinePrinterForReturnType(TypeInfo returnTypeInfo) {
        try {
            return determinePrinterForType(returnTypeInfo);
        } catch (Exception e) {
            throw new RuntimeException("Could not prepare converters for method params " + returnTypeInfo, e);
        }
    }

    private static final ConcurrentMap<TypeInfo, ObjectBinaryPrinter> cache = new ConcurrentHashMap<>(1024);

    public ObjectBinaryPrinter determinePrinterForType(TypeInfo typeInfo) {
//        return cache.computeIfAbsent(
//                type, t -> {
//                    for (ObjectBinaryPrinter printer : printers) {
//                        if (printer.supports(t)) {
//                            return printer;
//                        }
//                    }
//                    throw new RuntimeException("Could not find a suitable printer for type " + type);
//                }
//        );
        for (ObjectBinaryPrinter printer : printers) {
            if (printer.supports(typeInfo)) {
                return printer;
            }
        }
        throw new RuntimeException("Could not find a suitable printer for type " + typeInfo);
    }
}
