package com.test.cases;

@SuppressWarnings("all")
public class LotsOfCallsTestCases {

    private static volatile int calls = 1000;
    private static volatile int value;

    public void level0() {
        for (int i = 0; i < 10; i++) {
            level1();
        }
    }

    public void level1() {
        for (int i = 0; i < 10; i++) {
            level2();
        }
    }

    public void level2() {
        for (int i = 0; i < 10; i++) {
            make1000Calls();
        }
    }

    public void make1000CallsLevel0() {
        make1000Calls();
    }

    public void make1000Calls() {
        for (int i = 0; i < calls; i++) {
            value = subCall();
        }
    }

    private int subCall() {
        return 2;
    }

    public static void main(String[] args) {
        SafeCaller.call(() -> new LotsOfCallsTestCases().make1000CallsLevel0());
        SafeCaller.call(() -> new LotsOfCallsTestCases().level0());
    }
}
