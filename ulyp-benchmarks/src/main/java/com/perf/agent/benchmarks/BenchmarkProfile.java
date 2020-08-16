package com.perf.agent.benchmarks;

import com.perf.agent.benchmarks.proc.BenchmarkEnv;
import com.ulyp.core.util.MethodMatcher;
import com.ulyp.core.util.PackageList;
import com.ulyp.transport.SettingsResponse;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BenchmarkProfile {

    @Nullable
    private final MethodMatcher methodToRecord;
    @NotNull
    private final PackageList instrumentedPackages;
    private final List<String> additionalProcessArgs;
    private final boolean uiEnabled;
    private final int uiListenPort;

    public BenchmarkProfile(
            @Nullable MethodMatcher methodToRecord,
            @NotNull PackageList instrumentedPackages,
            List<String> additionalProcessArgs,
            boolean uiEnabled,
            int uiListenPort) {
        this.methodToRecord = methodToRecord;
        this.instrumentedPackages = instrumentedPackages;
        this.additionalProcessArgs = additionalProcessArgs;
        this.uiEnabled = uiEnabled;
        this.uiListenPort = uiListenPort;
    }

    public boolean shouldSendSomethingToUi() {
        return uiEnabled && !instrumentedPackages.isEmpty() && methodToRecord != null;
    }

    public int getUiListenPort() {
        return uiListenPort;
    }

    /**
     * Only will be called if this has {@link BenchmarkProfile#uiEnabled} set to true
     * @return settings to send to subprocess back as a response to settings request
     */
    public SettingsResponse getSettingsFromUi() {
        SettingsResponse.Builder builder = SettingsResponse
                .newBuilder()
                .setMayStartRecording(true)
                .setRecordCollectionsItems(false)
                .addAllInstrumentedPackages(instrumentedPackages);

        if (methodToRecord != null) {
            builder = builder.addTraceStartMethods(methodToRecord.toString());
        }

        return builder.build();
    }

    public List<String> getSubprocessCmdArgs() {
        List<String> args = new ArrayList<>();
        if (!instrumentedPackages.isEmpty()) {
            args.add("-javaagent:" + BenchmarkEnv.findBuiltAgentJar());
        }
        if (uiEnabled) {
            args.add("-Dulyp.ui-host=localhost");
            args.add("-Dulyp.ui-port=" + uiListenPort);
        } else {
            args.add("-Dulyp.ui-enabled=false");
            args.add("-Dulyp.start-method=" + Objects.requireNonNull(this.methodToRecord));
            args.add("-Dulyp.packages=" + this.instrumentedPackages);
        }
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
