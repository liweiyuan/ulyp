package com.ulyp.agent.settings;

import com.ulyp.agent.MethodMatcher;
import com.ulyp.agent.transport.DisconnectedTransport;
import com.ulyp.agent.transport.GrpcUploadingTransport;
import com.ulyp.agent.transport.UiAddress;
import com.ulyp.agent.transport.UploadingTransport;
import com.ulyp.core.util.CommaSeparatedList;
import com.ulyp.transport.SettingsResponse;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SystemPropertiesSettings implements AgentSettings {

    public static SystemPropertiesSettings load() {

        List<String> packages = new ArrayList<>(CommaSeparatedList.parse(System.getProperty(PACKAGES_PROPERTY, "")));

        String excludedPackagesStr = System.getProperty(EXCLUDE_PACKAGES_PROPERTY, "");
        List<String> excludedPackages = CommaSeparatedList.parse(excludedPackagesStr);

        String tracedMethods = System.getProperty(START_METHOD_PROPERTY, "");
        TracingStartMethodList tracingStartMethods = new TracingStartMethodList(CommaSeparatedList.parse(tracedMethods));

        UiAddress uiAddress;
        boolean uiEnabled = Boolean.parseBoolean(System.getProperty(UI_ENABLED, "true"));
        if (uiEnabled) {
            // TODO this looks stupid
            String uiHost = System.getProperty(UI_HOST_PROPERTY, GrpcUploadingTransport.DEFAULT_ADDRESS.hostName);
            int uiPort = Integer.parseInt(System.getProperty(UI_PORT_PROPERTY, String.valueOf(GrpcUploadingTransport.DEFAULT_ADDRESS.port)));
            uiAddress = new UiAddress(uiHost, uiPort);
        } else {
            uiAddress = null;
        }

        int maxTreeDepth = Integer.parseInt(System.getProperty(MAX_DEPTH_PROPERTY, String.valueOf(Integer.MAX_VALUE)));
        int maxCallPerMethod = Integer.parseInt(System.getProperty(MAX_CALL_PER_METHOD, String.valueOf(Integer.MAX_VALUE / 2)));
        int minTraceCount = Integer.parseInt(System.getProperty(MIN_TRACE_COUNT, String.valueOf(1)));
        return new SystemPropertiesSettings(
                uiAddress,
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
    public static final String UI_ENABLED = "ulyp.ui-enabled";
    public static final String MAX_DEPTH_PROPERTY = "ulyp.max-depth";
    public static final String MAX_CALL_PER_METHOD = "ulyp.max-calls-per-method";
    public static final String MIN_TRACE_COUNT = "ulyp.min-trace-count";

    @Nullable
    private final UiAddress uiAddress;
    private final List<String> instrumentatedPackages;
    private final List<String> excludedFromInstrumentationPackages;
    private final TracingStartMethodList startTracingMethods;
    private final int maxTreeDepth;
    private final int maxCallsPerMethod;
    private final int minTraceCount;

    public SystemPropertiesSettings(
            @Nullable UiAddress uiAddress,
            List<String> instrumentedPackages,
            List<String> excludedFromInstrumentationPackages,
            TracingStartMethodList tracingStartMethodList,
            int maxTreeDepth,
            int maxCallsPerMethod,
            int minTraceCount)
    {
        this.uiAddress = uiAddress;
        this.instrumentatedPackages = instrumentedPackages;
        this.excludedFromInstrumentationPackages = excludedFromInstrumentationPackages;
        this.startTracingMethods = tracingStartMethodList;
        this.maxTreeDepth = maxTreeDepth;
        this.maxCallsPerMethod = maxCallsPerMethod;
        this.minTraceCount = minTraceCount;
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

    public List<String> getInstrumentatedPackages() {
        return instrumentatedPackages;
    }

    public List<String> getExcludedFromInstrumentationPackages() {
        return excludedFromInstrumentationPackages;
    }

    public List<String> toCmdJavaProps() {
        List<String> params = new ArrayList<>();

        params.add("-D" + PACKAGES_PROPERTY + "=" + String.join(",", instrumentatedPackages));
        if (excludedFromInstrumentationPackages.isEmpty()) {
            params.add("-D" + EXCLUDE_PACKAGES_PROPERTY + "=" + String.join(",", excludedFromInstrumentationPackages));
        }

        params.add("-D" + START_METHOD_PROPERTY + "=" + startTracingMethods.stream().map(MethodMatcher::toString).collect(Collectors.joining()));
        if (uiAddress != null) {

        } else {
            params.add("-D" + UI_PORT_PROPERTY + "=" + uiAddress.port);
        }
        params.add("-D" + MAX_DEPTH_PROPERTY + "=" + maxTreeDepth);
        params.add("-D" + MIN_TRACE_COUNT + "=" + minTraceCount);
        params.add("-D" + MAX_CALL_PER_METHOD + "=" + maxCallsPerMethod);

        return params;
    }

    public UploadingTransport buildUiTransport() {
        if (uiAddress != null) {
            return new GrpcUploadingTransport(uiAddress);
        } else {
            return new DisconnectedTransport(
                    SettingsResponse.newBuilder()
                            .addAllInstrumentedPackages(instrumentatedPackages)
                            .addAllExcludedFromInstrumentationPackages(excludedFromInstrumentationPackages)
                            .addAllTraceStartMethods(startTracingMethods.stream().map(MethodMatcher::toString).collect(Collectors.toList()))
                            .setMayStartTracing(true)
                            .setTraceCollections(false)
                            .build()
            );
        }
    }

    @Override
    public String toString() {
        return "Settings{" +
                "uiAddress=" + uiAddress +
                ", packages=" + instrumentatedPackages +
                ", excludePackages=" + excludedFromInstrumentationPackages +
                ", startTracingMethods=" + startTracingMethods +
                ", maxTreeDepth=" + maxTreeDepth +
                ", maxCallsPerMethod=" + maxCallsPerMethod +
                ", minTraceCount=" + minTraceCount +
                '}';
    }
}
