package com.ulyp.agent.util;

import com.ulyp.core.printers.Type;
import net.bytebuddy.description.type.TypeDescription;

import java.util.HashSet;
import java.util.Set;

public class ByteBuddyType implements Type {

    private final TypeDescription.Generic type;
    private final Set<String> superClassesNames = new HashSet<>();
    private final Set<String> interfacesClassesNames = new HashSet<>();

    public ByteBuddyType(TypeDescription.Generic type) {
        this.type = type;

        while (type != null && !type.equals(TypeDescription.Generic.OBJECT)) {
            superClassesNames.add(type.getActualName());

            for (TypeDescription.Generic interfface : type.getInterfaces()) {
                interfacesClassesNames.add(interfface.getActualName());
            }

            type = type.getSuperClass();
        }
    }

    @Override
    public String getName() {
        return type.getActualName();
    }

    public Set<String> getSuperClassesNames() {
        return superClassesNames;
    }

    public Set<String> getInterfacesClassesNames() {
        return interfacesClassesNames;
    }

    @Override
    public boolean isExactlyJavaLangObject() {
        return type.equals(TypeDescription.Generic.OBJECT);
    }

    @Override
    public boolean isExactlyJavaLangString() {
        return type.equals(TypeDescription.Generic.OfNonGenericType.ForLoadedType.of(String.class));
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
        return getSuperClassesNames().contains("java.lang.Number");
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
        return getInterfacesClassesNames().contains("java.util.Collection");
    }

    @Override
    public boolean isClassObject() {
        return type.equals(TypeDescription.Generic.CLASS);
    }

    @Override
    public boolean hasToStringMethod() {
        return type.getDeclaredMethods().stream().anyMatch(
                method -> method.getActualName().equals("toString") && method.getReturnType().equals(TypeDescription.Generic.OfNonGenericType.ForLoadedType.of(String.class))
        );
    }
}
