package com.perf.agent.benchmarks;

import java.util.List;

public class BenchmarkSettings {

    private Class<?> mainClass;
    private Class<?> classToTrace;
    private String methodToTrace;
    private List<String> tracedPackages;
    private boolean traceCollections = false;
    private int uiListenPort;

    public Class<?> getMainClass() {
        return mainClass;
    }

    public BenchmarkSettings setMainClass(Class<?> mainClass) {
        this.mainClass = mainClass;
        return this;
    }

    public List<String> getTracedPackages() {
        return tracedPackages;
    }

    public BenchmarkSettings setTracedPackages(List<String> tracedPackages) {
        this.tracedPackages = tracedPackages;
        return this;
    }

    public boolean traceCollections() {
        return traceCollections;
    }

    public BenchmarkSettings setTraceCollections(boolean traceCollections) {
        this.traceCollections = traceCollections;
        return this;
    }

    public Class<?> getClassToTrace() {
        return classToTrace;
    }

    public BenchmarkSettings setClassToTrace(Class<?> classToTrace) {
        this.classToTrace = classToTrace;
        return this;
    }

    public String getMethodToTrace() {
        return methodToTrace;
    }

    public BenchmarkSettings setMethodToTrace(String methodToTrace) {
        this.methodToTrace = methodToTrace;
        return this;
    }

    public int getUiListenPort() {
        return uiListenPort;
    }

    public BenchmarkSettings setUiListenPort(int uiListenPort) {
        this.uiListenPort = uiListenPort;
        return this;
    }
}
