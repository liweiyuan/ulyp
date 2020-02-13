package com.ulyp.agent.printer;

import com.ulyp.agent.util.ClassUtils;

import java.util.Collection;

public class CollectionPrinter implements Printer {

    public static final CollectionPrinter instance = new CollectionPrinter();

    @Override
    public String print(Object obj) {
        Collection<?> collection = (Collection<?>) obj;
        if(collection == null) {
            return "null";
        }
        switch (collection.size()) {
            case 0:
                return ClassUtils.getSimpleName(collection.getClass()) + "{}";
            case 1:
                return ClassUtils.getSimpleName(collection.getClass()) +
                        "{ " +
                        IdentityPrinter.instance.print(collection.iterator().next()) +
                        " }";
            default:
                return ClassUtils.getSimpleName(collection.getClass()) + "{ " + collection.size() +" }";
        }
    }
}
