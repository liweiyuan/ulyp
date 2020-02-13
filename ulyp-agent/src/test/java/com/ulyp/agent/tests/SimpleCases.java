package com.ulyp.agent.tests;

public class SimpleCases {

    public int returnIntWithEmptyParams() {
        return 124234232;
    }

    public int throwsRuntimeException() {
        throw new RuntimeException("exception message");
    }

    public static void main(String[] args) {
        SafeCaller.call(() -> new SimpleCases().returnIntWithEmptyParams());
        SafeCaller.call(() -> new SimpleCases().throwsRuntimeException());
    }
}
