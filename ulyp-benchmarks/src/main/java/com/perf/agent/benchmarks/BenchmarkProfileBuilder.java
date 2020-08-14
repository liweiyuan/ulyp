package com.perf.agent.benchmarks;

import com.perf.agent.benchmarks.proc.BenchmarkEnv;
import com.ulyp.core.util.MethodMatcher;
import com.ulyp.core.util.PackageList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BenchmarkProfileBuilder {

    private MethodMatcher tracedMethod;
    @NotNull
    private PackageList instrumentedPackages = new PackageList();
    private boolean uiEnabled = true;
    private final int uiPort = BenchmarkEnv.pickFreePort();
    private final List<String> additionalProcessArgs = new ArrayList<>();

    public BenchmarkProfileBuilder withAdditionalArgs(String... args) {
        additionalProcessArgs.addAll(Arrays.asList(args));
        return this;
    }

    public BenchmarkProfileBuilder withTracedMethod(MethodMatcher tracedMethod) {
        this.tracedMethod = tracedMethod;
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
        return new BenchmarkProfile(tracedMethod, instrumentedPackages, additionalProcessArgs, uiEnabled, uiPort);
    }
}