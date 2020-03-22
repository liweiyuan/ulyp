package com.ulyp.agent.util;

import com.ulyp.transport.BooleanType;
import com.ulyp.transport.TMethodDescriptionDecoder;

import java.util.Collections;
import java.util.List;

public class MethodTraceTreeNode {

    private final String className;
    private final String methodName;
    private final boolean isVoidMethod;
    private final List<String> args;
    private final String returnValue;
    private final boolean thrown;
    private final List<MethodTraceTreeNode> children;

    public MethodTraceTreeNode(
            List<String> args,
            String returnValue,
            boolean thrown,
            TMethodDescriptionDecoder methodDescription,
            List<MethodTraceTreeNode> children)
    {
        this.isVoidMethod = methodDescription.returnsSomething() == BooleanType.F;
        this.args = args;
        this.returnValue = returnValue;
        this.thrown = thrown;
        int tmpLimit = methodDescription.limit();
        this.className = methodDescription.className();
        this.methodName = methodDescription.methodName();
        methodDescription.limit(tmpLimit);
        this.children = children != null ? children : Collections.emptyList();
    }

    public int getSubtreeNodeCount() {
        return 1 + children.stream().map(MethodTraceTreeNode::getSubtreeNodeCount).reduce(0, Integer::sum);
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
