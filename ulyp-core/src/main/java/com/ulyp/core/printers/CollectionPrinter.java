package com.ulyp.core.printers;

import com.ulyp.core.ClassDescription;
import com.ulyp.core.printers.bytes.BinaryInput;
import com.ulyp.core.printers.bytes.BinaryOutput;
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
            if(interfce == Collection.class) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String read(ClassDescription classDescription, BinaryInput binaryInput) {
        return classDescription.getSimpleName() + "{" + binaryInput.readInt() + "}";
    }

    @Override
    public void write(Object obj, BinaryOutput out) throws Exception {
        Collection<?> collection = (Collection<?>) obj;
        out.write(collection.size());
/*        switch (collection.size()) {
            case 0:
                out.write("{}");
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
                out.write("{ " + collection.size() +" }");
        }*/
    }
}
