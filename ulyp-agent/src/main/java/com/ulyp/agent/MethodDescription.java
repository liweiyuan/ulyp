package com.ulyp.agent;

import com.ulyp.agent.util.MethodInfoUtils;
import com.ulyp.agent.vars.ExceptionPrinter;
import com.ulyp.agent.vars.Printer;
import com.ulyp.agent.vars.Printers;
import com.ulyp.transport.TMethodInfo;

import java.lang.reflect.Executable;

public class MethodDescription {

    private final Executable executable;
    private final Printer[] paramPrinters;
    private final Printer resultPrinter;
    private final TMethodInfo methodInfo;

    public MethodDescription(long id, Executable executable) {
        this.methodInfo = MethodInfoUtils.of(id, executable);
        this.executable = executable;
        this.paramPrinters = Printers.instance.paramPrinters(executable);
        this.resultPrinter = Printers.instance.resultPrinter(executable);
    }

    public long getId() {
        return methodInfo.getId();
    }

    public Executable getExecutable() {
        return executable;
    }

    public Printer[] getParamPrinters() {
        return paramPrinters;
    }

    public Printer getResultPrinter() {
        return resultPrinter;
    }

    public Printer getExceptionPrinter() {
        return ExceptionPrinter.instance;
    }

    public TMethodInfo getMethodInfo() {
        return methodInfo;
    }
}
