package com.ulyp.core.printers;

import com.ulyp.core.util.ClassUtils;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Reflection is usually used for RTTI but it's not possible for the agent to use it. Type information
 * must be available way before class is loaded and instrumented. Bytebuddy provides type description but this
 * module CAN NOT use byte buddy since this module is installed at bootstrap class loader search path (and
 * it therefore is visible to any classloader in the instrumented app).
 *
 * As a consequence, this module uses this interface and implementation is provied by byte buddy in ulyp-agent
 * module and UI in ulyp-ui
 */
public interface TypeInfo {

    int getId();

    String getName();

    default String getSimpleName() {
        return ClassUtils.getSimpleNameFromName(getName());
    }

    Set<TypeTrait> getTraits();

    Set<String> getSuperClassesNames();

    Set<String> getInterfacesClassesNames();

    ObjectBinaryPrinter getSuggestedPrinter();

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

    boolean isEnum();

    boolean isInterface();

    boolean isTypeVar();

    boolean isCollection();

    boolean isClassObject();

    boolean hasToStringMethod();
}
