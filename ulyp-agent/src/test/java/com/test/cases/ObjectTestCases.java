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

    public static void main(String[] args) {
        SafeCaller.call(() -> new ObjectTestCases().acceptsTwoObjects(new Object(), new Object()));
        SafeCaller.call(() -> new ObjectTestCases().acceptsTwoObjects2("asdasd", 34));
    }
}
