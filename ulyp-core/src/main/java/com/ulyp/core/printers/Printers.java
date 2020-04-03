package com.ulyp.core.printers;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
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
            printers[i] = printerTypes.get(i).getPrinter();
        }
    }

    public static Printers getInstance() {
        return instance;
    }

    public ObjectBinaryPrinter[] determinePrintersForParameterTypes(Executable method) {
        try {
            Parameter[] parameters;
            try {
                parameters = method.getParameters();
            } catch(Exception e) {
                return empty;
            }

            if (parameters.length == 0) {
                return empty;
            }
            ObjectBinaryPrinter[] convs = new ObjectBinaryPrinter[parameters.length];
            for (int i = 0; i < convs.length; i++) {
                convs[i] = determinePrinterForType(parameters[i].getType());
            }
            return convs;
        } catch (Exception e) {
            throw new RuntimeException("Could not prepare converters for method params " + method, e);
        }
    }

    public ObjectBinaryPrinter determinePrinterForReturnType(Executable method) {
        try {
            if (method instanceof Constructor) {
                return determinePrinterForType(method.getDeclaringClass());
            } else {
                return determinePrinterForType(((Method)method).getReturnType());
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not prepare converters for method params " + method, e);
        }
    }

    private static final ConcurrentMap<Class<?>, ObjectBinaryPrinter> cache = new ConcurrentHashMap<>(1024);

    public ObjectBinaryPrinter determinePrinterForType(Class<?> type) {
        return cache.computeIfAbsent(
                type, t -> {
                    for (ObjectBinaryPrinter printer : printers) {
                        if (printer.supports(t)) {
                            return printer;
                        }
                    }
                    throw new RuntimeException("Could not find a suitable printer for type " + type);
                }
        );
    }
}
