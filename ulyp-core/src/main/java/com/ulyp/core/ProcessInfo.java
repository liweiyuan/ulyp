package com.ulyp.core;

public class ProcessInfo {

    private final String mainClassName;

    public ProcessInfo(String mainClassName) {
        this.mainClassName = mainClassName;
    }

    public String getMainClassName() {
        return mainClassName;
    }
}
