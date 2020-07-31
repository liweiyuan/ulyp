package com.ulyp.core.printers;

public interface Type {

    boolean isExactlyJavaLangObject();

    boolean isExactlyJavaLangString();

    boolean isNonPrimitveArray();

    boolean isPrimitiveArray();

    boolean isPrimitive();

    boolean isBoxedNumber();

    boolean isEnum();

    boolean isInterface();

    boolean isCollection();

    boolean isClassObject();

    boolean hasToStringMethod();
}
