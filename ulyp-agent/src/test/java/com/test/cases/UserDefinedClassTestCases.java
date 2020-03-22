package com.test.cases;

@SuppressWarnings("all")
public class UserDefinedClassTestCases {

    public class TestClass {

    }

    TestClass returnInnerClass() {
        return new TestClass();
    }

    public static void main(String[] args) {
        SafeCaller.call(() -> new UserDefinedClassTestCases().returnInnerClass());
    }
}
