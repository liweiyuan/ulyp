package com.ulyp.storage;

import com.ulyp.transport.BooleanType;
import com.ulyp.transport.TMethodDescriptionDecoder;

import java.util.List;

public class MethodTraceTreeNode {

    private long id;
    private String className;
    private String methodName;
    private boolean isVoidMethod;
    private List<String> args;
    private String returnValue;
    private boolean thrown;
    private List<MethodTraceTreeNode> children;
    private int nodeCount;

    public MethodTraceTreeNode(
            List<String> args,
            String returnValue,
            boolean thrown,
            TMethodDescriptionDecoder methodDescription,
            List<MethodTraceTreeNode> children,
            int nodeCount)
    {
        this.isVoidMethod = methodDescription.returnsSomething() == BooleanType.F;
        this.args = args;
        this.returnValue = returnValue;
        this.thrown = thrown;
        int originalLimit = methodDescription.limit();
        this.className = methodDescription.className();
        this.methodName = methodDescription.methodName();
        methodDescription.limit(originalLimit);
        this.children = children;
        this.nodeCount = nodeCount;
    }

    public long getId() {
        return id;
    }

    public int getNodeCount() {
        return nodeCount;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public List<String> getArgs() {
        return args;
    }

    public String getReturnValue() {
        return returnValue;
    }

    public boolean hasThrown() {
        return thrown;
    }

    public List<MethodTraceTreeNode> getChildren() {
        return children;
    }

    public MethodTraceTreeNode setId(long id) {
        this.id = id;
        return this;
    }

    public MethodTraceTreeNode setClassName(String className) {
        this.className = className;
        return this;
    }

    public MethodTraceTreeNode setMethodName(String methodName) {
        this.methodName = methodName;
        return this;
    }

    public MethodTraceTreeNode setVoidMethod(boolean voidMethod) {
        isVoidMethod = voidMethod;
        return this;
    }

    public MethodTraceTreeNode setArgs(List<String> args) {
        this.args = args;
        return this;
    }

    public MethodTraceTreeNode setReturnValue(String returnValue) {
        this.returnValue = returnValue;
        return this;
    }

    public MethodTraceTreeNode setThrown(boolean thrown) {
        this.thrown = thrown;
        return this;
    }

    public MethodTraceTreeNode setChildren(List<MethodTraceTreeNode> children) {
        this.children = children;
        return this;
    }

    public MethodTraceTreeNode setNodeCount(int nodeCount) {
        this.nodeCount = nodeCount;
        return this;
    }

    /**
     * @return either printed return value, or printed throwable if something was thrown
     */
    public String getResult() {
        return (isVoidMethod && !hasThrown()) ? "void" : returnValue;
    }

    public String toString() {
        return getResult() + " : " +
                className +
                "." +
                methodName +
                args;
    }
}
