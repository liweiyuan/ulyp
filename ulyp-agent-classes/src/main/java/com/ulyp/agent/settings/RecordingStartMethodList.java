package com.ulyp.agent.settings;

import com.ulyp.core.util.MethodMatcher;
import com.ulyp.core.MethodInfo;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RecordingStartMethodList {

    private final List<MethodMatcher> methods;

    public RecordingStartMethodList(MethodMatcher methodMatcher) {
        this.methods = Collections.singletonList(methodMatcher);
    }

    public RecordingStartMethodList(List<String> methods) {
        this.methods = methods.stream().map(MethodMatcher::parse).collect(Collectors.toList());
    }

    public boolean shouldStartRecording(MethodInfo description) {
        return methods.isEmpty() || methods.stream().anyMatch(matcher -> matcher.matches(description));
    }

    public Stream<MethodMatcher> stream() {
        return methods.stream();
    }

    @Override
    public String toString() {
        return "RecordingStartMethodList{" +
                "methods=" + methods +
                '}';
    }
}
