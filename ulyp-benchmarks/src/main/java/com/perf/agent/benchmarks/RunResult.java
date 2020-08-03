package com.perf.agent.benchmarks;

import org.HdrHistogram.Histogram;

import java.util.concurrent.TimeUnit;

public class RunResult {

    private final Class<?> benchmarkClazz;
    private final String profileName;
    private final double mean;
    private final double stdDev;
    private final TimeUnit timeUnit;

    public RunResult(Class<?> benchmarkClazz, String profileName, Histogram histogram, TimeUnit timeUnit) {
        this.benchmarkClazz = benchmarkClazz;
        this.profileName = profileName;
        this.mean = histogram.getMean();
        this.stdDev = histogram.getStdDeviation();
        this.timeUnit = timeUnit;
    }

    public void print() {
        StringBuilder builder = new StringBuilder();

        builder.append(benchmarkClazz);
        builder.append(": ");
        builder.append(profileName);
        padTo(builder, 90);

        builder.append(String.format("%.0f", mean))
                .append(" ± ")
                .append(String.format("%.0f", stdDev))
                .append("   ")
                .append(timeUnit);

        System.out.println(builder);
    }

    private void padTo(StringBuilder builder, int length) {
        while(builder.length() < length) {
            builder.append(' ');
        }
    }
}
