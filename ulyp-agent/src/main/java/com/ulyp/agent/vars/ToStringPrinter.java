package com.ulyp.agent.vars;

public class ToStringPrinter implements Printer {

    public static final ToStringPrinter instance = new ToStringPrinter();

    @Override
    public String print(Object obj) {
        return obj != null ? obj.toString() : "null";
    }
}
