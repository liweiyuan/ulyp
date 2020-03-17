package com.ulyp.core.printers;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Printers {

    public static final Printers instance = new Printers();
    private static final ObjectBinaryPrinter[] empty = new ObjectBinaryPrinter[0];

    public ObjectBinaryPrinter[] paramPrinters(Executable method) {
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
                convs[i] = printerForClass(parameters[i].getType());
            }
            return convs;
        } catch (Exception e) {
            throw new RuntimeException("Could not prepare converters for method params " + method, e);
        }
    }

    public ObjectBinaryPrinter resultPrinter(Executable method) {
        try {
            if (method instanceof Constructor) {
                return printerForClass(method.getDeclaringClass());
            } else {
                return printerForClass(((Method)method).getReturnType());
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not prepare converters for method params " + method, e);
        }
    }

    private static final ConcurrentMap<Class<?>, ObjectBinaryPrinter> cache = new ConcurrentHashMap<>(1024);

    // TODO printers should tell if they are able to print a paramter, instead of if/else mess
    private ObjectBinaryPrinter printerForClass(Class<?> type) {

        return cache.computeIfAbsent(
                type, t -> {
                    if (t.isPrimitive()) {

                        return ObjectBinaryPrinterType.TO_STRING_PRINTER.getPrinter();
                    } else if (t.getName().equals("java.lang.String")) {

                        return ObjectBinaryPrinterType.STRING_PRINTER.getPrinter();
                    } else if (t == Class.class) {

                        return ObjectBinaryPrinterType.CLASS_PRINTER.getPrinter();
                    } /*else if (isCollection(t)) {

                        return ObjectBinaryPrinterType.COLLECTION.getPrinter();
                    } */else if (t.getName().startsWith("java.util.concurrent.atomic")) {

                        return ObjectBinaryPrinterType.TO_STRING_PRINTER.getPrinter();
                    } else if (isBoxedNumber(t) || isBoxedBoolean(t)) {

                        return ObjectBinaryPrinterType.TO_STRING_PRINTER.getPrinter();
                    } else if (isEnum(t)) {

                        return ObjectBinaryPrinterType.ENUM_PRINTER.getPrinter();
                    } else {

                        return ObjectBinaryPrinterType.IDENTITY_PRINTER.getPrinter();
                    }
                }
        );
    }

    private boolean isCollection(Class<?> ctClass) {
        for (Class<?> interfce : ctClass.getInterfaces()) {
            if(interfce.getName().equals("java.util.Collection")) {
                return true;
            }
        }
        return false;
    }

    private boolean isBoxedNumber(Class<?> ctClass) {
        if(ctClass == null || ctClass.getName().equals("java.lang.Object")) {
            return false;
        }

        Class<?> ctSuperclass = ctClass.getSuperclass();
        if(ctSuperclass != null && ctSuperclass.getName().equals("java.lang.Number")) {
            return true;
        } else {
            return isBoxedNumber(ctSuperclass);
        }
    }

    private boolean isEnum(Class<?> ctClass) {
        return ctClass.isEnum();
    }

    private boolean isBoxedBoolean(Class<?> ctClass) {
        return ctClass.getName().equals("java.lang.Boolean");
    }
}
