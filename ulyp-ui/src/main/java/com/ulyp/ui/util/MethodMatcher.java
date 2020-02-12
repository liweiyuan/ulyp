package com.ulyp.ui.util;

public class MethodMatcher {
    private final String classSimpleName;
    private final String methodSimpleName;

    MethodMatcher(String raw) {
        this.classSimpleName = raw.substring(0, raw.indexOf('.'));
        this.methodSimpleName = raw.substring(raw.indexOf('.') + 1);
    }

    public boolean matchesExact(String className, String methodName) {
        return (classSimpleName.equals("*") || StringUtils.toSimpleName(className).equals(classSimpleName)) &&
                (methodSimpleName.equals("*") || methodName.equals(methodSimpleName));
    }

    @Override
    public String toString() {
        return "Exec{" +
                "classSimpleName='" + classSimpleName + '\'' +
                ", methodSimpleName='" + methodSimpleName + '\'' +
                '}';
    }
}
