package com.perf.agent.benchmarks;

import com.perf.agent.benchmarks.impl.H2MemDatabaseBenchmark;
import com.perf.agent.benchmarks.impl.SpringHibernateBenchmark;
import com.perf.agent.benchmarks.proc.BenchmarkProcessRunner;
import com.perf.agent.benchmarks.proc.UIServerStub;
import com.ulyp.core.CallEnterTraceList;
import com.ulyp.transport.TCallTraceLogUploadRequest;
import org.HdrHistogram.Histogram;
import org.h2.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class BenchmarksMain {

    public static int pickEmptyPort() {
        // TODO implement
        return 10000 + ThreadLocalRandom.current().nextInt(1000);
    }

    public static void main(String[] args) throws Exception {

        List<RunResult> runResults = new ArrayList<>();

        runResults.addAll(runBench(H2MemDatabaseBenchmark.class));
        runResults.addAll(runBench(SpringHibernateBenchmark.class));

        for (RunResult runResult : runResults) {
            runResult.print();
        }
    }

    private static List<RunResult> runBench(Class<? extends Benchmark> benchmarkClazz) throws Exception {
        List<RunResult> runResults = new ArrayList<>();

        Benchmark benchmark = benchmarkClazz.newInstance();

        BenchmarkSettings settings = new BenchmarkSettings();
        settings.setClassToTrace(benchmarkClazz);
        settings.setMethodToTrace("main");
        settings.setUiListenPort(pickEmptyPort());
        settings.setMainClass(benchmarkClazz);

        settings.setTracedPackages(Collections.emptyList());

        // 'warmup' ui stubs
        for (int i = 0; i < 2; i++) {
            run(settings, emptyHistogram(), emptyHistogram());
        }

        for (BenchmarkProfile profile : benchmark.getProfiles()) {
            Histogram procTimeHistogram = emptyHistogram();
            Histogram traceTimeHistogram = emptyHistogram();
            Histogram traceCountHistogram = emptyHistogram();

            settings.setClassToTrace(profile.getTracedClass());
            settings.setMethodToTrace(profile.getTracedMethod());
            settings.setTracedPackages(profile.getInstrumentedPackages());

            for (int i = 0; i < 10; i++) {
                int tracesCount = run(settings, procTimeHistogram, traceTimeHistogram);
                traceCountHistogram.recordValue(tracesCount);
            }

            runResults.add(new RunResult(benchmarkClazz, profile, procTimeHistogram, traceTimeHistogram, traceCountHistogram));
        }

        return runResults;
    }

    private static int run(BenchmarkSettings settings, Histogram procTimeHistogram, Histogram traceTimeHistogram) {

        try (MillisMeasured measured = new MillisMeasured(procTimeHistogram)) {
            try (UIServerStub uiServerStub = new UIServerStub(settings)) {
                BenchmarkProcessRunner.runClassInSeparateJavaProcess(settings);

                if (!settings.getTracedPackages().isEmpty() && !StringUtils.isNullOrEmpty(settings.getMethodToTrace())) {
                    TCallTraceLogUploadRequest tCallTraceLogUploadRequest = uiServerStub.get(5, TimeUnit.MINUTES);
                    traceTimeHistogram.recordValue(tCallTraceLogUploadRequest.getLifetimeMillis());

                    CallEnterTraceList tCallEnterTraceDecoders = new CallEnterTraceList(tCallTraceLogUploadRequest.getTraceLog().getEnterTraces());
                    return tCallEnterTraceDecoders.size();
                }

                return 0;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static Histogram emptyHistogram() {
        return new Histogram(1, TimeUnit.MINUTES.toMillis(5), 2);
    }
}
