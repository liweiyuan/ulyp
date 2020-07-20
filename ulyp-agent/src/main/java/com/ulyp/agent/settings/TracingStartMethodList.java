package com.ulyp.agent.settings;

import com.ulyp.agent.MethodMatcher;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TracingStartMethodList {

    private final List<MethodMatcher> methods;

    public TracingStartMethodList(MethodMatcher methodMatcher) {
        this.methods = Collections.singletonList(methodMatcher);
    }

    public TracingStartMethodList(List<String> methods) {
        if (methods != null) {
            // TODO method matcher to have a proper constuctor
            this.methods = methods.stream()
                    .map(str -> new MethodMatcher(
                            str.substring(0, str.indexOf('.')),
                            str.substring(str.indexOf('.') + 1))
                    ).collect(Collectors.toList());
        } else {
            this.methods = Collections.emptyList();
        }
    }

    public boolean shouldStartTracing(MethodDescription description) {
        return methods.isEmpty() || methodMatches(methods, description);
    }

    private boolean methodMatches(List<MethodMatcher> methodsToMatch, MethodDescription description) {
        try {
            boolean profileThis = classMatches(
                    methodsToMatch,
                    description.getDeclaringType().asGenericType(),
                    description.getActualName());
            if (profileThis) {
                return true;
            }

            for (TypeDescription.Generic ctInterface : description.getDeclaringType().asGenericType().getInterfaces()) {
                if (classMatches(methodsToMatch, ctInterface, description.getActualName())) {
                    return true;
                }
            }

            return hasSuperclassThatMatches(
                    methodsToMatch,
                    description.getDeclaringType().getSuperClass(),
                    description.getActualName()
            );
        } catch (Exception e) {
            System.err.println("ERROR while checking if should start profiling: " + e.getMessage());
            return false;
        }
    }

    private boolean classMatches(
            List<MethodMatcher> methodToStartProfiling,
            TypeDescription.Generic clazzType,
            String methodName)
    {
        for (MethodMatcher exec : methodToStartProfiling) {
            if (exec.matchesExact(clazzType, methodName)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasSuperclassThatMatches(
            List<MethodMatcher> methodsToMatch,
            TypeDescription.Generic clazzType,
            String methodName)
    {
        if(clazzType == null || clazzType.getTypeName().equals("java.lang.Object")) {
            return false;
        }

        return classMatches(methodsToMatch, clazzType, methodName) ||
                hasSuperclassThatMatches(methodsToMatch, clazzType.getSuperClass(), methodName);
    }

    public Stream<MethodMatcher> stream() {
        return methods.stream();
    }

    @Override
    public String toString() {
        return "TracingStartMethodList{" +
                "methods=" + methods +
                '}';
    }
}
