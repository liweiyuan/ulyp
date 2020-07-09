package com.ulyp.core;

import java.lang.reflect.Method;
import java.util.Arrays;

public class Workspace {

    public static class TestClass {

    }

    public static class TestClassWithToString {
        @Override
        public String toString() {
            return "as" + 2;
        }
    }

    public static void main(String[] args) {

        System.out.println(Arrays.asList(TestClass.class.getDeclaredMethods()));
        System.out.println(Arrays.asList(TestClassWithToString.class.getDeclaredMethods()));

        Method declaredMethod = TestClassWithToString.class.getDeclaredMethods()[0];
    }
}
