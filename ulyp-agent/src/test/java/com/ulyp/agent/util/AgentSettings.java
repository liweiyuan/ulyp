package com.ulyp.agent.util;

public class AgentSettings {
    private Class<?> mainClassName;
    private String packages;
    private String startMethod;
    private int maxDepth = Integer.MAX_VALUE;

    public Class<?> getMainClassName() {
        return mainClassName;
    }

    public String getPackages() {
        return packages;
    }

    public String getStartMethod() {
        return startMethod;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public AgentSettings setPackages(String packages) {
        this.packages = packages;
        return this;
    }

    public AgentSettings setStartMethod(String startMethod) {
        this.startMethod = startMethod;
        return this;
    }

    public AgentSettings setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
        return this;
    }

    public AgentSettings setMainClassName(Class<?> mainClassName) {
        this.mainClassName = mainClassName;
        return this;
    }
}
