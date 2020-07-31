package com.ulyp.agent.util;

import com.ulyp.core.printers.Type;
import com.ulyp.core.util.ClassUtils;
import net.bytebuddy.description.type.TypeDescription;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

// TODO tests for this?
public class ByteBuddyType implements Type {

    private final TypeDescription.Generic type;
    private final Set<String> superClassesNames = new HashSet<>();
    private final Set<String> interfacesClassesNames = new HashSet<>();

    public ByteBuddyType(TypeDescription.Generic type) {
        this.type = type;

        while (type != null && !type.getActualName().equals("java.lang.Object")) {
            superClassesNames.add(type.getActualName());

            for (TypeDescription.Generic interfface : type.getInterfaces()) {
                interfacesClassesNames.add(interfface.getActualName());
            }

            type = type.getSuperClass();
        }
    }

    public Set<String> getSuperClassesNames() {
        return superClassesNames;
    }

    public Set<String> getInterfacesClassesNames() {
        return interfacesClassesNames;
    }

    public Set<String> getSuperClassesSimpleNames() {
        return superClassesNames.stream().map(ClassUtils::getSimpleNameFromName).collect(Collectors.toSet());
    }

    public Set<String> getInterfacesSimpleClassNames() {
        return interfacesClassesNames.stream().map(ClassUtils::getSimpleNameFromName).collect(Collectors.toSet());
    }

    @Override
    public boolean isExactlyJavaLangObject() {
        return type == TypeDescription.Generic.OBJECT;
    }

    @Override
    public boolean isExactlyJavaLangString() {
        return type == TypeDescription.Generic.OfNonGenericType.ForLoadedType.of(String.class);
    }

    @Override
    public boolean isNonPrimitveArray() {
        return type.isArray() && !type.getComponentType().isPrimitive();
    }

    @Override
    public boolean isPrimitiveArray() {
        return type.isArray() && type.getComponentType().isPrimitive();
    }

    @Override
    public boolean isPrimitive() {
        return type.isPrimitive();
    }

    @Override
    public boolean isBoxedNumber() {
        return interfaceClassSimpleNames.contains("java.lang.Number");
    }

    @Override
    public boolean isEnum() {
        return type.isEnum();
    }

    @Override
    public boolean isInterface() {
        return type.isInterface();
    }

    @Override
    public boolean isCollection() {
        return interfaceClassSimpleNames.contains("java.util.Collection");
    }

    @Override
    public boolean isClassObject() {
        return type == TypeDescription.Generic.CLASS;
    }

    @Override
    public boolean hasToStringMethod() {
        return type.getDeclaredMethods().stream().anyMatch(
                method -> method.getActualName().equals("toString") && method.getReturnType() == TypeDescription.Generic.OfNonGenericType.ForLoadedType.of(String.class)
        );
    }
}
