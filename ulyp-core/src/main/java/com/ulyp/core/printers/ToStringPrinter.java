package com.ulyp.core.printers;

public class ToStringPrinter extends ObjectBinaryPrinter {

    protected ToStringPrinter(int id) {
        super(id);
    }

    @Override
    boolean supports(Class<?> clazz) {
        return clazz.isPrimitive() || isBoxedNumber(clazz) || isBoxedBoolean(clazz);
    }

    @Override
    public void write(Object obj, BinaryStream out) {
        out.write(obj != null ? obj.toString() : "null");
    }

    private boolean isBoxedNumber(Class<?> ctClass) {
        if(ctClass == null || ctClass.getName().equals("java.lang.Object")) {
            return false;
        }

        Class<?> ctSuperclass = ctClass.getSuperclass();
        if(ctSuperclass != null && ctSuperclass.getName().equals("java.lang.Number")) {
            return true;
        } else {
            return isBoxedNumber(ctSuperclass);
        }
    }

    private boolean isBoxedBoolean(Class<?> ctClass) {
        return ctClass.getName().equals("java.lang.Boolean");
    }
}
