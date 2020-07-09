package com.ulyp.agent;

import net.bytebuddy.description.type.TypeDescription;

public class MethodMatcher {
    private final String classSimpleName;
    private final String methodSimpleName;
    private final boolean isWildcard;

    public MethodMatcher(Class<?> clazz, String methodSimpleName) {
        this.classSimpleName = clazz.getSimpleName();
        this.methodSimpleName = methodSimpleName;
        this.isWildcard = methodSimpleName.equals("*");
    }

    public MethodMatcher(String classSimpleName, String methodSimpleName) {
        this.classSimpleName = classSimpleName;
        this.methodSimpleName = methodSimpleName;
        this.isWildcard = methodSimpleName.equals("*");
    }

    public boolean matchesExact(TypeDescription.Generic clazzType, String methodName) {
        return getSimpleName(clazzType.getActualName()).equals(classSimpleName) && (isWildcard || methodName.equals(methodSimpleName));
    }

    private String getSimpleName(String name) {
        int lastDot = name.lastIndexOf('.');
        if (lastDot > 0) {
            return name.substring(lastDot + 1);
        } else {
            return name;
        }
    }

    @Override
    public String toString() {
        return classSimpleName + "." + methodSimpleName;
    }
}
