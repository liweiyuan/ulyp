package com.ulyp.agent.tests;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@SuppressWarnings("all")
public class AtomicNumbersTestCases {

    public static volatile Object store0 = new Object();
    public static volatile Object store1 = new Object();

    public static AtomicInteger intSum(AtomicInteger v1, AtomicInteger v2) {
        store0 = v1;
        store1 = v2;
        return new AtomicInteger(v1.get() + v2.get());
    }

    public static AtomicLong longSum(AtomicLong v1, AtomicLong v2) {
        store0 = v1;
        store1 = v2;
        return new AtomicLong(v1.get() + v2.get());
    }

    public static void main(String[] args) {
        SafeCaller.call(() -> new AtomicNumbersTestCases().intSum(new AtomicInteger(-234), new AtomicInteger(23)));
        SafeCaller.call(() -> new AtomicNumbersTestCases().longSum(new AtomicLong(-234), new AtomicLong(23)));

    }
}
