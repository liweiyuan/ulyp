package com.ulyp.core;

import com.ulyp.core.printers.ObjectRepresentation;
import com.ulyp.transport.BooleanType;
import com.ulyp.transport.TMethodDescriptionDecoder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CallRecord {

    private long id;
    private final String className;
    private final String methodName;
    private final boolean isVoidMethod;
    private final ObjectRepresentation callee;
    private final ObjectRepresentation returnValue;
    private final List<ObjectRepresentation> args;
    private final List<String> parameterNames;
    private final boolean thrown;
    private final List<CallRecord> children;
    private final int subtreeNodeCount;

    public CallRecord(
            ObjectRepresentation callee,
            List<ObjectRepresentation> args,
            ObjectRepresentation returnValue,
            boolean thrown,
            TMethodDescriptionDecoder methodDescription,
            List<CallRecord> children)
    {
        this.callee = callee;
        this.isVoidMethod = methodDescription.returnsSomething() == BooleanType.F;
        this.args = new ArrayList<>(args);
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

        this.children = new ArrayList<>(children);
        this.subtreeNodeCount = children.stream().map(CallRecord::getSubtreeNodeCount).reduce(1, Integer::sum);
    }

    public ObjectRepresentation getCallee() {
        return callee;
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

    public List<ObjectRepresentation> getArgs() {
        return args;
    }

    public List<String> getArgTexts() {
        return args.stream().map(ObjectRepresentation::getPrintedText).collect(Collectors.toList());
    }

    public List<String> getParameterNames() {
        return parameterNames;
    }

    public ObjectRepresentation getReturnValue() {
        return returnValue;
    }

    public boolean hasThrown() {
        return thrown;
    }

    public List<CallRecord> getChildren() {
        return children;
    }

    public CallRecord setId(long id) {
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
