package com.ulyp.agent.tests;

@SuppressWarnings("all")
public class SimpleTestCases {

    private static volatile Object store0;
    private static volatile Object store1;
    private static volatile Object store2;
    private static volatile Object store3;

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
        store0 = v;
    }

    public void consumesIntAndString(int v, String s) {
        store0 = v;
        store1 = s;
    }

    public static void staticMethod() {}

    public static void main(String[] args) {
        SafeCaller.call(() -> new SimpleTestCases().returnIntWithEmptyParams());
        SafeCaller.call(() -> new SimpleTestCases().returnTestObjectWithEmptyParams());
        SafeCaller.call(() -> new SimpleTestCases().returnStringWithEmptyParams());
        SafeCaller.call(() -> new SimpleTestCases().returnNullObjectWithEmptyParams());
        SafeCaller.call(() -> new SimpleTestCases().throwsRuntimeException());
        SafeCaller.call(() -> new SimpleTestCases().consumesInt(45324));
        SafeCaller.call(() -> new SimpleTestCases().consumesIntAndString(45324, "asdasd"));
        staticMethod();
    }
}
