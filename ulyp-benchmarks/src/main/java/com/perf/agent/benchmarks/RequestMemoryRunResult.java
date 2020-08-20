package com.perf.agent.benchmarks;

import org.HdrHistogram.Histogram;

public class RequestMemoryRunResult {

    private final Class<?> benchmarkClazz;
    private final BenchmarkProfile profile;
    private final long bytesSize;

    public RequestMemoryRunResult(
            Class<?> benchmarkClazz,
            BenchmarkProfile profile,
            long bytesSize) {
        this.benchmarkClazz = benchmarkClazz;
        this.profile = profile;
        this.bytesSize = bytesSize;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append(benchmarkClazz.getSimpleName());
        builder.append(": ");
        padTo(builder, 30);
        builder.append(profile);
        padTo(builder, 70);

        builder.append(bytesSize / 1000).append(" kb");

        return builder.toString();
    }

    private void padTo(StringBuilder builder, int length) {
        while (builder.length() < length) {
            builder.append(' ');
        }
    }
}
