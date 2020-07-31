package com.ulyp.agent.settings;

import com.ulyp.core.printers.Type;
import net.bytebuddy.description.type.TypeDescription;

public class ByteBuddyType implements Type {

    private final TypeDescription.Generic type;

    public ByteBuddyType(TypeDescription.Generic type) {
        this.type = type;
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
        return type.isArray();
    }

    @Override
    public boolean isPrimitiveArray() {
        // TODO
        return false;
    }

    @Override
    public boolean isPrimitive() {
        return type.isPrimitive();
    }

    @Override
    public boolean isBoxedNumber() {
        // TODO implement
        return false;
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
        // TODO
        return false;
    }

    @Override
    public boolean isClassObject() {
        return type == TypeDescription.Generic.CLASS;
    }

    @Override
    public boolean hasToStringMethod() {
        // TODO
        return false;
    }
}
