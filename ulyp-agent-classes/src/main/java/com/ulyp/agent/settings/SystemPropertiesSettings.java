package com.ulyp.agent.settings;

import com.ulyp.agent.MethodMatcher;
import com.ulyp.agent.transport.UiAddress;
import com.ulyp.agent.transport.UploadingTransport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SystemPropertiesSettings implements AgentSettings {

    public static SystemPropertiesSettings loadFromSystemProperties() {

        String packagesToInstrument = System.getProperty(PACKAGES_PROPERTY);
        List<String> packages;
        if (packagesToInstrument == null) {
            packages = Collections.emptyList();
        } else {
            packages = new ArrayList<>(Arrays.asList(packagesToInstrument.split(",")));
        }

        String excludedPackagesStr = System.getProperty(EXCLUDE_PACKAGES_PROPERTY);
        List<String> excludedPackages;

        // TODO remove that if
        if (excludedPackagesStr != null) {
            excludedPackages = new ArrayList<>(Arrays.asList(excludedPackagesStr.split(",")));
        } else {
            excludedPackages = Collections.emptyList();
        }

        String tracedMethods = System.getProperty(START_METHOD_PROPERTY);
        TracingStartMethodList tracingStartMethods;

        // TODO remove that if
        if (tracedMethods != null) {
            tracingStartMethods = new TracingStartMethodList(Arrays.stream(tracedMethods.split(",")).collect(Collectors.toList()));
        } else {
            tracingStartMethods = new TracingStartMethodList(Collections.emptyList());
        }

        String uiHost = System.getProperty(UI_HOST_PROPERTY, UploadingTransport.DEFAULT_ADDRESS.hostName);
        int uiPort = Integer.parseInt(System.getProperty(UI_PORT_PROPERTY, String.valueOf(UploadingTransport.DEFAULT_ADDRESS.port)));

        int maxTreeDepth = Integer.parseInt(System.getProperty(MAX_DEPTH_PROPERTY, String.valueOf(Integer.MAX_VALUE)));
        int maxCallPerMethod = Integer.parseInt(System.getProperty(MAX_CALL_PER_METHOD, String.valueOf(Integer.MAX_VALUE / 2)));
        int minTraceCount = Integer.parseInt(System.getProperty(MIN_TRACE_COUNT, String.valueOf(1)));
        return new SystemPropertiesSettings(
                new UiAddress(uiHost, uiPort),
                packages,
                excludedPackages,
                tracingStartMethods,
                maxTreeDepth,
                maxCallPerMethod,
                minTraceCount
        );
    }

    public static final String PACKAGES_PROPERTY = "ulyp.packages";
    public static final String EXCLUDE_PACKAGES_PROPERTY = "ulyp.exclude-packages";
    public static final String START_METHOD_PROPERTY = "ulyp.start-method";
    public static final String UI_HOST_PROPERTY = "ulyp.ui-host";
    public static final String UI_PORT_PROPERTY = "ulyp.ui-port";
    public static final String MAX_DEPTH_PROPERTY = "ulyp.max-depth";
    public static final String MAX_CALL_PER_METHOD = "ulyp.max-calls-per-method";
    public static final String MIN_TRACE_COUNT = "ulyp.min-trace-count";

    private UiAddress uiAddress;
    private final List<String> packages;
    private final List<String> excludePackages;
    private final TracingStartMethodList startTracingMethods;
    private final int maxTreeDepth;
    private final int maxCallsPerMethod;
    private final int minTraceCount;

    public SystemPropertiesSettings(
            UiAddress uiAddress,
            List<String> packages,
            List<String> excludePackages,
            TracingStartMethodList tracingStartMethodList,
            int maxTreeDepth,
            int maxCallsPerMethod,
            int minTraceCount)
    {
        this.uiAddress = uiAddress;
        this.packages = packages;
        this.excludePackages = excludePackages;
        this.startTracingMethods = tracingStartMethodList;
        this.maxTreeDepth = maxTreeDepth;
        this.maxCallsPerMethod = maxCallsPerMethod;
        this.minTraceCount = minTraceCount;
    }


    public UiAddress getUiAddress() {
        return uiAddress;
    }

    public int getMaxTreeDepth() {
        return maxTreeDepth;
    }

    public int getMinTraceCount() {
        return minTraceCount;
    }

    public int getMaxCallsPerMethod() {
        return maxCallsPerMethod;
    }

    public List<String> getPackages() {
        return packages;
    }

    public List<String> getExcludePackages() {
        return excludePackages;
    }

    public List<String> toCmdJavaProps() {
        List<String> params = new ArrayList<>();

        params.add("-D" + PACKAGES_PROPERTY + "=" + String.join(",", packages));
        if (excludePackages.isEmpty()) {
            params.add("-D" + EXCLUDE_PACKAGES_PROPERTY + "=" + String.join(",", excludePackages));
        }

        params.add("-D" + START_METHOD_PROPERTY + "=" + startTracingMethods.stream().map(MethodMatcher::toString).collect(Collectors.joining()));
        params.add("-D" + UI_PORT_PROPERTY + "=" + uiAddress.port);
        params.add("-D" + MAX_DEPTH_PROPERTY + "=" + maxTreeDepth);
        params.add("-D" + MIN_TRACE_COUNT + "=" + minTraceCount);
        params.add("-D" + MAX_CALL_PER_METHOD + "=" + maxCallsPerMethod);

        return params;
    }

    @Override
    public String toString() {
        return "Settings{" +
                "uiAddress=" + uiAddress +
                ", packages=" + packages +
                ", excludePackages=" + excludePackages +
                ", startTracingMethods=" + startTracingMethods +
                ", maxTreeDepth=" + maxTreeDepth +
                ", maxCallsPerMethod=" + maxCallsPerMethod +
                ", minTraceCount=" + minTraceCount +
                '}';
    }
}
