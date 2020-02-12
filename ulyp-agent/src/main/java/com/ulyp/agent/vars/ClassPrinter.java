package com.ulyp.agent.vars;

public class ClassPrinter implements Printer {

    public static final ClassPrinter instance = new ClassPrinter();

    @Override
    public String print(Object obj) {
        if (obj == null) {
            return "null";
        } else {
            Class<?> clazz = (Class<?>) obj;
            return "Class{" + clazz.getName() + "}";
        }
    }
}
