package com.ulyp.core;

import com.ulyp.core.printers.ObjectBinaryPrinter;
import com.ulyp.core.printers.Printers;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MethodDescription {

    private final long id;
    private final ClassDescription classDescription;
    private final String methodName;
    private final String toStringCached;
    private final boolean returnsSomething;
    private final List<String> parameterNames;
    private final ObjectBinaryPrinter[] paramPrinters;
    private final ObjectBinaryPrinter resultPrinter;

    public MethodDescription(long methodId, ClassDescription classDescription, Executable executable) {
        this.id = methodId;
        this.classDescription = classDescription;
        this.methodName = executable instanceof Constructor ? "<init>" : executable.getName();
        this.toStringCached = classDescription.getSimpleName() + "." + methodName;
        this.returnsSomething = !(executable instanceof Method) || !((Method) executable).getReturnType().equals(Void.TYPE);
        this.paramPrinters = Printers.getInstance().determinePrintersForParameterTypes(executable);
        this.resultPrinter = Printers.getInstance().determinePrinterForReturnType(executable);
        boolean hasParamNames = executable.getParameterCount() > 0 && executable.getParameters()[0].isNamePresent();
        if (hasParamNames) {
            this.parameterNames = new ArrayList<>(executable.getParameterCount());
            for (Parameter parameter : executable.getParameters()) {
                parameterNames.add(parameter.getName());
            }
        } else {
            this.parameterNames = Collections.emptyList();
        }
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

    public ClassDescription getClassDescription() {
        return classDescription;
    }

    public String getMethodName() {
        return methodName;
    }

    public boolean returnsSomething() {
        return returnsSomething;
    }

    public List<String> getParameterNames() {
        return parameterNames;
    }

    @Override
    public String toString() {
        return toStringCached;
    }
}
