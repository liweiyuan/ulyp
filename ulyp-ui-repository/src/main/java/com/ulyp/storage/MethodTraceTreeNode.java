package com.ulyp.storage;

import com.ulyp.transport.BooleanType;
import com.ulyp.transport.TMethodDescriptionDecoder;

import java.util.ArrayList;
import java.util.List;

public class MethodTraceTreeNode {

    private long id;
    private String className;
    private String methodName;
    private boolean isVoidMethod;
    private List<ObjectValue> args;
    private List<String> parameterNames;
    private ObjectValue returnValue;
    private boolean thrown;
    private List<MethodTraceTreeNode> children;
    private int subtreeNodeCount;

    public MethodTraceTreeNode(
            List<ObjectValue> args,
            ObjectValue returnValue,
            boolean thrown,
            TMethodDescriptionDecoder methodDescription,
            List<MethodTraceTreeNode> children)
    {
        this.isVoidMethod = methodDescription.returnsSomething() == BooleanType.F;
        this.args = args;
        this.returnValue = returnValue;
        this.thrown = thrown;
        int originalLimit = methodDescription.limit();
        TMethodDescriptionDecoder.ParameterNamesDecoder paramNamesDecoder = methodDescription.parameterNames();
        this.parameterNames = new ArrayList<>();
        while (paramNamesDecoder.hasNext()) {
            this.parameterNames.add(paramNamesDecoder.next().value());
        }
        this.className = methodDescription.className();
        this.methodName = methodDescription.methodName();
        methodDescription.limit(originalLimit);
        this.children = children;
        this.subtreeNodeCount = children.stream().map(MethodTraceTreeNode::getSubtreeNodeCount).reduce(1, Integer::sum);
    }

    public long getId() {
        return id;
    }

    public int getSubtreeNodeCount() {
        return subtreeNodeCount;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public List<ObjectValue> getArgs() {
        return args;
    }

    public List<String> getParameterNames() {
        return parameterNames;
    }

    public ObjectValue getReturnValue() {
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

    /**
     * @return either printed return value, or printed throwable if something was thrown
     */
    public String getResult() {
        return (isVoidMethod && !hasThrown()) ? "void" : returnValue.getPrintedText();
    }

    public String toString() {
        return getResult() + " : " +
                className +
                "." +
                methodName +
                args;
    }
}
