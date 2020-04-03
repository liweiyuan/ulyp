package com.test.cases;

@SuppressWarnings("all")
public class UserDefinedClassTestCases {

    public class TestClass {

    }

    public static class ToStringCallsSelf {

        private final String name;
        private final String secondName;

        public ToStringCallsSelf(String name, String secondName) {
            this.name = name;
            this.secondName = secondName;
        }

        public String getName() {
            return name;
        }

        public String getSecondName() {
            return secondName;
        }

        @Override
        public String toString() {
            return "ToStringCallsSelf{" +
                    "name='" + getName() + '\'' +
                    ", secondName='" + getSecondName() + '\'' +
                    '}';
        }
    }

    TestClass returnInnerClass() {
        return new TestClass();
    }

    ToStringCallsSelf returnClassThatCallsSelfInToString(ToStringCallsSelf v1, ToStringCallsSelf v2) {
        return new ToStringCallsSelf(v1.toString() + v2.toString(), v1.toString() + "/" + v2.toString());
    }

    public static void main(String[] args) {
        SafeCaller.call(() -> new UserDefinedClassTestCases().returnInnerClass());
        SafeCaller.call(() -> new UserDefinedClassTestCases().returnClassThatCallsSelfInToString(
                new ToStringCallsSelf("n1", "s1"),
                new ToStringCallsSelf("n1", "s1")
        ));
    }
}
