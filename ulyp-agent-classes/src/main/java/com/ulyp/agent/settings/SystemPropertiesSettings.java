package com.ulyp.agent.settings;

import com.ulyp.agent.transport.UiAddress;
import com.ulyp.agent.transport.UiTransport;
import com.ulyp.agent.transport.file.FileUiAddress;
import com.ulyp.core.util.CommaSeparatedList;
import com.ulyp.core.util.PackageList;
import org.jetbrains.annotations.NotNull;

public class SystemPropertiesSettings {

    public static SystemPropertiesSettings load() {

        PackageList instrumentationPackages = new PackageList(CommaSeparatedList.parse(System.getProperty(PACKAGES_PROPERTY, "")));
        PackageList excludedPackages = new PackageList(CommaSeparatedList.parse(System.getProperty(EXCLUDE_PACKAGES_PROPERTY, "")));

        String methodsToRecord = System.getProperty(START_METHOD_PROPERTY, "");
        RecordingStartMethodList recordingStartMethods = new RecordingStartMethodList(CommaSeparatedList.parse(methodsToRecord));

        UiAddress uiAddress;
        String file = System.getProperty(FILE_PATH);
        if (file != null) {
            uiAddress = new FileUiAddress(file);
        } else {
            throw new RuntimeException("Property " + FILE_PATH + " must be set");
        }

        boolean shouldRecordConstructors = false;
        if (System.getProperty(RECORD_CONSTRUCTORS) != null) {
            shouldRecordConstructors = true;
        }

        /*
        if (true) {

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
        */

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
                minRecordsCount,
                shouldRecordConstructors
        );
    }

    public static final String PACKAGES_PROPERTY = "ulyp.packages";
    public static final String EXCLUDE_PACKAGES_PROPERTY = "ulyp.exclude-packages";
    // TODO name
    public static final String START_METHOD_PROPERTY = "ulyp.methods";
    public static final String FILE_PATH = "ulyp.file";
    public static final String RECORD_CONSTRUCTORS = "ulyp.constructors";
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
    private final boolean shouldRecordConstructors;

    public SystemPropertiesSettings(
            @NotNull UiAddress uiAddress,
            PackageList instrumentedPackages,
            PackageList excludedFromInstrumentationPackages,
            @NotNull RecordingStartMethodList methodsToRecord,
            int maxTreeDepth,
            int maxCallsToRecordPerMethod,
            int minRecordsCountForLog,
            boolean shouldRecordConstructors)
    {
        this.uiAddress = uiAddress;
        this.instrumentatedPackages = instrumentedPackages;
        this.excludedFromInstrumentationPackages = excludedFromInstrumentationPackages;
        this.methodsToRecord = methodsToRecord;
        this.maxTreeDepth = maxTreeDepth;
        this.maxCallsToRecordPerMethod = maxCallsToRecordPerMethod;
        this.minRecordsCountForLog = minRecordsCountForLog;
        this.shouldRecordConstructors = shouldRecordConstructors;
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

    public RecordingStartMethodList getMethodsToRecord() {
        return methodsToRecord;
    }

    public UiTransport buildUiTransport() {
        return uiAddress.buildTransport();
    }

    public boolean shouldRecordConstructors() {
        return shouldRecordConstructors;
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
