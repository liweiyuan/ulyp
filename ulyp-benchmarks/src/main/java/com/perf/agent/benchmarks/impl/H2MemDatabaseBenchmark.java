package com.perf.agent.benchmarks.impl;

import com.perf.agent.benchmarks.Benchmark;
import com.perf.agent.benchmarks.BenchmarkProfile;

import java.sql.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class H2MemDatabaseBenchmark implements Benchmark {

    private Connection connection;

    @Override
    public List<BenchmarkProfile> getProfiles() {
        return Arrays.asList(
                // TODO use builder
                new BenchmarkProfile(H2MemDatabaseBenchmark.class, "main", Arrays.asList("com", "org")),
                new BenchmarkProfile(null, null, Arrays.asList("com", "org")),
                // TODO use named static var
                new BenchmarkProfile(null, null, Collections.emptyList())
        );
    }

    public void setUp() throws SQLException {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        connection = DriverManager.getConnection("jdbc:h2:mem:", "sa", "");

        Statement statement = connection.createStatement();
        statement.execute("create table test(id int primary key, name varchar)");
    }

    public void tearDown() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select count(*) from test");
        resultSet.next();
        System.out.println(resultSet.getInt(1));
        connection.close();

        statement.close();
    }

    public void run() throws Exception {

        for (int id = 0; id < 10; id++) {
            new Inserter(connection).insert(id);
        }
    }

    private static class Inserter {
        private final Connection connection;

        private Inserter(Connection connection) {
            this.connection = connection;
        }

        public void insert(int id) throws SQLException {
            PreparedStatement prep = connection.prepareStatement(
                    "insert into test values(?, ?)");
            prep.setInt(1, id);
            prep.setString(2, "Hello");
            prep.execute();
        }
    }

    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();

        H2MemDatabaseBenchmark benchmark = new H2MemDatabaseBenchmark();
        benchmark.setUp();
        benchmark.run();
        benchmark.tearDown();

        System.out.println("Took: " + (System.currentTimeMillis() - start));
    }
}