package com.test.cases;

@SuppressWarnings("all")
public class ObjectTestCases {

    private static volatile Object store0;
    private static volatile Object store1;
    private static volatile Object store2;
    private static volatile Object store3;

    public void acceptsTwoObjects(Object o1, Object o2) {
        store0 = o1;
        store1 = o2;
    }

    public void acceptsTwoObjects2(Object o1, Object o2) {
        store0 = o1;
        store1 = o2;
    }

    public void acceptsTwoObjects3(Object o1, Object o2) {
        store0 = o1;
        store1 = o2;
    }

    public void acceptsTwoNulls(Object o1, Object o2) {
        store0 = o1;
        store1 = o2;
    }

    private static class X {}

    private static class Y {
        public String toString() {
            return "Y{}";
        }
    }

    public static void main(String[] args) {
        SafeCaller.call(() -> new ObjectTestCases().acceptsTwoObjects(new Object(), new Object()));
        SafeCaller.call(() -> new ObjectTestCases().acceptsTwoObjects2("asdasd", 34));
        SafeCaller.call(() -> new ObjectTestCases().acceptsTwoObjects3(new X(), new Y()));
        SafeCaller.call(() -> new ObjectTestCases().acceptsTwoNulls(null, null));
    }
}
