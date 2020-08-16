package com.ulyp.core;

public class CallRecordTree {

    private final CallRecord root;

    public CallRecordTree(CallRecord root) {
        this.root = root;
    }

    public CallRecord getRoot() {
        return root;
    }
}
