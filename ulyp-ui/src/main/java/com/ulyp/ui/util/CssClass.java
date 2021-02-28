package com.ulyp.ui.util;

public enum CssClass {

    CALL_TREE_PLAIN_TEXT("ulyp-ctt-sep"),
    CALL_TREE_IDENTITY_REPR("ulyp-ctt-identity");

    private final String className;

    CssClass(String cssName) {
        this.className = cssName;
    }

    public String getName() {
        return className;
    }
}
