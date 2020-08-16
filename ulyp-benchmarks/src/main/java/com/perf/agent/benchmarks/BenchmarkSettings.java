package com.perf.agent.benchmarks;

import java.util.List;

public class BenchmarkSettings {

    private Class<?> mainClass;

    // TODO Method matcher
    private Class<?> classToRecord;
    private String methodToRecord;

    // TODO PackageList
    private List<String> instrumentedPakages;

    private boolean recordCollectionItems = false;
    private int uiListenPort;

    public Class<?> getMainClass() {
        return mainClass;
    }

    public BenchmarkSettings setMainClass(Class<?> mainClass) {
        this.mainClass = mainClass;
        return this;
    }

    public List<String> getInstrumentedPakages() {
        return instrumentedPakages;
    }

    public BenchmarkSettings setInstrumentedPakages(List<String> instrumentedPakages) {
        this.instrumentedPakages = instrumentedPakages;
        return this;
    }

    public BenchmarkSettings setRecordCollectionItems(boolean recordCollectionItems) {
        this.recordCollectionItems = recordCollectionItems;
        return this;
    }

    public Class<?> getClassToRecord() {
        return classToRecord;
    }

    public BenchmarkSettings setClassToRecord(Class<?> classToRecord) {
        this.classToRecord = classToRecord;
        return this;
    }

    public String getMethodToRecord() {
        return methodToRecord;
    }

    public BenchmarkSettings setMethodToRecord(String methodToRecord) {
        this.methodToRecord = methodToRecord;
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
