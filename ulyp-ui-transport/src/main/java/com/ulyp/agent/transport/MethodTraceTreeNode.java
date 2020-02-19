package com.ulyp.agent.transport;

import com.ulyp.transport.TMethodEnterTrace;
import com.ulyp.transport.TMethodExitTrace;
import com.ulyp.transport.TMethodInfo;

import java.util.Collections;
import java.util.List;

public class MethodTraceTreeNode {

    private final String className;
    private final String methodName;
    private final boolean isVoidMethod;
    private final List<String> args;
    private final String returnValue;
    private final String thrownValue;
    private final List<MethodTraceTreeNode> children;
    private final int nodeCount;

    MethodTraceTreeNode(
            TMethodEnterTrace methodEnterTrace,
            TMethodExitTrace methodExitTrace,
            TMethodInfo methodInfo,
            List<MethodTraceTreeNode> children,
            int nodeCount)
    {
        this.isVoidMethod = !methodInfo.getReturnsSomething();
        this.args = methodEnterTrace.getArgsList();
        this.returnValue = methodExitTrace.getReturnValue();
        this.thrownValue = methodExitTrace.getThrown();
        this.className = methodInfo.getClassName();
        this.methodName = methodInfo.getMethodName();

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

    public String getThrownValue() {
        return thrownValue;
    }

    public List<MethodTraceTreeNode> getChildren() {
        return children;
    }

    /**
     * @return either printed return value, or printed throwable if something was thrown
     */
    public String getResult() {
        if (!thrownValue.isEmpty()) {
            return thrownValue;
        } else {
            return isVoidMethod ? "void" : returnValue;
        }
    }

    public String toString() {
        return getResult() + " : " +
                className +
                "." +
                methodName +
                args;
    }
}
