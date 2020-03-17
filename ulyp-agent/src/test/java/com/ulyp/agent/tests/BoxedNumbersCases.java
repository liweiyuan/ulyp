package com.ulyp.agent.tests;

@SuppressWarnings("all")
public class BoxedNumbersCases {

    public static volatile Object store0 = new Object();
    public static volatile Object store1 = new Object();

    public static int primitiveIntSum(int v1, int v2) {
        return v1 + v2;
    }

    public static double primitiveDoubleSum(double v1, double v2) {
        return v1 + v2;
    }

    public static Integer boxedIntSum(Integer v1, Integer v2) {
        store0 = v1;
        store1 = v2;
        return v1 + v2;
    }

    public static Double boxedDoubleSum(Double v1, Double v2) {
        store0 = v1;
        store1 = v2;
        return v1 + v2;
    }

    public static void main(String[] args) {
        SafeCaller.call(() -> new BoxedNumbersCases().boxedIntSum(Integer.valueOf(-234), Integer.valueOf(23)));
        SafeCaller.call(() -> new BoxedNumbersCases().boxedDoubleSum(Double.valueOf(-5434.23), Double.valueOf(321.2453)));
        SafeCaller.call(() -> new BoxedNumbersCases().primitiveDoubleSum(Double.valueOf(-5434.23), Double.valueOf(321.2453)));
        SafeCaller.call(() -> new BoxedNumbersCases().primitiveIntSum(-234, 23));
    }
}
