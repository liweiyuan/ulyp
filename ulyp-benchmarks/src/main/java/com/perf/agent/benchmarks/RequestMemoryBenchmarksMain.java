package com.perf.agent.benchmarks;

import com.perf.agent.benchmarks.impl.H2MemDatabaseBenchmark;
import com.perf.agent.benchmarks.impl.SpringHibernateMediumBenchmark;
import com.perf.agent.benchmarks.impl.SpringHibernateSmallBenchmark;
import com.perf.agent.benchmarks.proc.BenchmarkProcessRunner;
import com.perf.agent.benchmarks.proc.UIServerStub;
import com.ulyp.transport.TCallRecordLogUploadRequest;
import org.HdrHistogram.Histogram;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RequestMemoryBenchmarksMain {

    public static void main(String[] args) throws Exception {

        List<RequestMemoryRunResult> runResults = new ArrayList<>();

//        runResults.addAll(runBench(H2MemDatabaseBenchmark.class));
//        runResults.addAll(runBench(SpringHibernateSmallBenchmark.class));
        runResults.addAll(runBench(SpringHibernateMediumBenchmark.class));

        for (RequestMemoryRunResult runResult : runResults) {
            System.out.println(runResult);
        }
    }

    private static List<RequestMemoryRunResult> runBench(Class<? extends Benchmark> benchmarkClazz) throws Exception {
        List<RequestMemoryRunResult> runResults = new ArrayList<>();

        Benchmark benchmark = benchmarkClazz.newInstance();

        for (BenchmarkProfile profile : benchmark.getProfiles()) {
            if (!profile.shouldSendSomethingToUi()) {
                // If nothing is sent to UI, then there is nothing to measure
                continue;
            }

            TCallRecordLogUploadRequest request = run(benchmarkClazz, profile);
            int byteSize = request.getSerializedSize();
            runResults.add(new RequestMemoryRunResult(benchmarkClazz, profile, byteSize));
        }

        return runResults;
    }

    private static TCallRecordLogUploadRequest run(Class<?> benchmarkClazz, BenchmarkProfile profile) {

        try (UIServerStub uiServerStub = new UIServerStub(profile)) {

            BenchmarkProcessRunner.runClassInSeparateJavaProcess(benchmarkClazz, profile);

            return uiServerStub.get(5, TimeUnit.MINUTES);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Histogram emptyHistogram() {
        return new Histogram(1, 1_000_000_000, 2);
    }
}
