package com.ulyp.agent.transport;

import com.ulyp.transport.TMethodEnterTrace;
import com.ulyp.transport.TMethodExitTrace;
import com.ulyp.transport.TMethodInfo;

import java.util.Collections;
import java.util.List;

public class MethodTraceTreeNode {

    private final TMethodEnterTrace methodEnterTrace;
    private final TMethodExitTrace methodExitTrace;
    private final TMethodInfo methodInfo;
    private final List<MethodTraceTreeNode> children;
    private final int nodeCount;

    MethodTraceTreeNode(
            TMethodEnterTrace methodEnterTrace,
            TMethodExitTrace methodExitTrace,
            TMethodInfo methodInfo,
            List<MethodTraceTreeNode> children,
            int nodeCount)
    {
        this.methodEnterTrace = methodEnterTrace;
        this.methodExitTrace = methodExitTrace;
        this.methodInfo = methodInfo;
        this.children = Collections.unmodifiableList(children);
        this.nodeCount = nodeCount;
    }

    public int getNodeCount() {
        return nodeCount;
    }

    public TMethodInfo getMethodInfo() {
        return methodInfo;
    }

    public TMethodEnterTrace getMethodEnterTrace() {
        return methodEnterTrace;
    }

    public TMethodExitTrace getMethodExitTrace() {
        return methodExitTrace;
    }

    public List<MethodTraceTreeNode> getChildren() {
        return children;
    }

    /**
     * @return either printed return value, or printed throwable if something was thrown
     */
    public String getResult() {
        if (!methodExitTrace.getThrown().isEmpty()) {
            return methodExitTrace.getThrown();
        } else {
            return methodInfo.getReturnsSomething() && !methodExitTrace.getReturnValue().isEmpty()
                    ? methodExitTrace.getReturnValue()
                    : "void";
        }
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getResult()).append(" : ");
        builder.append(methodInfo.getClassName())
                .append(".")
                .append(methodInfo.getMethodName());
        builder.append(methodEnterTrace.getArgsList());
        return builder.toString();
    }
}
