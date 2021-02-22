package com.test.cases.util;

import com.ulyp.agent.settings.SystemPropertiesSettings;
import com.ulyp.core.util.MethodMatcher;
import com.ulyp.core.util.PackageList;

import java.util.ArrayList;
import java.util.List;

public class TestSettingsBuilder {

    private Class<?> mainClassName;
    private MethodMatcher methodToRecord;
    private OutputFile outputFile = new OutputFile("test", ".dat");
    private PackageList instrumentedPackages = new PackageList();
    private PackageList excludedFromInstrumentationPackages = new PackageList();
    private int minRecordsForLog = 1;
    private int maxDepth = Integer.MAX_VALUE;
    private int maxCallsPerMethod = Integer.MAX_VALUE;

    private boolean recordCollectionItems = false;

    public Class<?> getMainClassName() {
        return mainClassName;
    }

    public TestSettingsBuilder setMainClassName(Class<?> mainClassName) {
        this.mainClassName = mainClassName;
        if (instrumentedPackages.isEmpty()) {
            instrumentedPackages = new PackageList(mainClassName.getPackage().getName());
        }
        return this;
    }

    public boolean getRecordCollectionItems() {
        return recordCollectionItems;
    }

    public TestSettingsBuilder setRecordCollectionItems(boolean recordCollectionItems) {
        this.recordCollectionItems = recordCollectionItems;
        return this;
    }

    public PackageList getInstrumentedPackages() {
        return instrumentedPackages;
    }

    public TestSettingsBuilder setInstrumentedPackages(String... packages) {
        this.instrumentedPackages = new PackageList(packages);
        return this;
    }

    public MethodMatcher getMethodToRecord() {
        return methodToRecord;
    }

    public TestSettingsBuilder setMethodToRecord(MethodMatcher methodToRecord) {
        this.methodToRecord = methodToRecord;
        return this;
    }

    public TestSettingsBuilder setMethodToRecord(String startMethod) {
        if (mainClassName != null) {
            this.methodToRecord = new MethodMatcher(mainClassName, startMethod);
        } else {
            throw new IllegalArgumentException("Please set main class name first");
        }
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

    public TestSettingsBuilder setMinRecordsForLog(int minRecordsForLog) {
        this.minRecordsForLog = minRecordsForLog;
        return this;
    }

    public OutputFile getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(OutputFile outputFile) {
        this.outputFile = outputFile;
    }

    public PackageList getExcludedFromInstrumentationPackages() {
        return excludedFromInstrumentationPackages;
    }

    public TestSettingsBuilder setExcludedFromInstrumentationPackages(String... packages) {
        this.excludedFromInstrumentationPackages = new PackageList(packages);
        return this;
    }

    public List<String> toCmdJavaProps() {
        List<String> params = new ArrayList<>();

        params.add("-D" + SystemPropertiesSettings.PACKAGES_PROPERTY + "=" + String.join(",", instrumentedPackages));
        if (excludedFromInstrumentationPackages.isEmpty()) {
            params.add("-D" + SystemPropertiesSettings.EXCLUDE_PACKAGES_PROPERTY + "=" + String.join(",", excludedFromInstrumentationPackages));
        }

        params.add("-D" + SystemPropertiesSettings.START_METHOD_PROPERTY + "=" + methodToRecord.toString());
        params.add("-D" + SystemPropertiesSettings.FILE_PATH + "=" + outputFile);
//        params.add("-D" + SystemPropertiesSettings.MAX_DEPTH_PROPERTY + "=" + maxTreeDepth);
//        params.add("-D" + SystemPropertiesSettings.MIN_TRACE_COUNT + "=" + minRecordsCountForLog);
//        params.add("-D" + SystemPropertiesSettings.MAX_CALL_TO_RECORD_PER_METHOD + "=" + maxCallsToRecordPerMethod);

        return params;
    }
}
