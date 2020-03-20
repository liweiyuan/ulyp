package com.ulyp.agent.transport;

import com.ulyp.transport.BooleanType;
import com.ulyp.transport.SMethodDescriptionDecoder;
import com.ulyp.transport.TMethodInfo;

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
    private final int nodeCount;

    MethodTraceTreeNode(
            List<String> args,
            String returnValue,
            boolean thrown,
            SMethodDescriptionDecoder methodDescription,
            List<MethodTraceTreeNode> children,
            int nodeCount)
    {
        this.isVoidMethod = methodDescription.returnsSomething() == BooleanType.F;
        this.args = args;
        this.returnValue = returnValue;
        this.thrown = thrown;
        this.className = methodDescription.className();
        this.methodName = methodDescription.methodName();

        this.children = Collections.unmodifiableList(children);
        this.nodeCount = nodeCount;
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
