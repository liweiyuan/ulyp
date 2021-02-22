package com.perf.agent.benchmarks;

import com.perf.agent.benchmarks.proc.BenchmarkEnv;
import com.perf.agent.benchmarks.proc.OutputFile;
import com.ulyp.core.util.MethodMatcher;
import com.ulyp.core.util.PackageList;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BenchmarkProfile {

    @Nullable
    private final MethodMatcher methodToRecord;
    private final OutputFile outputFile = new OutputFile("test", ".dat");
    @NotNull
    private final PackageList instrumentedPackages;
    private final List<String> additionalProcessArgs;
    private final boolean uiEnabled;

    public BenchmarkProfile(
            @Nullable MethodMatcher methodToRecord,
            @NotNull PackageList instrumentedPackages,
            List<String> additionalProcessArgs,
            boolean uiEnabled) {
        this.methodToRecord = methodToRecord;
        this.instrumentedPackages = instrumentedPackages;
        this.additionalProcessArgs = additionalProcessArgs;
        this.uiEnabled = uiEnabled;
    }

    public boolean shouldSendSomethingToUi() {
        return !instrumentedPackages.isEmpty() && methodToRecord != null;
    }

    public OutputFile getOutputFile() {
        return outputFile;
    }

    public List<String> getSubprocessCmdArgs() {
        List<String> args = new ArrayList<>();

        if (!instrumentedPackages.isEmpty()) {
            args.add("-javaagent:" + BenchmarkEnv.findBuiltAgentJar());
        }

        if (uiEnabled) {

        } else {
//            args.add("-Dulyp.ui-enabled=false");
        }

        args.add("-Dulyp.file=" + outputFile);
        args.add("-Dulyp.methods=" + Objects.requireNonNull(this.methodToRecord));
        args.add("-Dulyp.packages=" + this.instrumentedPackages);
        args.addAll(additionalProcessArgs);

        return args;
    }

    @Override
    public String toString() {
        if (!instrumentedPackages.isEmpty()) {
            return instrumentedPackages + "/" + (methodToRecord != null ? methodToRecord : "no tracing");
        } else {
            return "no agent";
        }
    }
}
