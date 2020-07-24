package com.ulyp.agent.settings;

import com.ulyp.agent.MethodMatcher;

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

    public boolean shouldStartTracing(MethodRepresentation description) {
        return methods.isEmpty() || methods.stream().anyMatch(matcher -> matcher.matches(description));
    }

//    private boolean methodMatches(MethodRepresentation description) {
//        try {
//            if (classMatches(description.getDeclaringType().asGenericType(), description.getActualName())) {
//                return true;
//            }
//
//            return hasSuperclassThatMatches(description.getDeclaringType().getSuperClass(), description.getActualName());
//        } catch (Exception e) {
//            System.err.println("ERROR while checking if should start profiling: " + e.getMessage());
//            return false;
//        }
//    }
//
//    private boolean classMatches(TypeDescription.Generic clazzType, String methodName) {
//        for (MethodMatcher exec : methods) {
//            if (exec.matches(clazzType, methodName)) {
//                return true;
//            }
//        }
//        for (TypeDescription.Generic ctInterface : clazzType.getInterfaces()) {
//            if (classMatches(ctInterface, methodName)) {
//                return true;
//            }
//        }
//
//        return false;
//    }
//
//    private boolean hasSuperclassThatMatches(TypeDescription.Generic clazzType, String methodName) {
//        if(clazzType == null || clazzType.getTypeName().equals("java.lang.Object")) {
//            return false;
//        }
//
//        return classMatches(clazzType, methodName) || hasSuperclassThatMatches(clazzType.getSuperClass(), methodName);
//    }

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
