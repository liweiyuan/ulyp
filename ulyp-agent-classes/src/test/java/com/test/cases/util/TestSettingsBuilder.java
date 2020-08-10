package com.test.cases.util;

import com.ulyp.agent.MethodMatcher;
import com.ulyp.agent.settings.SystemPropertiesSettings;
import com.ulyp.agent.settings.TracingStartMethodList;
import com.ulyp.agent.transport.UiAddress;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TestSettingsBuilder {
    public TestSettingsBuilder set;
    private Class<?> mainClassName;
    private String methodToTrace;
    public String hostName;
    private boolean uiEnabled = true;
    public int port;
    private List<String> instrumentedPackages = new ArrayList<>();
    private List<String> excludedFromInstrumentationPackages = new ArrayList<>();
    private int minTraceCount = 1;
    private int maxDepth = Integer.MAX_VALUE;
    private int maxCallsPerMethod = Integer.MAX_VALUE;

    private boolean traceCollections = false;

    public boolean isUiEnabled() {
        return uiEnabled;
    }

    public void setUiEnabled(boolean uiEnabled) {
        this.uiEnabled = uiEnabled;
    }

    public Class<?> getMainClassName() {
        return mainClassName;
    }

    public boolean getTraceCollections() {
        return traceCollections;
    }

    public String getMethodToTrace() {
        return methodToTrace;
    }

    public List<String> getInstrumentedPackages() {
        return instrumentedPackages;
    }

    public TestSettingsBuilder setTraceCollections(boolean traceCollections) {
        this.traceCollections = traceCollections;
        return this;
    }

    public TestSettingsBuilder setInstrumentedPackages(List<String> instrumentedPackages) {
        this.instrumentedPackages = instrumentedPackages;
        return this;
    }

    public TestSettingsBuilder setMethodToTrace(String startMethod) {
        this.methodToTrace = startMethod;
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

    public TestSettingsBuilder setMinTraceCount(int minTraceCount) {
        this.minTraceCount = minTraceCount;
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
                new TracingStartMethodList(new MethodMatcher(mainClassName, methodToTrace)),
                maxDepth,
                maxCallsPerMethod,
                minTraceCount
        );
    }
}
