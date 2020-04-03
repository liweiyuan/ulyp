package com.test.cases;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("all")
public class CollectionTestCases {

    private static volatile Object store0;
    private static volatile Object store1;

    public void acceptsListOfStringsWithSize0(List<String> list) {
        store0 = list;
    }

    public void acceptsListOfStringsWithSize1(List<String> list) {
        store0 = list;
    }

    public static void main(String[] args) {
        SafeCaller.call(() -> new CollectionTestCases().acceptsListOfStringsWithSize0(Collections.emptyList()));
        SafeCaller.call(() -> new CollectionTestCases().acceptsListOfStringsWithSize1(Arrays.asList("a")));
    }
}
