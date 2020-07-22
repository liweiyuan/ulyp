package com.ulyp.agent;

import net.bytebuddy.description.type.TypeDescription;

public class MethodMatcher {

    private static final String WILDCARD = "*";

    private final String classSimpleName;
    private final String methodSimpleName;
    private final boolean isWildcard;

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

    public boolean matchesExact(TypeDescription.Generic clazzType, String methodName) {
        return (isWildcard || methodName.equals(methodSimpleName)) && getSimpleName(clazzType.getActualName()).equals(classSimpleName);
    }

    private String getSimpleName(String name) {
        int sepPos = -1;
        for (int i = name.length() - 1; i >= 0; i--) {
            char c = name.charAt(i);
            if (c == '.' || c == '$') {
                sepPos = i;
                break;
            }
        }

        if (sepPos > 0) {
            return name.substring(sepPos + 1);
        } else {
            return name;
        }
    }

    @Override
    public String toString() {
        return classSimpleName + "." + methodSimpleName;
    }
}
