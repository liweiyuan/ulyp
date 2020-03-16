package com.ulyp.core.printers;

import com.ulyp.core.util.ClassUtils;

import java.util.Collection;

public class CollectionPrinter extends ObjectBinaryPrinter {

    protected CollectionPrinter(int id) {
        super(id);
    }

    @Override
    public void write(Object obj, BinaryStream out) {
        Collection<?> collection = (Collection<?>) obj;
        if(collection == null) {
            out.write("null");
        } else {
            switch (collection.size()) {
                case 0:
                    out.write(ClassUtils.getSimpleName(collection.getClass()) + "{}");
                    break;
                case 1:
                    out.write(ClassUtils.getSimpleName(collection.getClass()) +
                                    "{ " +
//                        IdentityPrinter.instance.print(collection.iterator().next()) +
                                    collection.iterator().next() +
                                    " }"
                    );
                default:
                    out.write(ClassUtils.getSimpleName(collection.getClass()) + "{ " + collection.size() +" }");
            }
        }
    }
}
