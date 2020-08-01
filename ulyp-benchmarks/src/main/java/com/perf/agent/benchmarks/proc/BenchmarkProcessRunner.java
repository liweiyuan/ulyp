package com.perf.agent.benchmarks.proc;

import com.perf.agent.benchmarks.BenchmarkSettings;
import org.buildobjects.process.ProcBuilder;
import org.buildobjects.process.ProcResult;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class BenchmarkProcessRunner {

    public static void runClassInSeparateJavaProcess(BenchmarkSettings settings) {
        File agentJar = findAgentJar();
        String classPath = System.getProperty("java.class.path");

        try {
            String javaHome = System.getProperty("java.home");
            String javaBinary = Paths.get(javaHome, "bin", "java").toString();

            List<String> processArgs = new ArrayList<>();
            processArgs.add("-javaagent:" + agentJar.getAbsolutePath());
            processArgs.add("-cp");
            processArgs.add(classPath);
            processArgs.add("-Dulyp.ui-host=localhost");
            processArgs.add("-Dulyp.ui-port=" + settings.getUiListenPort());
            processArgs.add(settings.getClassToTrace().getName());

            ProcResult result = new ProcBuilder(javaBinary, processArgs.toArray(new String[]{}))
                    .withTimeoutMillis(TimeUnit.MINUTES.toMillis(3))
                    .ignoreExitStatus()
                    .run();

            System.out.println("Proc output:\n" + result.getOutputString());
            System.out.println("Proc err:\n" + result.getErrorString());
            System.out.println("Proc run time: " + result.getExecutionTime() + " ms");

            if (result.getExitValue() != 0) {
                throw new RuntimeException("Process exit code is not 0, proc string " + result.getProcString());
            }
        } catch (Exception e) {
            throw new RuntimeException("Process ended unsuccessfully", e);
        }
    }

    private static File findAgentJar() {
        Path libDir;
        if (Files.exists(Paths.get("..", "ulyp-agent"))) {
            libDir = Paths.get("..", "ulyp-agent", "build", "libs");
        } else {
            libDir = Paths.get("ulyp-agent", "build", "libs");
        }


        return Arrays.stream(Objects.requireNonNull(libDir.toFile().listFiles()))
                .filter(file -> file.getName().startsWith("ulyp-agent"))
                .filter(file -> file.getName().endsWith(".jar"))
                .findAny()
                .orElseThrow(() -> new AssertionError("Could not find built ulyp agent jar"));
    }
}
