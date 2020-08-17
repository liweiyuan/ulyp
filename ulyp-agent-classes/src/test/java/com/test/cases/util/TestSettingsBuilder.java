package com.test.cases.util;

import com.ulyp.core.util.MethodMatcher;
import com.ulyp.agent.settings.SystemPropertiesSettings;
import com.ulyp.agent.settings.RecordingStartMethodList;
import com.ulyp.agent.transport.GrpcUiAddress;
import com.ulyp.core.util.PackageList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestSettingsBuilder {

    // TODO matcher
    private Class<?> mainClassName;
    private MethodMatcher methodToRecord;

    public String hostName;
    private boolean uiEnabled = true;
    public int port;
    private PackageList instrumentedPackages = new PackageList();
    private PackageList excludedFromInstrumentationPackages = new PackageList();
    private int minRecordsForLog = 1;
    private int maxDepth = Integer.MAX_VALUE;
    private int maxCallsPerMethod = Integer.MAX_VALUE;

    private boolean recordCollectionItems = false;

    public boolean isUiEnabled() {
        return uiEnabled;
    }

    public void setUiEnabled(boolean uiEnabled) {
        this.uiEnabled = uiEnabled;
    }

    public Class<?> getMainClassName() {
        return mainClassName;
    }

    public boolean getRecordCollectionItems() {
        return recordCollectionItems;
    }

    public PackageList getInstrumentedPackages() {
        return instrumentedPackages;
    }

    public TestSettingsBuilder setRecordCollectionItems(boolean recordCollectionItems) {
        this.recordCollectionItems = recordCollectionItems;
        return this;
    }

    public TestSettingsBuilder setInstrumentedPackages(String... packages) {
        this.instrumentedPackages = new PackageList(packages);
        return this;
    }

    public MethodMatcher getMethodToRecord() {
        return methodToRecord;
    }

    public TestSettingsBuilder setMethodToRecord(MethodMatcher methodToRecord) {
        this.methodToRecord = methodToRecord;
        return this;
    }

    public TestSettingsBuilder setMethodToRecord(String startMethod) {
        if (mainClassName != null) {
            this.methodToRecord = new MethodMatcher(mainClassName, startMethod);
        } else {
            throw new IllegalArgumentException("Please set main class name first");
        }
        return this;
    }

    public TestSettingsBuilder setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
        return this;
    }

    public int getMaxCallsPerMethod() {
        return maxCallsPerMethod;
    }

    public TestSettingsBuilder setMaxCallsPerMethod(int maxCallsPerMethod) {
        this.maxCallsPerMethod = maxCallsPerMethod;
        return this;
    }

    public TestSettingsBuilder setMainClassName(Class<?> mainClassName) {
        this.mainClassName = mainClassName;
        if (instrumentedPackages.isEmpty()) {
            instrumentedPackages = new PackageList(mainClassName.getPackage().getName());
        }
        return this;
    }

    public TestSettingsBuilder setHostName(String hostName) {
        this.hostName = hostName;
        return this;
    }

    public TestSettingsBuilder setPort(int port) {
        this.port = port;
        return this;
    }

    public TestSettingsBuilder setMinRecordsForLog(int minRecordsForLog) {
        this.minRecordsForLog = minRecordsForLog;
        return this;
    }

    public PackageList getExcludedFromInstrumentationPackages() {
        return excludedFromInstrumentationPackages;
    }

    public TestSettingsBuilder setExcludedFromInstrumentationPackages(String... packages) {
        this.excludedFromInstrumentationPackages = new PackageList(packages);
        return this;
    }
}
