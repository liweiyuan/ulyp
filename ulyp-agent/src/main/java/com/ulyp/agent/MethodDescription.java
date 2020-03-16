package com.ulyp.agent;

import com.ulyp.agent.util.MethodInfoUtils;
import com.ulyp.core.printers.ThrowablePrinter;
import com.ulyp.core.printers.ObjectBinaryPrinter;
import com.ulyp.core.printers.Printers;
import com.ulyp.transport.TMethodInfo;

import java.lang.reflect.Executable;

public class MethodDescription {

    private final ObjectBinaryPrinter[] paramPrinters;
    private final ObjectBinaryPrinter resultPrinter;
    private final TMethodInfo methodInfo;

    public MethodDescription(long id, Executable executable) {
        this.methodInfo = MethodInfoUtils.of(id, executable);
        this.paramPrinters = Printers.instance.paramPrinters(executable);
        this.resultPrinter = Printers.instance.resultPrinter(executable);
    }

    public long getId() {
        return methodInfo.getId();
    }

    public ObjectBinaryPrinter[] getParamPrinters() {
        return paramPrinters;
    }

    public ObjectBinaryPrinter getResultPrinter() {
        return resultPrinter;
    }

    public TMethodInfo getMethodInfo() {
        return methodInfo;
    }
}
