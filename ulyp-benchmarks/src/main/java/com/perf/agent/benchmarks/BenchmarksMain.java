package com.perf.agent.benchmarks;

import com.perf.agent.benchmarks.impl.H2MemDatabaseBenchmark;
import com.perf.agent.benchmarks.proc.BenchmarkProcessRunner;
import com.perf.agent.benchmarks.proc.UIServerStub;
import org.HdrHistogram.Histogram;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class BenchmarksMain {

    public static int pickEmptyPort() {
        // TODO implement
        return 10000 + ThreadLocalRandom.current().nextInt(1000);
    }

    public static void main(String[] args) {

        List<RunResult> runResults = new ArrayList<>();

        runResults.addAll(runBench(H2MemDatabaseBenchmark.class));

        for (RunResult runResult : runResults) {
            runResult.print();
        }
    }

    private static List<RunResult> runBench(Class<?> benchmarkClazz) {
        List<RunResult> runResults = new ArrayList<>();

        BenchmarkSettings settings = new BenchmarkSettings();
        settings.setClassToTrace(benchmarkClazz);
        settings.setMethodToTrace("main");
        settings.setUiListenPort(pickEmptyPort());
        settings.setTracedPackages(new H2MemDatabaseBenchmark().getPackagesToTrace());

        // WARMUP
        for (int i = 0; i < 5; i++) {
            run(settings);
        }

        Histogram histogram = new Histogram(1, TimeUnit.MINUTES.toMillis(5), 2);

        for (int i = 0; i < 10; i++) {
            try (MillsMeasured measured = new MillsMeasured(histogram)) {
                run(settings);
            }
        }

        runResults.add(new RunResult(benchmarkClazz, "main() trace", histogram, TimeUnit.MILLISECONDS));

        return runResults;
    }

    private static void run(BenchmarkSettings settings) {
        try (UIServerStub uiServerStub = new UIServerStub(settings)) {
            BenchmarkProcessRunner.runClassInSeparateJavaProcess(settings);

            if (settings.getClassToTrace() != null) {
                uiServerStub.get(5, TimeUnit.MINUTES);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
