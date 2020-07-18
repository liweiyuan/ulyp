package com.ulyp.agent.util;

import com.ulyp.agent.settings.AgentSettings;
import org.buildobjects.process.ProcBuilder;
import org.buildobjects.process.ProcResult;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class TestUtil {

    public static void runClassInSeparateJavaProcess(TestSettingsBuilder settingsBuilder) {
        File agentJar = findAgentJar();
        String classPath = System.getProperty("java.class.path");

        try {

            AgentSettings settings = settingsBuilder.build();

            String javaHome = System.getProperty("java.home");
            String javaBinary = Paths.get(javaHome, "bin", "java").toString();

            List<String> processArgs = new ArrayList<>();
            processArgs.add("-javaagent:" + agentJar.getAbsolutePath());
            processArgs.addAll(settings.toCmdJavaProps());
            processArgs.add("-cp");
            processArgs.add(classPath);
            processArgs.add(settingsBuilder.getMainClassName().getName());

            ProcResult run = new ProcBuilder(javaBinary, processArgs.toArray(new String[]{})).run();

            System.out.println(run.getOutputString());
        } catch (Exception e) {
            e.printStackTrace();
            throw new AssertionError("Process ended unsuccessfully", e);
        }
    }

    private static File findAgentJar() {
        Path libDir = Paths.get(".", "build", "libs");

        return Arrays.stream(Objects.requireNonNull(libDir.toFile().listFiles()))
                .filter(file -> file.getName().startsWith("ulyp-agent"))
                .filter(file -> file.getName().endsWith(".jar"))
                .findAny()
                .orElseThrow(() -> new AssertionError("Could not find built ulyp agent jar"));
    }

    public static int pickEmptyPort() {
        // TODO implement
        return 10000 + ThreadLocalRandom.current().nextInt(1000);
    }
}
