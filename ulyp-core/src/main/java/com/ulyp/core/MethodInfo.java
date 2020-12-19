package com.ulyp.core;

import com.ulyp.core.printers.ObjectBinaryPrinter;
import com.ulyp.core.printers.ObjectBinaryPrinterType;
import com.ulyp.core.printers.Printers;
import com.ulyp.core.printers.TypeInfo;

import java.util.Arrays;
import java.util.List;

public class MethodInfo {

    private final int id;
    private final String methodName;
    private final TypeInfo declaringTypeInfo;
    private final boolean isStatic;
    private final boolean isConstructor;
    private final boolean returnsSomething;
    private final ObjectBinaryPrinter[] paramPrinters;
    private final ObjectBinaryPrinter resultPrinter;

    public MethodInfo(
            int methodId,
            String methodName,
            boolean isConstructor,
            boolean isStatic,
            boolean returnsSomething,
            List<TypeInfo> paramsTypeInfos,
            TypeInfo returnTypeInfo,
            TypeInfo declaringTypeInfo)
    {
        this.id = methodId;
        if (!isConstructor) {
            this.methodName = methodName;
        } else {
            this.methodName = "<init>";
        }
        this.isConstructor = isConstructor;
        this.returnsSomething = isConstructor || returnsSomething;
        this.declaringTypeInfo = declaringTypeInfo;
        this.isStatic = isStatic;

        this.paramPrinters = Printers.getInstance().determinePrintersForParameterTypes(paramsTypeInfos);
        this.resultPrinter = isConstructor ?
                ObjectBinaryPrinterType.IDENTITY_PRINTER.getInstance() :
                Printers.getInstance().determinePrinterForReturnType(returnTypeInfo);
    }

    public int getId() {
        return id;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public boolean isConstructor() {
        return isConstructor;
    }

    public ObjectBinaryPrinter[] getParamPrinters() {
        return paramPrinters;
    }

    public ObjectBinaryPrinter getResultPrinter() {
        return resultPrinter;
    }

    public TypeInfo getDeclaringType() {
        return declaringTypeInfo;
    }

    public String getMethodName() {
        return methodName;
    }

    public boolean returnsSomething() {
        return returnsSomething;
    }

    @Override
    public String toString() {
        return "MethodDescription{" +
                "id=" + id +
                ", methodName='" + methodName + '\'' +
                ", declaringType=" + declaringTypeInfo +
                ", isStatic=" + isStatic +
                ", returnsSomething=" + returnsSomething +
                ", paramPrinters=" + Arrays.toString(paramPrinters) +
                ", resultPrinter=" + resultPrinter +
                '}';
    }
}
