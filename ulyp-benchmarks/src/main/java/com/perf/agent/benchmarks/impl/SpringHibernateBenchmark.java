package com.perf.agent.benchmarks.impl;

import com.perf.agent.benchmarks.Benchmark;
import com.perf.agent.benchmarks.BenchmarkProfile;
import com.perf.agent.benchmarks.impl.spring.ApplicationConfiguration;
import com.perf.agent.benchmarks.impl.spring.User;
import com.perf.agent.benchmarks.impl.spring.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SpringHibernateBenchmark implements Benchmark {

    private static final int USER_TO_SAVE = 1;

    @Override
    public List<BenchmarkProfile> getProfiles() {
        return Arrays.asList(
                new BenchmarkProfile(SpringHibernateBenchmark.class, "main", Arrays.asList("com", "org")),
                new BenchmarkProfile(UserService.class, "save", Arrays.asList("com", "org")),
                new BenchmarkProfile(null, null, Arrays.asList("com", "org")),
                new BenchmarkProfile(null, null, Collections.emptyList())
        );
    }

    private ApplicationContext context;
    private UserService saver;

    @Override
    public void setUp() throws Exception {
        context = new AnnotationConfigApplicationContext(ApplicationConfiguration.class);
        saver = context.getBean(UserService.class);
    }

    @Override
    public void tearDown() throws Exception {
        int count = saver.findAll().size();
        if (count != 1) {
            throw new RuntimeException("Doesn't work, users found: " + count);
        }
    }

    @Override
    public void run() throws Exception {
        User user = new User("Test", "User");
        saver.save(user);
    }

    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();

        SpringHibernateBenchmark benchmark = new SpringHibernateBenchmark();
        benchmark.setUp();
        benchmark.run();
        benchmark.tearDown();

        System.out.println("Took: " + (System.currentTimeMillis() - start));
    }
}
