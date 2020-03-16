package com.ulyp.agent.tests;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("all")
public class SimpleCases {

    public enum TestEnum {
        T1("3.4"),
        T2("3.5");

        private final String s;

        TestEnum(String x) {
            s = x;
        }

        public String toString() {
            return s;
        }
    }

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

    public void consumesMapAndEnums(Map<TestEnum, TestEnum> map, TestEnum l1, TestEnum l2) {
        store0 = map;
        store1 = l1;
        store2 = l2;
    }

    public static void main(String[] args) {
        SafeCaller.call(() -> new SimpleCases().returnIntWithEmptyParams());
        SafeCaller.call(() -> new SimpleCases().returnTestObjectWithEmptyParams());
        SafeCaller.call(() -> new SimpleCases().returnStringWithEmptyParams());
        SafeCaller.call(() -> new SimpleCases().returnNullObjectWithEmptyParams());
        SafeCaller.call(() -> new SimpleCases().throwsRuntimeException());
        SafeCaller.call(() -> new SimpleCases().consumesInt(45324));
        SafeCaller.call(() -> new SimpleCases().consumesIntAndString(45324, "asdasd"));
        SafeCaller.call(() -> new SimpleCases().consumesMapAndEnums(new HashMap<TestEnum, TestEnum>() {{ put(TestEnum.T1, TestEnum.T2); }}, TestEnum.T1, TestEnum.T2));
    }
}
