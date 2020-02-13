package com.ulyp.agent.tests;

public class SeveralMethodsCases {

    public int fib(int v) {
        if (v <= 1) {
            return v;
        }
        return fib(v - 1) + fib(v - 2);
    }

    public static void main(String[] args) {
        SafeCaller.call(() -> new SeveralMethodsCases().fib(3));
    }
}
