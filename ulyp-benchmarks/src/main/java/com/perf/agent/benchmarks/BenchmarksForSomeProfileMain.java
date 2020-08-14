package com.perf.agent.benchmarks;

import com.perf.agent.benchmarks.impl.SpringHibernateBenchmark;
import com.perf.agent.benchmarks.proc.BenchmarkProcessRunner;
import com.perf.agent.benchmarks.proc.UIServerStub;
import com.ulyp.core.CallEnterTraceList;
import com.ulyp.core.util.PackageList;
import com.ulyp.transport.TCallTraceLogUploadRequest;
import org.HdrHistogram.Histogram;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BenchmarksForSomeProfileMain {

    private static final int ITERATIONS_PER_PROFILE = 1;

    public static void main(String[] args) throws Exception {

        BenchmarkProfile trueProfile = new BenchmarkProfileBuilder()
                .withInstrumentedPackages(new PackageList("com", "org"))
//                .withAdditionalArgs(
//                        "-XX:+UnlockDiagnosticVMOptions",
//                        "-XX:+UnlockCommercialFeatures",
//                        "-XX:+FlightRecorder",
//                        "-XX:+DebugNonSafepoints",
//                        "-XX:StartFlightRecording=name=Profiling,dumponexit=true,delay=2s,filename=C:\\Temp\\myrecording.jfr,settings=profile"
//                )
                .build();

        for (RunResult result : runBench(SpringHibernateBenchmark.class, trueProfile)) {
            result.print();
        }
    }

    private static List<RunResult> runBench(
            Class<? extends Benchmark> benchmarkClazz,
            BenchmarkProfile profile) throws Exception {
        List<RunResult> runResults = new ArrayList<>();


        Histogram procTimeHistogram = emptyHistogram();
        Histogram traceTimeHistogram = emptyHistogram();
        Histogram traceCountHistogram = emptyHistogram();

        for (int i = 0; i < ITERATIONS_PER_PROFILE; i++) {
            int tracesCount = run(benchmarkClazz, profile, procTimeHistogram, traceTimeHistogram);
            traceCountHistogram.recordValue(tracesCount);
        }

        runResults.add(new RunResult(benchmarkClazz, profile, procTimeHistogram, traceTimeHistogram, traceCountHistogram));

        return runResults;
    }

    private static int run(Class<?> benchmarkClazz, BenchmarkProfile profile, Histogram procTimeHistogram, Histogram traceTimeHistogram) {

        try (MillisMeasured measured = new MillisMeasured(procTimeHistogram)) {
            try (UIServerStub uiServerStub = new UIServerStub(profile)) {

                BenchmarkProcessRunner.runClassInSeparateJavaProcess(benchmarkClazz, profile);

                if (profile.shouldSendSomethingToUi()) {

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
