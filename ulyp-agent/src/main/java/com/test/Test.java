package com.test;

public class Test extends A {

    public Test() {
        B.foo();
    }

    public static void foo() {
        System.out.println("a");
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println(new Test());
        B.foo();

        Thread.sleep(5000000);
    }
}
