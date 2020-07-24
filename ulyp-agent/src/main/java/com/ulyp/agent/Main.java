package com.ulyp.agent;

public class Main {
    public static void main(String[] args) {
        // TODO replace
        System.out.println("Sample usage: java -javaagent:C:\\Work\\ulyp\\ulyp-agent\\build\\libs\\ulyp-agent-0.1.jar\n " +
                "-Dulyp.packages=com.demo,org.hibernate,org.h2\n " +
                "-Dulyp.start-method=JpaProxyUserRepositoryIntegrationTest.sampleTestCase YourClass");
    }
}
