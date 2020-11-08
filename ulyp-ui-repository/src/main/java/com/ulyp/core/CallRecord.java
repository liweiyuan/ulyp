package com.ulyp.core;

import com.ulyp.core.printers.NotRecordedObjectRepresentation;
import com.ulyp.core.printers.ObjectRepresentation;
import com.ulyp.transport.BooleanType;
import com.ulyp.transport.TMethodInfoDecoder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CallRecord {

    private final long id;
    private final String className;

    // TODO move this group to method class
    private final String methodName;
    private final boolean isVoidMethod;
    private final boolean isStatic;
    private final List<String> parameterNames;

    private final ObjectRepresentation callee;
    private final List<ObjectRepresentation> args;

    private ObjectRepresentation returnValue = NotRecordedObjectRepresentation.getInstance();
    private boolean thrown;

    private final CallRecordDatabase database;
    private int subtreeNodeCount;

    public CallRecord(
            long id,
            ObjectRepresentation callee,
            List<ObjectRepresentation> args,
            TMethodInfoDecoder methodDescription,
            CallRecordDatabase database,
            int subtreeNodeCount)
    {
        this.id = id;
        this.callee = callee;
        this.isVoidMethod = methodDescription.returnsSomething() == BooleanType.F;
        this.isStatic = methodDescription.staticFlag() == BooleanType.T;
        this.args = new ArrayList<>(args);
        int originalLimit = methodDescription.limit();

        TMethodInfoDecoder.ParameterNamesDecoder paramNamesDecoder = methodDescription.parameterNames();
        this.parameterNames = new ArrayList<>();
        while (paramNamesDecoder.hasNext()) {
            this.parameterNames.add(paramNamesDecoder.next().value());
        }
        this.className = methodDescription.className();
        this.methodName = methodDescription.methodName();
        methodDescription.limit(originalLimit);

        this.database = database;
        this.subtreeNodeCount = subtreeNodeCount;
    }

    public void forEach(Consumer<CallRecord> recordConsumer) {
        recordConsumer.accept(this);

        for (CallRecord child : getChildren()) {
            child.forEach(recordConsumer);
        }
    }

    public ObjectRepresentation getCallee() {
        return callee;
    }

    public long getId() {
        return id;
    }

    public boolean isVoidMethod() {
        return isVoidMethod;
    }

    public boolean isStatic() {
        return isStatic;
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
        return database.getChildren(this.id);
    }

    public CallRecord setSubtreeNodeCount(int subtreeNodeCount) {
        this.subtreeNodeCount = subtreeNodeCount;
        return this;
    }

    public CallRecord setReturnValue(ObjectRepresentation returnValue) {
        this.returnValue = returnValue;
        return this;
    }

    public CallRecord setThrown(boolean thrown) {
        this.thrown = thrown;
        return this;
    }

    public String toString() {
        return getReturnValue() + " : " +
                className +
                "." +
                methodName +
                args;
    }

    public boolean isComplete() {
        return returnValue != NotRecordedObjectRepresentation.getInstance();
    }
}
