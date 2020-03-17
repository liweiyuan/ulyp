package com.ulyp.agent.tests;

public class SeveralMethodsTestCases {

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

    public static void main(String[] args) {
        SafeCaller.call(() -> new SeveralMethodsTestCases().callTwoMethods());
    }
}
