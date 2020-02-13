package com.ulyp.agent.printer;

import com.ulyp.agent.util.ClassUtils;

public class CustomClassPrinter implements Printer {

    public static final Printer instance = new CustomClassPrinter();

    @Override
    public String print(Object obj) {
        return obj != null ? (ClassUtils.getSimpleName(obj.getClass()) + "@" + System.identityHashCode(obj)) : "null";
    }
}
