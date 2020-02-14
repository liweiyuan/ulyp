package com.ulyp.ui.util;

import com.ulyp.transport.TMethodEnterTrace;
import com.ulyp.transport.TMethodExitTrace;
import com.ulyp.transport.TMethodInfo;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public SearchIndex getSearchIndex() {
        Set<String> words = new HashSet<>(methodEnterTrace.getArgsList());
        if (methodInfo.getReturnsSomething() && !methodExitTrace.getReturnValue().isEmpty()) {
            words.add(methodExitTrace.getReturnValue());
        }
        words.add(methodInfo.getMethodName());
        words.add(StringUtils.toSimpleName(methodInfo.getClassName()));
        words.add(methodInfo.getClassName());
        return new HashSetIndex(words);
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
