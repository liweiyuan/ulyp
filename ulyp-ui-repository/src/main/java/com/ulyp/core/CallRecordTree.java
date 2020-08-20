package com.ulyp.core;

public class CallRecordTree {

    private final CallRecord root;
    private final String threadName;

    public CallRecordTree(CallRecord root) {
        this.root = root;
        threadName = "123";
    }

    public CallRecord getRoot() {
        return root;
    }
}
