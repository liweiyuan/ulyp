package com.ulyp.agent.settings;

import com.ulyp.core.util.CommaSeparatedList;
import com.ulyp.core.util.PackageList;
import com.ulyp.database.Database;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SystemPropertiesSettings {

    public static SystemPropertiesSettings load() {

        PackageList instrumentationPackages = new PackageList(CommaSeparatedList.parse(System.getProperty(PACKAGES_PROPERTY, "")));
        PackageList excludedPackages = new PackageList(CommaSeparatedList.parse(System.getProperty(EXCLUDE_PACKAGES_PROPERTY, "")));

        String methodsToRecord = System.getProperty(START_METHOD_PROPERTY, "");
        RecordingStartMethodList recordingStartMethods = new RecordingStartMethodList(CommaSeparatedList.parse(methodsToRecord));

        Path outputFilePath;
        String file = System.getProperty(FILE_PATH);
        if (file != null) {
            outputFilePath = Paths.get(file);
        } else {
            throw new RuntimeException("Property " + FILE_PATH + " must be set");
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
                outputFilePath,
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
    // TODO name
    public static final String START_METHOD_PROPERTY = "ulyp.methods";
    public static final String FILE_PATH = "ulyp.file";
    public static final String MAX_DEPTH_PROPERTY = "ulyp.max-depth";
    public static final String MAX_CALL_TO_RECORD_PER_METHOD = "ulyp.max-recorded-calls-per-method";
    public static final String MIN_TRACE_COUNT = "ulyp.min-trace-count";

    private final Path outputFilePath;
    private final PackageList instrumentatedPackages;
    private final PackageList excludedFromInstrumentationPackages;
    @NotNull private final RecordingStartMethodList methodsToRecord;
    private final int maxTreeDepth;
    private final int maxCallsToRecordPerMethod;
    private final int minRecordsCountForLog;

    public SystemPropertiesSettings(
            Path outputFilePath,
            PackageList instrumentedPackages,
            PackageList excludedFromInstrumentationPackages,
            @NotNull RecordingStartMethodList methodsToRecord,
            int maxTreeDepth,
            int maxCallsToRecordPerMethod,
            int minRecordsCountForLog)
    {
        this.outputFilePath = outputFilePath;
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

    public RecordingStartMethodList getMethodsToRecord() {
        return methodsToRecord;
    }

    public Database.Writer buildDbWriter() throws IOException {
        return Database.openForWrite(outputFilePath);
    }

    @Override
    public String toString() {
        return "Settings{" +
                "outputFilePath=" + outputFilePath +
                ", packages=" + instrumentatedPackages +
                ", excludePackages=" + excludedFromInstrumentationPackages +
                ", startRecordingMethods=" + methodsToRecord +
                ", maxTreeDepth=" + maxTreeDepth +
                ", maxCallsPerMethod=" + maxCallsToRecordPerMethod +
                ", minTraceCount=" + minRecordsCountForLog +
                '}';
    }
}
