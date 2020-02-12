package com.ulyp.agent.vars;

import com.ulyp.agent.util.ClassUtils;

public class ExceptionPrinter implements Printer {

    public static final Printer instance = new ExceptionPrinter();

    @Override
    public String print(Object obj) {
        Throwable t = (Throwable) obj;
        return ClassUtils.getSimpleName(t.getClass()) + ": " + StringPrinter.instance.print(t.getMessage());
    }
}
