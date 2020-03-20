package com.ulyp.core;

import com.ulyp.core.printers.ObjectBinaryPrinter;
import com.ulyp.core.printers.Printers;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;

public class MethodDescription {

    private final long id;
    private final String className;
    private final String methodName;
    private final boolean returnsSomething;
    private final ObjectBinaryPrinter[] paramPrinters;
    private final ObjectBinaryPrinter resultPrinter;

    public MethodDescription(long id, Executable executable) {
        this.id = id;
        this.className = executable.getDeclaringClass().getName();
        this.methodName = executable instanceof Constructor ? "<init>" : executable.getName();
        this.returnsSomething = !(executable instanceof Method) || !((Method) executable).getReturnType().equals(Void.TYPE);
        this.paramPrinters = Printers.instance.paramPrinters(executable);
        this.resultPrinter = Printers.instance.resultPrinter(executable);
    }

    public long getId() {
        return id;
    }

    public ObjectBinaryPrinter[] getParamPrinters() {
        return paramPrinters;
    }

    public ObjectBinaryPrinter getResultPrinter() {
        return resultPrinter;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public boolean returnsSomething() {
        return returnsSomething;
    }
}
