package com.ulyp.core;

public class CallTraceTree {

    private final CallTrace root;

    public CallTraceTree(CallTrace root) {
        this.root = root;
    }

    public CallTrace getRoot() {
        return root;
    }
}
