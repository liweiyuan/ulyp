package com.ulyp.agent.vars;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Printers {

    public static final Printers instance = new Printers();
    private static final Printer[] empty = new Printer[0];

    public Printer[] paramPrinters(Executable method) {
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
            Printer[] convs = new Printer[parameters.length];
            for (int i = 0; i < convs.length; i++) {
                convs[i] = printerForClass(parameters[i].getType());
            }
            return convs;
        } catch (Exception e) {
            throw new RuntimeException("Could not prepare converters for method params " + method, e);
        }
    }

    public Printer resultPrinter(Executable method) {
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

    private static final ConcurrentMap<Class<?>, Printer> cache = new ConcurrentHashMap<>(1024);

    // TODO printers should tell if they are able to print a paramter, instead of if/else mess
    private Printer printerForClass(Class<?> type) {

        return cache.computeIfAbsent(
                type, t -> {
                    if (t.isPrimitive()) {

                        return ToStringPrinter.instance;
                    } else if (t.getName().equals("java.lang.String")) {

                        return StringPrinter.instance;
                    } else if (t == Class.class) {

                        return ClassPrinter.instance;
                    } else if (isCollection(t)) {

                        return CollectionPrinter.instance;
                    } else if (t.getName().startsWith("java.util.concurrent.atomic")) {

                        return ToStringPrinter.instance;
                    } else if (isNumber(t) || isEnum(t) || isBoolean(t)) {

                        return ToStringPrinter.instance;
                    } else {

                        return CustomClassPrinter.instance;
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

    private boolean isNumber(Class<?> ctClass) {
        if(ctClass == null || ctClass.getName().equals("java.lang.Object")) {
            return false;
        }

        Class<?> ctSuperclass = ctClass.getSuperclass();
        if(ctSuperclass != null && ctSuperclass.getName().equals("java.lang.Number")) {
            return true;
        } else {
            return isNumber(ctSuperclass);
        }
    }

    private boolean isEnum(Class<?> ctClass) {
        return ctClass.isEnum();
    }

    private boolean isBoolean(Class<?> ctClass) {
        return ctClass.getName().equals("java.lang.Boolean");
    }
}
