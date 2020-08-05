package com.perf.agent.benchmarks;

import org.HdrHistogram.Histogram;

public class RunResult {

    private final Class<?> benchmarkClazz;
    private final BenchmarkProfile profile;
    private final Histogram procTimeHistogram;
    private final Histogram traceTimeHistogram;
    private final Histogram traceCountHistogram;

    public RunResult(
            Class<?> benchmarkClazz,
            BenchmarkProfile profile,
            Histogram procTimeHistogram,
            Histogram traceTimeHistogram,
            Histogram traceCountHistogram) {
        this.benchmarkClazz = benchmarkClazz;
        this.profile = profile;
        this.procTimeHistogram = procTimeHistogram;
        this.traceTimeHistogram = traceTimeHistogram;
        this.traceCountHistogram = traceCountHistogram;
    }

    public void print() {
        StringBuilder builder = new StringBuilder();

        builder.append(benchmarkClazz.getSimpleName());
        builder.append(": ");
        padTo(builder, 30);
        builder.append(profile);
        padTo(builder, 70);

        builder.append(String.format("%.2f", procTimeHistogram.getMean() / 1000.0))
                .append(" ± ")
                .append(String.format("%.3f", procTimeHistogram.getStdDeviation() / 1000.0))
                .append("   ")
                .append(String.format("%.2f", traceTimeHistogram.getMean() / 1000.0))
                .append(" ± ")
                .append(String.format("%.3f", traceTimeHistogram.getStdDeviation() / 1000.0))
                .append("   ")
                .append("sec")
                .append(" ")
                .append(traceCountHistogram.getMean());

        System.out.println(builder);
    }

    private void padTo(StringBuilder builder, int length) {
        while(builder.length() < length) {
            builder.append(' ');
        }
    }
}
