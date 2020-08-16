package com.test.cases.util;

import com.ulyp.core.util.MethodMatcher;
import com.ulyp.agent.settings.SystemPropertiesSettings;
import com.ulyp.agent.settings.RecordingStartMethodList;
import com.ulyp.agent.transport.UiAddress;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestSettingsBuilder {

    // TODO matcher
    private Class<?> mainClassName;
    private String methodToRecord;

    public String hostName;
    private boolean uiEnabled = true;
    public int port;
    private List<String> instrumentedPackages = new ArrayList<>();
    private List<String> excludedFromInstrumentationPackages = new ArrayList<>();
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

    public String getMethodToRecord() {
        return methodToRecord;
    }

    public List<String> getInstrumentedPackages() {
        return instrumentedPackages;
    }

    public TestSettingsBuilder setRecordCollectionItems(boolean recordCollectionItems) {
        this.recordCollectionItems = recordCollectionItems;
        return this;
    }

    public TestSettingsBuilder setInstrumentedPackages(List<String> instrumentedPackages) {
        this.instrumentedPackages = instrumentedPackages;
        return this;
    }

    public TestSettingsBuilder setMethodToRecord(String startMethod) {
        this.methodToRecord = startMethod;
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
            instrumentedPackages = Collections.singletonList(mainClassName.getPackage().getName());
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

    public List<String> getExcludedFromInstrumentationPackages() {
        return excludedFromInstrumentationPackages;
    }

    public TestSettingsBuilder setExcludedFromInstrumentationPackages(List<String> excludedFromInstrumentationPackages) {
        this.excludedFromInstrumentationPackages = excludedFromInstrumentationPackages;
        return this;
    }

    public SystemPropertiesSettings build() {
        return new SystemPropertiesSettings(
                new UiAddress(hostName, port),
                instrumentedPackages,
                excludedFromInstrumentationPackages,
                new RecordingStartMethodList(new MethodMatcher(mainClassName, methodToRecord)),
                maxDepth,
                maxCallsPerMethod,
                minRecordsForLog
        );
    }
}
