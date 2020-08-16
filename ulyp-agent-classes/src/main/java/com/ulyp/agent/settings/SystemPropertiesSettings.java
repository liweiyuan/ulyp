package com.ulyp.agent.settings;

import com.ulyp.core.util.MethodMatcher;
import com.ulyp.agent.transport.DisconnectedUiTransport;
import com.ulyp.agent.transport.GrpcUiTransport;
import com.ulyp.agent.transport.UiAddress;
import com.ulyp.agent.transport.UiTransport;
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

        String methodsToRecord = System.getProperty(START_METHOD_PROPERTY, "");
        RecordingStartMethodList tracingStartMethods = new RecordingStartMethodList(CommaSeparatedList.parse(methodsToRecord));

        UiAddress uiAddress;
        boolean uiEnabled = Boolean.parseBoolean(System.getProperty(UI_ENABLED, "true"));
        if (uiEnabled) {
            // TODO this looks stupid
            String uiHost = System.getProperty(UI_HOST_PROPERTY, GrpcUiTransport.DEFAULT_ADDRESS.hostName);
            int uiPort = Integer.parseInt(System.getProperty(UI_PORT_PROPERTY, String.valueOf(GrpcUiTransport.DEFAULT_ADDRESS.port)));
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
    // TODO use package list
    private final List<String> instrumentatedPackages;
    private final List<String> excludedFromInstrumentationPackages;
    private final RecordingStartMethodList methodsToRecord;
    private final int maxTreeDepth;
    private final int maxCallsPerMethod;
    private final int minRecordsCountForLog;

    public SystemPropertiesSettings(
            @Nullable UiAddress uiAddress,
            List<String> instrumentedPackages,
            List<String> excludedFromInstrumentationPackages,
            RecordingStartMethodList methodsToRecord,
            int maxTreeDepth,
            int maxCallsPerMethod,
            int minRecordsCountForLog)
    {
        this.uiAddress = uiAddress;
        this.instrumentatedPackages = instrumentedPackages;
        this.excludedFromInstrumentationPackages = excludedFromInstrumentationPackages;
        this.methodsToRecord = methodsToRecord;
        this.maxTreeDepth = maxTreeDepth;
        this.maxCallsPerMethod = maxCallsPerMethod;
        this.minRecordsCountForLog = minRecordsCountForLog;
    }

    public int getMaxTreeDepth() {
        return maxTreeDepth;
    }

    public int getMinRecordsCountForLog() {
        return minRecordsCountForLog;
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

        params.add("-D" + START_METHOD_PROPERTY + "=" + methodsToRecord.stream().map(MethodMatcher::toString).collect(Collectors.joining()));
        if (uiAddress != null) {

        } else {
            params.add("-D" + UI_PORT_PROPERTY + "=" + uiAddress.port);
        }
        params.add("-D" + MAX_DEPTH_PROPERTY + "=" + maxTreeDepth);
        params.add("-D" + MIN_TRACE_COUNT + "=" + minRecordsCountForLog);
        params.add("-D" + MAX_CALL_PER_METHOD + "=" + maxCallsPerMethod);

        return params;
    }

    public UiTransport buildUiTransport() {
        if (uiAddress != null) {
            return new GrpcUiTransport(uiAddress);
        } else {
            return new DisconnectedUiTransport(
                    SettingsResponse.newBuilder()
                            .addAllInstrumentedPackages(getInstrumentatedPackages())
                            .addAllExcludedFromInstrumentationPackages(getExcludedFromInstrumentationPackages())
                            .addAllMethodsToRecord(methodsToRecord.stream().map(MethodMatcher::toString).collect(Collectors.toList()))
                            .setMayStartRecording(true)
                            .setRecordCollectionsItems(false)
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
                ", startTracingMethods=" + methodsToRecord +
                ", maxTreeDepth=" + maxTreeDepth +
                ", maxCallsPerMethod=" + maxCallsPerMethod +
                ", minTraceCount=" + minRecordsCountForLog +
                '}';
    }
}
