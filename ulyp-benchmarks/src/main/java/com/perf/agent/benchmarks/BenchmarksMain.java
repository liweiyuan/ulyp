package com.perf.agent.benchmarks;

import com.perf.agent.benchmarks.impl.H2MemDatabaseBenchmark;
import com.perf.agent.benchmarks.impl.SpringHibernateBenchmark;
import com.perf.agent.benchmarks.proc.BenchmarkProcessRunner;
import com.perf.agent.benchmarks.proc.UIServerStub;
import com.ulyp.core.CallEnterRecordList;
import com.ulyp.transport.TCallRecordLogUploadRequest;
import org.HdrHistogram.Histogram;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BenchmarksMain {

    private static final int ITERATIONS_PER_PROFILE = 5;

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

        for (BenchmarkProfile profile : benchmark.getProfiles()) {
            Histogram procTimeHistogram = emptyHistogram();
            Histogram recordTimeHistogram = emptyHistogram();
            Histogram recordsCountHistogram = emptyHistogram();

            for (int i = 0; i < ITERATIONS_PER_PROFILE; i++) {
                int recordsCount = run(benchmarkClazz, profile, procTimeHistogram, recordTimeHistogram);
                recordsCountHistogram.recordValue(recordsCount);
            }

            runResults.add(new RunResult(benchmarkClazz, profile, procTimeHistogram, recordTimeHistogram, recordsCountHistogram));
        }

        return runResults;
    }

    private static int run(Class<?> benchmarkClazz, BenchmarkProfile profile, Histogram procTimeHistogram, Histogram recordsTimeHistogram) {

        try (MillisMeasured measured = new MillisMeasured(procTimeHistogram)) {
            try (UIServerStub uiServerStub = new UIServerStub(profile)) {

                BenchmarkProcessRunner.runClassInSeparateJavaProcess(benchmarkClazz, profile);

                if (profile.shouldSendSomethingToUi()) {

                    TCallRecordLogUploadRequest TCallRecordLogUploadRequest = uiServerStub.get(5, TimeUnit.MINUTES);
                    recordsTimeHistogram.recordValue(TCallRecordLogUploadRequest.getLifetimeMillis());

                    return new CallEnterRecordList(TCallRecordLogUploadRequest.getRecordLog().getEnterRecords()).size();
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
