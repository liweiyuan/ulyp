package com.ulyp.core.util;

import com.ulyp.core.MethodInfo;

public class MethodMatcher {

    private static final char SEPARATOR = '.';
    private static final String WILDCARD = "*";

    private final String classSimpleName;
    private final String methodName;
    private final boolean isClassNameWildcard;
    private final boolean isMethodWildcard;
    private final boolean isAbsoluteWildcard;

    public static MethodMatcher parse(String text) {
        int separatorPos = text.lastIndexOf(SEPARATOR);
        // TODO regexp?
        if (separatorPos < 0) {
            throw new SettingsException("");
        }

        return new MethodMatcher(text.substring(0, separatorPos), text.substring(separatorPos + 1));
    }

    public MethodMatcher(Class<?> clazz, String methodName) {
        this(clazz.getSimpleName(), methodName);
    }

    public MethodMatcher(String classSimpleName, String methodName) {
        this.classSimpleName = classSimpleName;
        this.methodName = methodName;
        this.isClassNameWildcard = classSimpleName.equals(WILDCARD);
        this.isMethodWildcard = methodName.equals(WILDCARD);
        this.isAbsoluteWildcard = isClassNameWildcard && isMethodWildcard;
    }

    public boolean matches(MethodInfo methodRepresentation) {
        if (isAbsoluteWildcard) {
            return true;
        } else {
            return (isMethodWildcard || methodRepresentation.getMethodName().equals(methodName)) &&
                    (methodRepresentation.getDeclaringType().getInterfacesSimpleClassNames().contains(classSimpleName) ||
                            methodRepresentation.getDeclaringType().getSuperClassesSimpleNames().contains(classSimpleName));
        }
    }

    @Override
    public String toString() {
        return classSimpleName + "." + methodName;
    }
}
