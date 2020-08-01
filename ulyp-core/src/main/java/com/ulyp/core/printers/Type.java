package com.ulyp.core.printers;

import com.ulyp.core.util.ClassUtils;

import java.util.Set;
import java.util.stream.Collectors;

public interface Type {

    String getName();

    Set<String> getSuperClassesNames();

    Set<String> getInterfacesClassesNames();

    default Set<String> getSuperClassesSimpleNames() {
        return getSuperClassesNames().stream().map(ClassUtils::getSimpleNameFromName).collect(Collectors.toSet());
    }

    default Set<String> getInterfacesSimpleClassNames() {
        return getInterfacesClassesNames().stream().map(ClassUtils::getSimpleNameFromName).collect(Collectors.toSet());
    }

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
