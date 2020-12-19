package com.ulyp.agent.settings;

import com.ulyp.agent.transport.*;
import com.ulyp.agent.transport.file.FileUiAddress;
import com.ulyp.agent.transport.grpc.GrpcUiAddress;
import com.ulyp.agent.transport.grpc.GrpcUiTransport;
import com.ulyp.agent.transport.nop.DisconnectedUiAddress;
import com.ulyp.core.util.CommaSeparatedList;
import com.ulyp.core.util.MethodMatcher;
import com.ulyp.core.util.PackageList;
import com.ulyp.transport.Settings;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SystemPropertiesSettings implements AgentSettings {

    public static SystemPropertiesSettings load() {

        PackageList instrumentationPackages = new PackageList(CommaSeparatedList.parse(System.getProperty(PACKAGES_PROPERTY, "")));
        PackageList excludedPackages = new PackageList(CommaSeparatedList.parse(System.getProperty(EXCLUDE_PACKAGES_PROPERTY, "")));

        String methodsToRecord = System.getProperty(START_METHOD_PROPERTY, "");
        RecordingStartMethodList recordingStartMethods = new RecordingStartMethodList(CommaSeparatedList.parse(methodsToRecord));

        UiAddress uiAddress;
        boolean uiEnabled = Boolean.parseBoolean(System.getProperty(UI_ENABLED, "true"));
        if (uiEnabled) {
            // TODO make url
            String file = System.getProperty("ulyp.file");
            if (file != null) {
                uiAddress = new FileUiAddress(
                        Settings.newBuilder()
                                .addAllInstrumentedPackages(instrumentationPackages)
                                .addAllExcludedFromInstrumentationPackages(excludedPackages)
                                .addAllMethodsToRecord(recordingStartMethods.stream().map(MethodMatcher::toString).collect(Collectors.toList()))
                                .setMayStartRecording(true)
                                .setRecordCollectionsItems(false)
                                .build(),
                        file
                );
            } else {
                String uiHost = System.getProperty(UI_HOST_PROPERTY, GrpcUiTransport.DEFAULT_ADDRESS.hostName);
                int uiPort = Integer.parseInt(System.getProperty(UI_PORT_PROPERTY, String.valueOf(GrpcUiTransport.DEFAULT_ADDRESS.port)));
                uiAddress = new GrpcUiAddress(uiHost, uiPort);
            }
        } else {
            uiAddress = new DisconnectedUiAddress(
                    Settings.newBuilder()
                            .addAllInstrumentedPackages(instrumentationPackages)
                            .addAllExcludedFromInstrumentationPackages(excludedPackages)
                            .addAllMethodsToRecord(recordingStartMethods.stream().map(MethodMatcher::toString).collect(Collectors.toList()))
                            .setMayStartRecording(true)
                            .setRecordCollectionsItems(false)
                            .build()
            );
        }

        int maxTreeDepth = Integer.parseInt(System.getProperty(MAX_DEPTH_PROPERTY, String.valueOf(Integer.MAX_VALUE)));
        int maxRecordedMethodCallsPerMethod = Integer.parseInt(System.getProperty(MAX_CALL_TO_RECORD_PER_METHOD, String.valueOf(Integer.MAX_VALUE / 2)));
        int minRecordsCount = Integer.parseInt(System.getProperty(MIN_TRACE_COUNT, String.valueOf(1)));
        return new SystemPropertiesSettings(
                uiAddress,
                instrumentationPackages,
                excludedPackages,
                recordingStartMethods,
                maxTreeDepth,
                maxRecordedMethodCallsPerMethod,
                minRecordsCount
        );
    }

    public static final String PACKAGES_PROPERTY = "ulyp.packages";
    public static final String EXCLUDE_PACKAGES_PROPERTY = "ulyp.exclude-packages";
    public static final String START_METHOD_PROPERTY = "ulyp.methods";
    public static final String UI_HOST_PROPERTY = "ulyp.ui-host";
    public static final String UI_PORT_PROPERTY = "ulyp.ui-port";
    public static final String UI_ENABLED = "ulyp.ui-enabled";
    public static final String MAX_DEPTH_PROPERTY = "ulyp.max-depth";
    public static final String MAX_CALL_TO_RECORD_PER_METHOD = "ulyp.max-recorded-calls-per-method";
    public static final String MIN_TRACE_COUNT = "ulyp.min-trace-count";

    @NotNull private final UiAddress uiAddress;
    private final PackageList instrumentatedPackages;
    private final PackageList excludedFromInstrumentationPackages;
    @NotNull private final RecordingStartMethodList methodsToRecord;
    private final int maxTreeDepth;
    private final int maxCallsToRecordPerMethod;
    private final int minRecordsCountForLog;

    public SystemPropertiesSettings(
            @NotNull UiAddress uiAddress,
            PackageList instrumentedPackages,
            PackageList excludedFromInstrumentationPackages,
            @NotNull RecordingStartMethodList methodsToRecord,
            int maxTreeDepth,
            int maxCallsToRecordPerMethod,
            int minRecordsCountForLog)
    {
        this.uiAddress = uiAddress;
        this.instrumentatedPackages = instrumentedPackages;
        this.excludedFromInstrumentationPackages = excludedFromInstrumentationPackages;
        this.methodsToRecord = methodsToRecord;
        this.maxTreeDepth = maxTreeDepth;
        this.maxCallsToRecordPerMethod = maxCallsToRecordPerMethod;
        this.minRecordsCountForLog = minRecordsCountForLog;
    }

    public int getMaxTreeDepth() {
        return maxTreeDepth;
    }

    public int getMinRecordsCountForLog() {
        return minRecordsCountForLog;
    }

    public int getMaxCallsToRecordPerMethod() {
        return maxCallsToRecordPerMethod;
    }

    public PackageList getInstrumentatedPackages() {
        return instrumentatedPackages;
    }

    public PackageList getExcludedFromInstrumentationPackages() {
        return excludedFromInstrumentationPackages;
    }

    // TODO unused?
    public List<String> toCmdJavaProps() {
        List<String> params = new ArrayList<>();

        params.add("-D" + PACKAGES_PROPERTY + "=" + String.join(",", instrumentatedPackages));
        if (excludedFromInstrumentationPackages.isEmpty()) {
            params.add("-D" + EXCLUDE_PACKAGES_PROPERTY + "=" + String.join(",", excludedFromInstrumentationPackages));
        }

        params.add("-D" + START_METHOD_PROPERTY + "=" + methodsToRecord.stream().map(MethodMatcher::toString).collect(Collectors.joining()));
//        if (uiAddress != null) {
//
//        } else {
//            params.add("-D" + UI_PORT_PROPERTY + "=" + uiAddress.port);
//        }
        params.add("-D" + MAX_DEPTH_PROPERTY + "=" + maxTreeDepth);
        params.add("-D" + MIN_TRACE_COUNT + "=" + minRecordsCountForLog);
        params.add("-D" + MAX_CALL_TO_RECORD_PER_METHOD + "=" + maxCallsToRecordPerMethod);

        return params;
    }

    public UiTransport buildUiTransport() {
        return uiAddress.buildTransport();
    }

    @Override
    public String toString() {
        return "Settings{" +
                "uiAddress=" + uiAddress +
                ", packages=" + instrumentatedPackages +
                ", excludePackages=" + excludedFromInstrumentationPackages +
                ", startRecordingMethods=" + methodsToRecord +
                ", maxTreeDepth=" + maxTreeDepth +
                ", maxCallsPerMethod=" + maxCallsToRecordPerMethod +
                ", minTraceCount=" + minRecordsCountForLog +
                '}';
    }
}
