package com.test.cases;

@SuppressWarnings("all")
public class RecursionTestCases {

    public int fibonacci(int v) {
        if (v <= 1) {
            return v;
        }
        return fibonacci(v - 1) + fibonacci(v - 2);
    }

    public static void main(String[] args) {
        SafeCaller.call(() -> new RecursionTestCases().fibonacci(10));
    }
}
