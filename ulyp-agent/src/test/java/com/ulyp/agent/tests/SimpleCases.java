package com.ulyp.agent.tests;

@SuppressWarnings("all")
public class SimpleCases {

    private static volatile Object store;

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

    public void consumesInt(int v) {
        store = v;
    }

    public static void main(String[] args) {
        SafeCaller.call(() -> new SimpleCases().returnIntWithEmptyParams());
        SafeCaller.call(() -> new SimpleCases().returnTestObjectWithEmptyParams());
        SafeCaller.call(() -> new SimpleCases().returnStringWithEmptyParams());
        SafeCaller.call(() -> new SimpleCases().returnNullObjectWithEmptyParams());
        SafeCaller.call(() -> new SimpleCases().throwsRuntimeException());
        SafeCaller.call(() -> new SimpleCases().consumesInt(45324));
    }
}
