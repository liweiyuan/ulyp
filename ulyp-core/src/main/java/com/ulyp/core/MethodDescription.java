package com.ulyp.core;

import com.ulyp.core.printers.ObjectBinaryPrinter;
import com.ulyp.core.printers.Printers;
import com.ulyp.core.printers.Type;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MethodDescription {

    private final long id;
    private final String methodName;
    private final Type declaringType;
    private final boolean isStatic;
    private final boolean returnsSomething;
    private final List<String> parameterNames;
    private final ObjectBinaryPrinter[] paramPrinters;
    private final ObjectBinaryPrinter resultPrinter;

    public MethodDescription(
            long methodId,
            String methodName,
            boolean isStatic,
            boolean returnsSomething,
            List<Type> paramsTypes,
            Type returnType,
            Type declaringType)
    {
        this.id = methodId;
        this.methodName = methodName;
        this.returnsSomething = returnsSomething;
        this.declaringType = declaringType;
        this.isStatic = isStatic;

        this.paramPrinters = Printers.getInstance().determinePrintersForParameterTypes(paramsTypes);
        this.resultPrinter = Printers.getInstance().determinePrinterForReturnType(returnType);

//        boolean hasParamNames = executable.getParameterCount() > 0 && executable.getParameters()[0].isNamePresent();
//        if (hasParamNames) {
//            this.parameterNames = new ArrayList<>(executable.getParameterCount());
//            for (Parameter parameter : executable.getParameters()) {
//                parameterNames.add(parameter.getName());
//            }
//        } else {
            this.parameterNames = Collections.emptyList();
//        }
    }

    public long getId() {
        return id;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public ObjectBinaryPrinter[] getParamPrinters() {
        return paramPrinters;
    }

    public ObjectBinaryPrinter getResultPrinter() {
        return resultPrinter;
    }

    public Type getDeclaringType() {
        return declaringType;
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
        return "MethodDescription{" +
                "id=" + id +
                ", methodName='" + methodName + '\'' +
                ", declaringType=" + declaringType +
                ", isStatic=" + isStatic +
                ", returnsSomething=" + returnsSomething +
                ", parameterNames=" + parameterNames +
                ", paramPrinters=" + Arrays.toString(paramPrinters) +
                ", resultPrinter=" + resultPrinter +
                '}';
    }
}
