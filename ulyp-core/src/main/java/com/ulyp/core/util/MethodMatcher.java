package com.ulyp.core.util;

import com.ulyp.core.MethodDescription;

public class MethodMatcher {

    private static final char SEPARATOR = '.';
    private static final String WILDCARD = "*";

    private final String classSimpleName;
    private final String methodSimpleName;
    private final boolean isWildcard;

    public static MethodMatcher parse(String text) {
        int separatorPos = text.lastIndexOf(SEPARATOR);
        // TODO regexp?
        if (separatorPos < 0) {
            throw new SettingsException("");
        }

        return new MethodMatcher(text.substring(0, separatorPos), text.substring(separatorPos + 1));
    }

    public MethodMatcher(Class<?> clazz, String methodSimpleName) {
        this.classSimpleName = clazz.getSimpleName();
        this.methodSimpleName = methodSimpleName;
        this.isWildcard = methodSimpleName.equals(WILDCARD);
    }

    public MethodMatcher(String classSimpleName, String methodSimpleName) {
        this.classSimpleName = classSimpleName;
        this.methodSimpleName = methodSimpleName;
        this.isWildcard = methodSimpleName.equals(WILDCARD);
    }

    public boolean matches(MethodDescription methodRepresentation) {
        return (isWildcard || methodRepresentation.getMethodName().equals(methodSimpleName)) &&
                (methodRepresentation.getDeclaringType().getInterfacesSimpleClassNames().contains(classSimpleName) ||
                        methodRepresentation.getDeclaringType().getSuperClassesSimpleNames().contains(classSimpleName));
    }

    @Override
    public String toString() {
        return classSimpleName + "." + methodSimpleName;
    }
}
