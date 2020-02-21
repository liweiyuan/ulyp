package com.ulyp.agent.tests;

public class SeveralMethodsCases {

    public void callTwoMethods() {
        method1();
        method2();
    }

    public void method1() {
        System.out.println("b");
    }

    public void method2() {
        System.out.println("c");
    }

    public int fibonacci(int v) {
        if (v <= 1) {
            return v;
        }
        return fibonacci(v - 1) + fibonacci(v - 2);
    }

    public static void main(String[] args) {
        SafeCaller.call(() -> new SeveralMethodsCases().fibonacci(10));
        SafeCaller.call(() -> new SeveralMethodsCases().callTwoMethods());
    }
}
