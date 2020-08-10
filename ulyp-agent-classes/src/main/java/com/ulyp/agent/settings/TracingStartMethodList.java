package com.ulyp.agent.settings;

import com.ulyp.core.util.MethodMatcher;
import com.ulyp.core.MethodDescription;

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
        return methods.isEmpty() || methods.stream().anyMatch(matcher -> matcher.matches(description));
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
