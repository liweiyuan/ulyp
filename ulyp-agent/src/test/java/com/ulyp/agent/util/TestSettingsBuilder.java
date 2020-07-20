package com.ulyp.agent.util;

import com.ulyp.agent.MethodMatcher;
import com.ulyp.agent.settings.SystemPropertiesSettings;
import com.ulyp.agent.settings.TracingStartMethodList;
import com.ulyp.agent.transport.UiAddress;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collections;

public class TestSettingsBuilder {
    private Class<?> mainClassName;
    private String methodToTrace;
    public String hostName;
    public int port;
    private String packages;
    private String excludedPackages;
    private int minTraceCount = 1;
    private int maxDepth = Integer.MAX_VALUE;
    private int maxCallsPerMethod = Integer.MAX_VALUE;

    private boolean traceCollections = false;

    public Class<?> getMainClassName() {
        return mainClassName;
    }

    public boolean getTraceCollections() {
        return traceCollections;
    }

    public String getMethodToTrace() {
        return methodToTrace;
    }

    public String getPackages() {
        return packages;
    }

    public TestSettingsBuilder setTraceCollections(boolean traceCollections) {
        this.traceCollections = traceCollections;
        return this;
    }

    public TestSettingsBuilder setPackages(String packages) {
        this.packages = packages;
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

    public TestSettingsBuilder setMaxCallsPerMethod(int maxCallsPerMethod) {
        this.maxCallsPerMethod = maxCallsPerMethod;
        return this;
    }

    public TestSettingsBuilder setMainClassName(Class<?> mainClassName) {
        this.mainClassName = mainClassName;
        if (packages == null) {
            packages = mainClassName.getPackage().getName();
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

    public SystemPropertiesSettings build() {
        return new SystemPropertiesSettings(
                new UiAddress(hostName, port),
                Collections.singletonList(packages),
                StringUtils.isEmpty(excludedPackages) ? Collections.singletonList(excludedPackages) : Collections.emptyList(),
                new TracingStartMethodList(new MethodMatcher(mainClassName, methodToTrace)),
                maxDepth,
                maxCallsPerMethod,
                minTraceCount
        );
    }
}
