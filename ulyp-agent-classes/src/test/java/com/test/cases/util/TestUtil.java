package com.test.cases.util;

import com.ulyp.agent.settings.SystemPropertiesSettings;
import org.buildobjects.process.ProcBuilder;
import org.buildobjects.process.ProcResult;
import org.junit.Assert;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class TestUtil {

    public static void runClassInSeparateJavaProcess(TestSettingsBuilder settingsBuilder) {
        File agentJar = findAgentJar();
        String classPath = System.getProperty("java.class.path");

        try {
            String javaHome = System.getProperty("java.home");
            String javaBinary = Paths.get(javaHome, "bin", "java").toString();

            List<String> processArgs = new ArrayList<>();
            processArgs.add("-javaagent:" + agentJar.getAbsolutePath());
            processArgs.add("-cp");
            processArgs.add(classPath);
            processArgs.add("-D" + SystemPropertiesSettings.MAX_CALL_TO_RECORD_PER_METHOD + "=" + settingsBuilder.getMaxCallsPerMethod());
            if (settingsBuilder.isUiEnabled()) {
                processArgs.add("-D" + SystemPropertiesSettings.UI_HOST_PROPERTY + "=localhost");
                processArgs.add("-D" + SystemPropertiesSettings.UI_PORT_PROPERTY + "=" + settingsBuilder.port);
            } else {
                processArgs.add("-D" + SystemPropertiesSettings.UI_ENABLED + "=false");
            }
//            processArgs.add("-D" + LoggingSettings.LOG_LEVEL_PROPERTY + "=TRACE");
            processArgs.add(settingsBuilder.getMainClassName().getName());

            ProcResult result = new ProcBuilder(javaBinary, processArgs.toArray(new String[]{}))
                    .withTimeoutMillis(TimeUnit.MINUTES.toMillis(3))
                    .ignoreExitStatus()
                    .run();

            System.out.println("Proc output:\n" + result.getOutputString());
            System.out.println("Proc err:\n" + result.getErrorString());
            System.out.println("Proc run time: " + result.getExecutionTime() + " ms");

            if (result.getExitValue() != 0) {
                Assert.fail("Process exit code is not 0, proc string " + result.getProcString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new AssertionError("Process ended unsuccessfully", e);
        }
    }

    private static File findAgentJar() {
        Path libDir = Paths.get("..", "ulyp-agent", "build", "libs");

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
