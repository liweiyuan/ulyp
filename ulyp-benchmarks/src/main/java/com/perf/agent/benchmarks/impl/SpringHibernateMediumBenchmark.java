package com.perf.agent.benchmarks.impl;

import com.perf.agent.benchmarks.Benchmark;
import com.perf.agent.benchmarks.BenchmarkProfile;
import com.perf.agent.benchmarks.BenchmarkProfileBuilder;
import com.perf.agent.benchmarks.impl.spring.*;
import com.ulyp.core.util.MethodMatcher;
import com.ulyp.core.util.PackageList;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class SpringHibernateMediumBenchmark implements Benchmark {

    private static final int PEOPLE_PER_DEPT = 1;
    private static final int DEPT_COUNT = 1;

    @Override
    public List<BenchmarkProfile> getProfiles() {
        return Arrays.asList(
                new BenchmarkProfileBuilder()
                        .withMethodToRecord(new MethodMatcher(SpringHibernateMediumBenchmark.class, "main"))
                        .withInstrumentedPackages(new PackageList("com", "org"))
                        .build(),
                new BenchmarkProfileBuilder()
                        .withMethodToRecord(new MethodMatcher(SpringHibernateMediumBenchmark.class, "setUp"))
                        .withInstrumentedPackages(new PackageList("com", "org"))
                        .build(),
                new BenchmarkProfileBuilder()
                        .withInstrumentedPackages(new PackageList("com", "org"))
                        .build(),
                new BenchmarkProfileBuilder().build()
        );
    }

    private ApplicationContext context;
    private DepartmentService departmentService;

    @Override
    public void setUp() throws Exception {
        context = new AnnotationConfigApplicationContext(ApplicationConfiguration.class);
        departmentService = context.getBean(DepartmentService.class);

        for (int i = 0; i < DEPT_COUNT; i++) {
            Department department = new Department();

            for (int z = 0; z < PEOPLE_PER_DEPT; z++) {
                Person person = new Person();
                person.setFirstName("Jeff_" + i + "_" + z);
                person.setLastName("");
                person.setPhoneNumber("+15324234234");
                person.setAge(ThreadLocalRandom.current().nextInt(50));
                department.addPerson(person);
            }

            departmentService.save(department);
        }
    }

    @Override
    public void tearDown() throws Exception {
        int peopleCount = departmentService.countPeople();
        if (peopleCount != DEPT_COUNT * PEOPLE_PER_DEPT) {
            throw new RuntimeException("People " + peopleCount);
        }

        departmentService.removeAll();
    }

    @Override
    public void run() throws Exception {
        departmentService.shufflePeople();
    }

    public static void main(String[] args) throws Exception {
        System.out.println(ManagementFactory.getRuntimeMXBean().getInputArguments());

        long start = System.currentTimeMillis();

        SpringHibernateMediumBenchmark benchmark = new SpringHibernateMediumBenchmark();
        benchmark.setUp();
        benchmark.run();
        benchmark.tearDown();

        System.out.println("Took: " + (System.currentTimeMillis() - start));
    }
}
