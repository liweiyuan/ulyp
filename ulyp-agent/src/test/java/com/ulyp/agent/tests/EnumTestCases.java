package com.ulyp.agent.tests;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("all")
public class EnumTestCases {

    private static volatile Object store0;
    private static volatile Object store1;
    private static volatile Object store2;
    private static volatile Object store3;

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

    public void consumesMapAndEnums(Map<TestEnum, TestEnum> map, TestEnum l1, TestEnum l2) {
        store0 = map;
        store1 = l1;
        store2 = l2;
    }

    public static void main(String[] args) {
        SafeCaller.call(() -> new EnumTestCases().consumesMapAndEnums(new HashMap<TestEnum, TestEnum>() {{ put(TestEnum.T1, TestEnum.T2); }}, TestEnum.T1, TestEnum.T2));
    }
}
