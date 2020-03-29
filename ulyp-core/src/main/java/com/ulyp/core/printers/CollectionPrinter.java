package com.ulyp.core.printers;

import com.ulyp.core.util.ClassUtils;

import java.util.Collection;

public class CollectionPrinter extends ObjectBinaryPrinter {

    protected CollectionPrinter(int id) {
        super(id);
    }

    @Override
    boolean supports(Class<?> clazz) {
        return isCollection(clazz);
    }

    private boolean isCollection(Class<?> clazz) {
        for (Class<?> interfce : clazz.getInterfaces()) {
            if(interfce.getName().equals("java.util.Collection")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void write(Object obj, BinaryOutput out) {
        Collection<?> collection = (Collection<?>) obj;
        if(collection == null) {
            out.write("null");
        } else {
            switch (collection.size()) {
                case 0:
                    out.write(ClassUtils.getSimpleName(collection.getClass()) + "{}");
                    return;
//                case 1:
//                    out.write(ClassUtils.getSimpleName(collection.getClass()) +
//                                    "{ " +
////                        IdentityPrinter.instance.print(collection.iterator().next()) +
////                                    collection.iterator().next() +
//                                    " }"
//                    );
//                    return;
                default:
                    out.write(ClassUtils.getSimpleName(collection.getClass()) + "{ " + collection.size() +" }");
            }
        }
    }
}
