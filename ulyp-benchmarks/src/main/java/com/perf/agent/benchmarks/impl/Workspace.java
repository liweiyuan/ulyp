package com.perf.agent.benchmarks.impl;

public class Workspace {

    public static int foo() {
        return bar("as", "bx", "xc");
    }

    public static int bar(Object v1, Object v2, Object v3) {
        return 42;
    }

    public static void main(String[] args) {

        try {
            Thread.sleep(5000 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}