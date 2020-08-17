package com.perf.agent.benchmarks.impl;

import com.perf.agent.benchmarks.Benchmark;
import com.perf.agent.benchmarks.BenchmarkProfile;
import com.perf.agent.benchmarks.BenchmarkProfileBuilder;
import com.ulyp.core.util.MethodMatcher;
import com.ulyp.core.util.PackageList;

import java.sql.*;
import java.util.Arrays;
import java.util.List;

public class H2MemDatabaseBenchmark2 implements Benchmark {

    private Connection connection;

    @Override
    public List<BenchmarkProfile> getProfiles() {
        return null;
    }

    public void setUp() throws SQLException {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        connection = DriverManager.getConnection("jdbc:h2:mem:", "sa", "");
//
//        Statement statement = connection.createStatement();
//        statement.execute("create table test(id int primary key, name varchar)");
    }

    public void tearDown() throws SQLException {

    }

    public void run() throws Exception {

    }

    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();

        H2MemDatabaseBenchmark2 benchmark = new H2MemDatabaseBenchmark2();
        benchmark.setUp();
        benchmark.run();
        benchmark.tearDown();

        System.out.println("Took: " + (System.currentTimeMillis() - start));
    }
}