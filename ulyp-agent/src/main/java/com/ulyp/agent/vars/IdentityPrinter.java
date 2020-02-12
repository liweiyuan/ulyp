package com.ulyp.agent.vars;

import com.ulyp.agent.util.ClassUtils;

public class IdentityPrinter implements Printer {

    public static final IdentityPrinter instance = new IdentityPrinter();

    @Override
    public String print(Object obj) {
        if (obj == null) {
            return "null";
        }

        return ClassUtils.getSimpleName(obj.getClass()) + "@" + System.identityHashCode(obj);
    }
}
