package com.ulyp.agent;

import org.junit.Assert;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.concurrent.ThreadLocalRandom;

public class TestUtil {

    public static class ForkAgentSettings {
        private Class<?> mainClassName;
        private String packages;
        private String startMethod;
        private int maxDepth = Integer.MAX_VALUE;

        public ForkAgentSettings setPackages(String packages) {
            this.packages = packages;
            return this;
        }

        public ForkAgentSettings setStartMethod(String startMethod) {
            this.startMethod = startMethod;
            return this;
        }

        public ForkAgentSettings setMaxDepth(int maxDepth) {
            this.maxDepth = maxDepth;
            return this;
        }

        public ForkAgentSettings setMainClassName(Class<?> mainClassName) {
            this.mainClassName = mainClassName;
            return this;
        }
    }

    public static void runClassInSeparateJavaProcess(ForkAgentSettings settings, int uiPort) {
        File agentJar = findAgentJar();
        String cp = System.getProperty("java.class.path");

        try {
            ProcessBuilder ps = new ProcessBuilder(
                    "java",
                    "-javaagent:" + agentJar.getAbsolutePath(),
                    "-Dulyp.packages=" + settings.packages,
                    "-Dulyp.start-method=" + settings.startMethod,
                    "-Dulyp.ui-port=" + uiPort,
                    "-Dulyp.max-depth=" + settings.maxDepth,
                    "-cp",
                    cp,
                    settings.mainClassName.getName()
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
                    "-Dulyp.packages=" + packages,
                    "-Dulyp.start-method=" + startMethod,
                    "-Dulyp.ui-port=" + uiPort,
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
        // TODO remove version from here
        File userDir = new File(System.getProperty("user.dir"));
        if (userDir.getName().equals("ulyp-agent")) {
            File file = new File(userDir.getAbsoluteFile() + "/build/libs/ulyp-agent-0.2.jar");
            if (!file.exists()) {
                Assert.fail("Could not find ulyp-agent-0.2.jar");
            }
            return file;
        } else {
            return null;
        }
    }

    public static int pickEmptyPort() {
        // TODO implement
        return 10000 + ThreadLocalRandom.current().nextInt(1000);
    }
}
