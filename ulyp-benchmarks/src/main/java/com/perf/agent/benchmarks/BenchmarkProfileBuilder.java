package com.perf.agent.benchmarks;

import com.ulyp.core.util.MethodMatcher;
import com.ulyp.core.util.PackageList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BenchmarkProfileBuilder {

    private MethodMatcher methodToRecord;
    @NotNull
    private PackageList instrumentedPackages = new PackageList();
    private boolean uiEnabled = true;
    private final List<String> additionalProcessArgs = new ArrayList<>();

    public BenchmarkProfileBuilder withAdditionalArgs(String... args) {
        additionalProcessArgs.addAll(Arrays.asList(args));
        return this;
    }

    public BenchmarkProfileBuilder withMethodToRecord(MethodMatcher methodToRecord) {
        this.methodToRecord = methodToRecord;
        return this;
    }

    public BenchmarkProfileBuilder withInstrumentedPackages(@NotNull PackageList instrumentedPackages) {
        this.instrumentedPackages = instrumentedPackages;
        return this;
    }

    public BenchmarkProfileBuilder withUiDisabled() {
        return setUiEnabled(false);
    }

    private BenchmarkProfileBuilder setUiEnabled(boolean uiEnabled) {
        this.uiEnabled = uiEnabled;
        return this;
    }

    public BenchmarkProfile build() {
        return new BenchmarkProfile(methodToRecord, instrumentedPackages, additionalProcessArgs, uiEnabled);
    }
}
