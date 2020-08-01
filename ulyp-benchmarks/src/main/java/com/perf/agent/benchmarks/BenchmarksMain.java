package com.perf.agent.benchmarks;

import com.perf.agent.benchmarks.proc.BenchmarkProcessRunner;
import com.perf.agent.benchmarks.proc.UIServerStub;
import com.ulyp.transport.TCallTraceLogUploadRequest;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class BenchmarksMain {

    public static int pickEmptyPort() {
        // TODO implement
        return 10000 + ThreadLocalRandom.current().nextInt(1000);
    }

    public static void main(String[] args) {

        Class<?> clazz = H2MemDatabaseBenchmark.class;
        BenchmarkSettings settings = new BenchmarkSettings();
        settings.setClassToTrace(clazz);
        settings.setMethodToTrace("main");
        settings.setUiListenPort(pickEmptyPort());
        settings.setTracedPackages(new H2MemDatabaseBenchmark().getPackagesToTrace());

        try (UIServerStub uiServerStub = new UIServerStub(settings)) {
            BenchmarkProcessRunner.runClassInSeparateJavaProcess(settings);

            TCallTraceLogUploadRequest tCallTraceLogUploadRequest = uiServerStub.get(5, TimeUnit.MINUTES);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
