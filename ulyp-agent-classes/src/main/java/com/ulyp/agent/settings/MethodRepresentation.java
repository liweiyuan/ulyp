package com.ulyp.agent.settings;

import com.ulyp.core.util.ClassUtils;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Byte-buddy wrapper. This module can't use bytebuddy, ...
 */
public class MethodRepresentation {

    private final Set<String> superClassesSimpleNames;
    private final Set<String> interfacesSimpleClassNames;
    private final String methodName;

    public MethodRepresentation(Set<String> superClassesSimpleNames, Set<String> interfacesSimpleClassNames, String methodName) {
        this.superClassesSimpleNames = superClassesSimpleNames.stream().map(ClassUtils::getSimpleNameFromName).collect(Collectors.toSet());
        this.interfacesSimpleClassNames = interfacesSimpleClassNames.stream().map(ClassUtils::getSimpleNameFromName).collect(Collectors.toSet());
        this.methodName = methodName;
    }

    public Set<String> getSuperClassesSimpleNames() {
        return superClassesSimpleNames;
    }

    public Set<String> getInterfacesSimpleClassNames() {
        return interfacesSimpleClassNames;
    }

    public String getMethodName() {
        return methodName;
    }
}
