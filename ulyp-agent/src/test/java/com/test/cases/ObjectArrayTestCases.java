package com.test.cases;

@SuppressWarnings("all")
public class ObjectArrayTestCases {

    private static volatile Object store0;
    private static volatile Object store1;

    public void acceptEmptyObjectArray(Object[] array) {
        store0 = array;
    }

    private static class X {
        private final String text;

        private X(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return "X{" +
                    "text='" + text + '\'' +
                    '}';
        }
    }

    public void acceptEmptyUserDefinedClassArray(X[] array) {
        store0 = array;
    }

    public void acceptUserDefinedClassArrayWith3Elements(X[] array) {
        store0 = array;
    }

    public static void main(String[] args) {
        SafeCaller.call(() -> new ObjectArrayTestCases().acceptEmptyObjectArray(new Object[]{}));
        SafeCaller.call(() -> new ObjectArrayTestCases().acceptEmptyUserDefinedClassArray(new X[]{}));
        SafeCaller.call(() -> new ObjectArrayTestCases().acceptUserDefinedClassArrayWith3Elements(
                new X[]{new X("a"), null, new X("b")}
                )
        );
    }
}
