package com.ulyp.agent.tests;

public class SimpleCases {

    public static class TestObject {}

    public TestObject returnTestObjectWithEmptyParams() {
        return new TestObject();
    }

    public String returnStringWithEmptyParams() {
        return "asdvdsa2";
    }

    public String returnNullObjectWithEmptyParams() {
        return null;
    }

    public int returnIntWithEmptyParams() {
        return 124234232;
    }

    public int throwsRuntimeException() {
        throw new RuntimeException("exception message");
    }

    public static void main(String[] args) {
        SafeCaller.call(() -> new SimpleCases().returnIntWithEmptyParams());
        SafeCaller.call(() -> new SimpleCases().returnTestObjectWithEmptyParams());
        SafeCaller.call(() -> new SimpleCases().returnStringWithEmptyParams());
        SafeCaller.call(() -> new SimpleCases().returnNullObjectWithEmptyParams());
        SafeCaller.call(() -> new SimpleCases().throwsRuntimeException());
    }
}
