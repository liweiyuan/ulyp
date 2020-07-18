package com.ulyp.agent.util;

import com.ulyp.agent.MethodMatcher;
import com.ulyp.agent.settings.AgentSettings;
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

    public AgentSettings build() {
        MethodMatcher methodMatcher = new MethodMatcher(mainClassName, methodToTrace);

        return new AgentSettings(
                new UiAddress(hostName, port),
                Arrays.asList(packages),
                StringUtils.isEmpty(excludedPackages) ? Arrays.asList(excludedPackages) : Collections.emptyList(),
                Arrays.asList(methodMatcher),
                maxDepth,
                maxCallsPerMethod,
                minTraceCount
        );
    }
}
