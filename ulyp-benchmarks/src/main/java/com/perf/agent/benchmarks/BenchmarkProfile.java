package com.perf.agent.benchmarks;

import javax.annotation.Nullable;
import java.util.List;

public class BenchmarkProfile {

    // TODO unite as MethodMatcher
    @Nullable private final Class<?> tracedClass;
    @Nullable private final String tracedMethod;
    private final List<String> instrumentedPackages;

    public BenchmarkProfile(Class<?> tracedClass, String tracedMethod, List<String> instrumentedPackages) {
        this.tracedClass = tracedClass;
        this.tracedMethod = tracedMethod;
        this.instrumentedPackages = instrumentedPackages;
    }

    @Nullable
    public Class<?> getTracedClass() {
        return tracedClass;
    }

    @Nullable
    public String getTracedMethod() {
        return tracedMethod;
    }

    public List<String> getInstrumentedPackages() {
        return instrumentedPackages;
    }

    @Override
    public String toString() {
        if (!instrumentedPackages.isEmpty()) {
            return String.join(",", instrumentedPackages) + "/" + (tracedClass != null ? (tracedClass.getSimpleName() + "." + tracedMethod) : "no tracing");
        } else {
            return "no agent";
        }
    }
}
