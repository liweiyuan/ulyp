package com.ulyp.agent.settings;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;

import java.util.HashSet;
import java.util.Set;

public class MethodRepresentationBuilder {

    public static MethodRepresentation build(MethodDescription description) {
        Set<String> superClassSimpleNames = new HashSet<>();
        Set<String> interfaceClassSimpleNames = new HashSet<>();

        TypeDefinition type = description.getDeclaringType();

        while (type != null && !type.getActualName().equals("java.lang.Object")) {
            superClassSimpleNames.add(type.getActualName());

            for (TypeDescription.Generic interfface : type.getInterfaces()) {
                interfaceClassSimpleNames.add(interfface.getActualName());
            }

            type = type.getSuperClass();
        }

        return new MethodRepresentation(superClassSimpleNames, interfaceClassSimpleNames, description.getActualName());
    }
}
