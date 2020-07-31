package com.ulyp.core;

import com.ulyp.core.printers.ObjectBinaryPrinter;
import com.ulyp.core.printers.Printers;
import com.ulyp.core.printers.Type;
import com.ulyp.core.util.ClassUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MethodDescription {

    private final long id;
    private final Set<String> superClassesNames;
    private final Set<String> interfacesClassesNames;
    private final ClassDescription classDescription;
    private final String methodName;
    private final String toStringCached;
    private final boolean returnsSomething;
    private final List<String> parameterNames;
    private final ObjectBinaryPrinter[] paramPrinters;
    private final ObjectBinaryPrinter resultPrinter;

    public MethodDescription(
            long methodId,
            String methodName,
            boolean returnsSomething,
            Set<String> superClassesNames,
            Set<String> interfacesClassNames,
            List<Type> paramsTypes,
            Type returnType,
            ClassDescription classDescription)
    {
        this.id = methodId;
        this.classDescription = classDescription;
        this.methodName = methodName;
        this.toStringCached = classDescription.getSimpleName() + "." + methodName;
        this.superClassesNames = superClassesNames;
        this.interfacesClassesNames = interfacesClassNames;
        this.returnsSomething = returnsSomething;

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
