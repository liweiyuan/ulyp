package com.ulyp.agent.util;

import com.ulyp.agent.Settings;
import org.junit.Assert;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.concurrent.ThreadLocalRandom;

public class TestUtil {

    public static void runClassInSeparateJavaProcess(AgentSettings settings, int uiPort) {
        File agentJar = findAgentJar();
        String cp = System.getProperty("java.class.path");

        try {
            ProcessBuilder ps = new ProcessBuilder(
                    "java",
                    "-javaagent:" + agentJar.getAbsolutePath(),
                    "-D" + Settings.PACKAGES_PROPERTY + "=" + settings.getPackages(),
                    "-D" + Settings.START_METHOD_PROPERTY + "=" + settings.getStartMethod(),
                    "-D" + Settings.UI_PORT_PROPERTY + "=" + uiPort,
                    "-D" + Settings.MAX_DEPTH_PROPERTY + "=" + settings.getMaxDepth(),
                    "-cp",
                    cp,
                    settings.getMainClassName().getName()
            );

            ps.redirectErrorStream(true);

            Process pr = ps.start();

            BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
            int code = pr.waitFor();
            Assert.assertEquals(0, code);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    public static void runClassInSeparateJavaProcess(Class<?> cl, String packages, String startMethod, int uiPort) {
        File agentJar = findAgentJar();
        String cp = System.getProperty("java.class.path");

        try {
            ProcessBuilder ps = new ProcessBuilder(
                    "java",
                    "-javaagent:" + agentJar.getAbsolutePath(),
                    "-D" + Settings.PACKAGES_PROPERTY + "=" + packages,
                    "-D" + Settings.START_METHOD_PROPERTY + "=" + startMethod,
                    "-D" + Settings.UI_PORT_PROPERTY + "=" + uiPort,
                    "-cp",
                    cp,
                    cl.getName()
            );

            ps.redirectErrorStream(true);

            Process pr = ps.start();

            BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
            int code = pr.waitFor();
            Assert.assertEquals(0, code);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    private static File findAgentJar() {
        File userDir = new File(System.getProperty("user.dir"));
        if (userDir.getName().equals("ulyp-agent")) {
            // TODO remove version from here
            File file = new File(userDir.getAbsoluteFile() + "/build/libs/ulyp-agent-0.2.jar");
            if (!file.exists()) {
                Assert.fail("Could not find ulyp-agent-0.2.jar");
            }
            return file;
        } else {
            Assert.fail("Expected current folder to be ulyp-agent, but instead was " + userDir.getName());
            throw new RuntimeException("Test failed"); // Will not happen, but needed for compiler
        }
    }

    public static int pickEmptyPort() {
        // TODO implement
        return 10000 + ThreadLocalRandom.current().nextInt(1000);
    }
}
